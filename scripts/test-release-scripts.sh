#!/usr/bin/env bash
set -uo pipefail

unsigned_apk="${1:?usage: test-release-scripts.sh <unsigned.apk>}"
here="$(cd "$(dirname "$0")" && pwd)"
[ -f "$unsigned_apk" ] || { echo "apk not found: $unsigned_apk" >&2; exit 1; }
unsigned_apk="$(cd "$(dirname "$unsigned_apk")" && pwd)/$(basename "$unsigned_apk")"
work="$(mktemp -d "${RUNNER_TEMP:-${TMPDIR:-/tmp}}/release-script-tests.XXXXXX")"
trap 'rm -rf "$work"' EXIT
[ -n "${ANDROID_HOME:-}" ] || { echo "ANDROID_HOME is not set" >&2; exit 1; }
apksigner="${APKSIGNER:-$ANDROID_HOME/build-tools/37.0.0/apksigner}"
[ -x "$apksigner" ] || { echo "apksigner not found: $apksigner" >&2; exit 1; }

fail() {
  echo "assertion failed: $*" >&2
  exit 1
}

need() {
  for script in "$@"; do
    [ -x "$here/$script" ] || fail "missing or not executable: scripts/$script"
  done
}

expect_status() {
  local want="$1" got="$2" what="$3"
  [ "$got" -eq "$want" ] || fail "$what: exit $got, wanted $want"
}

expect_nonzero() {
  [ "$1" -ne 0 ] || fail "$2: exit 0, wanted non-zero"
}

expect_in() {
  grep -qF -- "$2" "$1" || fail "$1 lacks: $2"
}

expect_not_in() {
  if grep -qF -- "$2" "$1"; then
    fail "$1 contains: $2"
  fi
}

expect_reached_launch() {
  local calls="$1/calls.log"
  expect_in "$calls" "install "
  expect_in "$calls" "logcat -c"
  expect_in "$calls" "shell am start"
  expect_in "$calls" "logcat -d"
}

make_keystore() {
  local dir="$1" pass="$2" alias="$3"
  KT_PASS="$pass" keytool -genkeypair -keystore "$dir/ks.p12" -storetype PKCS12 \
    -alias "$alias" -keyalg RSA -keysize 2048 -validity 2 -dname "CN=test" \
    -storepass:env KT_PASS -keypass:env KT_PASS > /dev/null 2>&1 || fail "keytool could not make a keystore"
}

fake_adb() {
  local dir="$1"
  mkdir -p "$dir/bin"
  : > "$dir/taps.log"
  : > "$dir/calls.log"
  printf '#!%s\n' "/usr/bin/env bash" > "$dir/bin/adb"
  cat >> "$dir/bin/adb" <<'FAKE'
dir="$FAKE_DIR"
echo "$*" >> "$dir/calls.log"
case "$*" in
  install*|uninstall*|wait-for-device|"logcat -c"|"shell am start"*|"shell uiautomator dump"*) exit 0 ;;
  "logcat -d")
    cat "$dir/logcat.txt"
    if [ -s "$dir/over.shown" ] && [ -f "$dir/late-logcat.txt" ] && [ $(($(date +%s) - $(cat "$dir/over.shown"))) -ge 2 ]; then
      cat "$dir/late-logcat.txt"
    fi
    ;;
  "shell cat "*)
    taps_done=$(($(wc -l < "$dir/taps.log")))
    if [ -f "$dir/dump-$taps_done.xml" ]; then
      cat "$dir/dump-$taps_done.xml"
    elif [ -s "$dir/taps.log" ] && [ -f "$dir/dump-after.xml" ]; then
      [ -s "$dir/over.shown" ] || date +%s > "$dir/over.shown"
      cat "$dir/dump-after.xml"
    else
      cat "$dir/dump.xml"
    fi
    ;;
  "shell input tap "*) echo "$*" >> "$dir/taps.log" ;;
  "exec-out screencap"*) printf 'png' ;;
  *) echo "unexpected adb call: $*" >&2; exit 99 ;;
esac
FAKE
  chmod +x "$dir/bin/adb"
  : > "$dir/logcat.txt"
  printf '<hierarchy><node index="0" text="Snake" bounds="[0,0][10,10]" /></hierarchy>' > "$dir/dump.xml"
}

play_dump='<hierarchy><node index="0" text="Play" bounds="[100,200][300,400]" /></hierarchy>'
over_dump='<hierarchy><node index="0" text="Game over. Score 3" bounds="[0,0][10,10]" /></hierarchy>'

run_smoke() {
  local dir="$1"
  FAKE_DIR="$dir" PATH="$dir/bin:$PATH" OUT_DIR="$dir/out" SMOKE_TIMEOUT_SECONDS="${SMOKE_TIMEOUT_SECONDS:-2}" SMOKE_SETTLE_SECONDS="${SMOKE_SETTLE_SECONDS:-0}" \
    "$here/release-smoke.sh" "$unsigned_apk" > "$dir/stdout.txt" 2> "$dir/stderr.txt"
}

