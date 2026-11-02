#!/usr/bin/env bash
set -euo pipefail

apk="${1:?usage: release-smoke.sh <apk>}"
package="${PACKAGE:-io.github.eastwoodno1.supersnake}"
timeout_seconds="${SMOKE_TIMEOUT_SECONDS:-120}"
out_dir="${OUT_DIR:-build/release-smoke}"

mkdir -p "$out_dir"
: > "$out_dir/ui-dump.xml"
: > "$out_dir/logcat.txt"

collect_evidence() {
  adb logcat -d > "$out_dir/logcat.txt" 2>/dev/null || true
  adb exec-out screencap -p > "$out_dir/game-over.png" 2>/dev/null || true
}
trap collect_evidence EXIT

dump_ui() {
  adb shell uiautomator dump /sdcard/ui-dump.xml > /dev/null 2>&1 || return 1
  adb shell cat /sdcard/ui-dump.xml > "$out_dir/ui-dump.xml.next" 2>/dev/null || return 1
  mv "$out_dir/ui-dump.xml.next" "$out_dir/ui-dump.xml"
}

play_bounds() {
  { grep -o '<node [^>]* text="Play"[^>]*>' "$out_dir/ui-dump.xml" || true; } |
    head -n 1 |
    sed -n 's/.*bounds="\[\([0-9]*\),\([0-9]*\)\]\[\([0-9]*\),\([0-9]*\)\]".*/\1 \2 \3 \4/p'
}

adb wait-for-device
adb uninstall "$package" > /dev/null 2>&1 || true
adb install "$apk" > /dev/null
adb logcat -c
adb shell am start -W -n "$package/.MainActivity" > /dev/null

taps=0
reached=0
deadline=$((SECONDS + timeout_seconds))
while [ "$SECONDS" -lt "$deadline" ]; do
  if dump_ui; then
    if [ "$taps" -eq 0 ]; then
      bounds="$(play_bounds)"
      if [ -n "$bounds" ]; then
        read -r x1 y1 x2 y2 <<< "$bounds"
        adb shell input tap $(((x1 + x2) / 2)) $(((y1 + y2) / 2))
        taps=1
      fi
    elif grep -qF 'Game over. Score' "$out_dir/ui-dump.xml"; then
      reached=1
      break
    fi
  fi
  sleep 1
done

adb logcat -d > "$out_dir/logcat.txt"

if [ "$taps" -eq 0 ]; then
  echo "smoke failed: no Play node found, zero interactions" >&2
  exit 1
fi
if [ "$reached" -eq 0 ]; then
  echo "smoke failed: no game over within ${timeout_seconds}s" >&2
  exit 1
fi
if grep -qF 'FATAL EXCEPTION' "$out_dir/logcat.txt"; then
  echo "smoke failed: FATAL EXCEPTION in logcat" >&2
  exit 1
fi
echo "smoke passed: game over reached, no FATAL EXCEPTION"