signedApkVerifies() {
  need sign-throwaway.sh
  local rc=0
  "$here/sign-throwaway.sh" "$unsigned_apk" "$work/signed.apk" > /dev/null 2>&1 || rc=$?
  expect_status 0 "$rc" "sign-throwaway"
  rc=0
  local out
  out="$("$apksigner" verify -v "$work/signed.apk" 2>&1)" || rc=$?
  expect_status 0 "$rc" "apksigner verify"
  case "$out" in *Verifies*) ;; *) fail "verify output lacks Verifies: $out" ;; esac
}

wrongAliasFails() {
  need sign-apk.sh
  local d="$work/wrongalias"
  mkdir -p "$d"
  make_keystore "$d" pw-wrong-alias right
  local rc=0
  KS_FILE="$d/ks.p12" KS_PASS=pw-wrong-alias KEY_ALIAS=other KEY_PASS=pw-wrong-alias \
    "$here/sign-apk.sh" "$unsigned_apk" "$d/out.apk" > /dev/null 2> "$d/err.txt" || rc=$?
  expect_nonzero "$rc" "wrong alias"
  expect_in "$d/err.txt" 'entry "other" does not contain a key'
  expect_not_in "$d/err.txt" ANDROID_HOME
  [ ! -e "$d/out.apk" ] || fail "an output apk exists after a failed signing"
}

missingKeystoreFails() {
  need sign-apk.sh
  local d="$work/missingks"
  mkdir -p "$d"
  local rc=0
  KS_FILE="$d/none.p12" KS_PASS=pw KEY_ALIAS=a KEY_PASS=pw \
    "$here/sign-apk.sh" "$unsigned_apk" "$d/out.apk" > /dev/null 2> "$d/err.txt" || rc=$?
  expect_nonzero "$rc" "missing keystore"
  expect_in "$d/err.txt" "keystore file not found"
  expect_not_in "$d/err.txt" ANDROID_HOME
  [ ! -e "$d/out.apk" ] || fail "an output apk exists after a failed signing"
}

signApkNeverPrintsSecrets() {
  need sign-apk.sh
  local d="$work/secrets"
  mkdir -p "$d"
  make_keystore "$d" topsecretpass right
  local rc=0
  KS_FILE="$d/ks.p12" KS_PASS=topsecretpass KEY_ALIAS=right KEY_PASS=topsecretpass \
    "$here/sign-apk.sh" "$unsigned_apk" "$d/ok.apk" > "$d/ok.txt" 2>&1 || rc=$?
  expect_status 0 "$rc" "passing sign run"
  rc=0
  KS_FILE="$d/ks.p12" KS_PASS=topsecretpass KEY_ALIAS=other KEY_PASS=topsecretpass \
    "$here/sign-apk.sh" "$unsigned_apk" "$d/bad.apk" > "$d/bad.txt" 2>&1 || rc=$?
  expect_nonzero "$rc" "failing sign run"
  expect_in "$d/bad.txt" 'entry "other" does not contain a key'
  expect_not_in "$d/bad.txt" ANDROID_HOME
  [ -s "$d/ok.txt" ] || fail "the passing run printed nothing, so the check proves nothing"
  [ -s "$d/bad.txt" ] || fail "the failing run printed nothing, so the check proves nothing"
  if grep -q topsecretpass "$d/ok.txt" "$d/bad.txt"; then
    fail "a secret reached the output"
  fi
}

noPlayButtonFailsTheSmoke() {
  need release-smoke.sh
  local d="$work/noplay"
  fake_adb "$d"
  local rc=0
  run_smoke "$d" || rc=$?
  expect_nonzero "$rc" "no Play node"
  expect_in "$d/stderr.txt" "smoke failed: no Play node found"
  expect_reached_launch "$d"
  [ ! -s "$d/taps.log" ] || fail "a tap was recorded without a Play node"
}

missingGameOverFailsTheSmoke() {
  need release-smoke.sh
  local d="$work/nogameover"
  fake_adb "$d"
  printf '%s' "$play_dump" > "$d/dump.xml"
  local rc=0
  run_smoke "$d" || rc=$?
  expect_nonzero "$rc" "no game over"
  expect_in "$d/stderr.txt" "smoke failed: no game over"
  expect_reached_launch "$d"
  [ "$(wc -l < "$d/taps.log")" -eq 1 ] || fail "expected exactly one tap"
}

fatalExceptionFailsTheSmoke() {
  need release-smoke.sh
  local d="$work/fatal"
  fake_adb "$d"
  printf '%s' "$play_dump" > "$d/dump.xml"
  printf '%s' "$over_dump" > "$d/dump-after.xml"
  printf '11-02 10:00:00.000 E AndroidRuntime: FATAL EXCEPTION: main\n' > "$d/logcat.txt"
  local rc=0
  SMOKE_TIMEOUT_SECONDS=10 run_smoke "$d" || rc=$?
  expect_nonzero "$rc" "FATAL EXCEPTION in logcat"
  expect_in "$d/stderr.txt" "smoke failed: FATAL EXCEPTION"
  expect_reached_launch "$d"
}

smokePassesAndKeepsEvidence() {
  need release-smoke.sh
  local d="$work/pass"
  fake_adb "$d"
  printf '%s' "$play_dump" > "$d/dump.xml"
  printf '%s' "$over_dump" > "$d/dump-after.xml"
  local rc=0
  SMOKE_TIMEOUT_SECONDS=10 run_smoke "$d" || rc=$?
  expect_status 0 "$rc" "clean smoke"
  [ "$(wc -l < "$d/taps.log")" -eq 1 ] || fail "expected exactly one tap"
  grep -q 'input tap 200 300' "$d/taps.log" || fail "tap was not at the centre of the Play node"
  for file in ui-dump.xml logcat.txt game-over.png; do
    [ -f "$d/out/$file" ] || fail "missing evidence file $file"
  done
}

crashAfterGameOverFailsTheSmoke() {
  need release-smoke.sh
  local d="$work/lateCrash"
  fake_adb "$d"
  printf '%s' "$play_dump" > "$d/dump.xml"
  printf '%s' "$over_dump" > "$d/dump-after.xml"
  printf '11-02 10:00:05.000 E AndroidRuntime: FATAL EXCEPTION: main\n' > "$d/late-logcat.txt"
  local rc=0
  SMOKE_TIMEOUT_SECONDS=10 SMOKE_SETTLE_SECONDS=2 run_smoke "$d" || rc=$?
  expect_nonzero "$rc" "crash after game over"
  expect_in "$d/stderr.txt" "smoke failed: FATAL EXCEPTION"
  expect_reached_launch "$d"
}

ignoresPlayAgainAndContentDesc() {
  need release-smoke.sh
  local d="$work/lookalikes"
  fake_adb "$d"
  printf '%s' '<hierarchy><node index="0" text="Play again" bounds="[1,2][3,4]" /><node index="1" text="" content-desc="Play" bounds="[5,6][7,8]" /></hierarchy>' > "$d/dump.xml"
  local rc=0
  run_smoke "$d" || rc=$?
  expect_nonzero "$rc" "look-alike nodes"
  expect_in "$d/stderr.txt" "smoke failed: no Play node found"
  [ ! -s "$d/taps.log" ] || fail "a tap was recorded for a look-alike node"
  printf '%s' '<hierarchy><node index="0" text="Play again" bounds="[1,2][3,4]" /><node index="1" text="Play" bounds="[100,200][300,400]" /></hierarchy>' > "$d/dump.xml"
  rc=0
  run_smoke "$d" || rc=$?
  [ "$(wc -l < "$d/taps.log")" -eq 1 ] || fail "expected exactly one tap"
  grep -q 'input tap 200 300' "$d/taps.log" || fail "tapped something other than the Play node"
}

dismissesTheFullScreenConfirmation() {
  need release-smoke.sh
  local d="$work/immersive"
  fake_adb "$d"
  local overlay='<node index="0" text="Viewing full screen" package="android" bounds="[100,50][900,100]" /><node index="1" text="To exit, swipe down from the top." package="android" bounds="[100,100][900,150]" /><node index="2" text="Got it" resource-id="android:id/ok" package="android" bounds="[800,100][1000,200]" />'
  printf '<hierarchy>%s</hierarchy>' "$overlay" > "$d/dump.xml"
  printf '%s' "$play_dump" > "$d/dump-1.xml"
  printf '%s' "$over_dump" > "$d/dump-after.xml"
  local rc=0
  SMOKE_TIMEOUT_SECONDS=10 run_smoke "$d" || rc=$?
  expect_status 0 "$rc" "smoke behind the confirmation"
  [ "$(wc -l < "$d/taps.log")" -eq 2 ] || fail "expected two taps"
  sed -n 1p "$d/taps.log" | grep -q 'input tap 900 150' || fail "first tap was not on Got it"
  sed -n 2p "$d/taps.log" | grep -q 'input tap 200 300' || fail "second tap was not on Play"
}

dismissesTheConfirmationAfterPlay() {
  need release-smoke.sh
  local d="$work/immersiveLate"
  fake_adb "$d"
  printf '%s' "$play_dump" > "$d/dump.xml"
  printf '<hierarchy><node index="0" text="Got it" package="com.android.systemui" bounds="[800,100][1000,200]" /></hierarchy>' > "$d/dump-1.xml"
  printf '%s' "$over_dump" > "$d/dump-after.xml"
  local rc=0
  SMOKE_TIMEOUT_SECONDS=10 run_smoke "$d" || rc=$?
  expect_status 0 "$rc" "smoke with the confirmation after Play"
  [ "$(wc -l < "$d/taps.log")" -eq 2 ] || fail "expected two taps"
}

resumesAfterAnInterruption() {
  need release-smoke.sh
  local d="$work/interrupted"
  fake_adb "$d"
  printf '%s' "$play_dump" > "$d/dump.xml"
  printf '<hierarchy><node index="0" text="Paused" package="io.github.eastwoodno1.supersnake" bounds="[0,0][10,10]" /><node index="1" text="Resume" package="io.github.eastwoodno1.supersnake" bounds="[474,1082][608,1135]" /></hierarchy>' > "$d/dump-1.xml"
  printf '%s' "$over_dump" > "$d/dump-after.xml"
  local rc=0
  SMOKE_TIMEOUT_SECONDS=10 run_smoke "$d" || rc=$?
  expect_status 0 "$rc" "smoke after a system dialog paused the game"
  [ "$(wc -l < "$d/taps.log")" -eq 2 ] || fail "expected two taps"
  grep -qF 'shell input tap 541 1108' "$d/taps.log" || fail "did not tap Resume"
}

ignoresResumeFromOtherPackages() {
  need release-smoke.sh
  local d="$work/otherResume"
  fake_adb "$d"
  printf '%s' "$play_dump" > "$d/dump.xml"
  printf '<hierarchy><node index="0" text="Resume" package="some.other.app" bounds="[474,1082][608,1135]" /></hierarchy>' > "$d/dump-1.xml"
  local rc=0
  run_smoke "$d" || rc=$?
  expect_nonzero "$rc" "Resume from another package"
  [ "$(wc -l < "$d/taps.log")" -eq 1 ] || fail "tapped a Resume that is not the app's"
}

ignoresGotItFromOtherPackages() {
  need release-smoke.sh
  local d="$work/otherGotIt"
  fake_adb "$d"
  printf '<hierarchy><node index="0" text="Got it" package="some.other.app" bounds="[800,100][1000,200]" /></hierarchy>' > "$d/dump.xml"
  local rc=0
  run_smoke "$d" || rc=$?
  expect_nonzero "$rc" "Got it from another package"
  [ ! -s "$d/taps.log" ] || fail "tapped a Got it that is not from the system UI"
}

timeoutNamesTheLastDumpTexts() {
  need release-smoke.sh
  local d="$work/lastTexts"
  fake_adb "$d"
  printf '<hierarchy><node index="0" text="Viewing full screen" package="x" bounds="[0,0][1,1]" /><node index="1" text="Other" package="x" bounds="[0,0][1,1]" /></hierarchy>' > "$d/dump.xml"
  local rc=0
  run_smoke "$d" || rc=$?
  expect_nonzero "$rc" "no Play node"
  expect_in "$d/stderr.txt" "last dump texts: Viewing full screen, Other"
}

lastTextsKeepCommasInsideATextTogether() {
  need release-smoke.sh
  local d="$work/commaTexts"
  fake_adb "$d"
  printf '<hierarchy><node index="0" text="To exit, swipe down from the top." package="x" bounds="[0,0][1,1]" /><node index="1" text="Got it" package="x" bounds="[0,0][1,1]" /></hierarchy>' > "$d/dump.xml"
  local rc=0
  run_smoke "$d" || rc=$?
  expect_nonzero "$rc" "no Play node"
  expect_in "$d/stderr.txt" "last dump texts: To exit, swipe down from the top., Got it"
}

tests=(
  signedApkVerifies
  wrongAliasFails
  missingKeystoreFails
  signApkNeverPrintsSecrets
  noPlayButtonFailsTheSmoke
  missingGameOverFailsTheSmoke
  fatalExceptionFailsTheSmoke
  crashAfterGameOverFailsTheSmoke
  ignoresPlayAgainAndContentDesc
  dismissesTheFullScreenConfirmation
  dismissesTheConfirmationAfterPlay
  resumesAfterAnInterruption
  ignoresResumeFromOtherPackages
  ignoresGotItFromOtherPackages
  timeoutNamesTheLastDumpTexts
  lastTextsKeepCommasInsideATextTogether
  smokePassesAndKeepsEvidence
)

passed=0
failed=0
for name in "${tests[@]}"; do
  ( set -e; "$name" )
  rc=$?
  if [ "$rc" -eq 0 ]; then
    echo "PASS $name"
    passed=$((passed + 1))
  else
    echo "FAIL $name"
    failed=$((failed + 1))
  fi
done

echo "$passed passed"
if [ "$failed" -ne 0 ]; then
  echo "$failed failed" >&2
  exit 1
fi
