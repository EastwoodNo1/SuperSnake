# Releasing SuperSnake

This is the owner's runbook for the signing key and for every release. Design: [ADR-0005](adr/0005-signing-key.md) (one key for the app's whole life), [ADR-0011](adr/0011-version-code.md) (version code) and [ADR-0012](adr/0012-release-workflow.md) (the release workflow). Spec: section 7.2 and section 8.

No secret value belongs in a command line, a shell history, a chat, a PR or a log. Every step below that handles a secret reads it from a prompt, standard input or a file outside every checkout.

Commands were checked against the tools' own help on 2026-11-02: `keytool -help -genkeypair`, `keytool -help -exportcert`, `keytool -help -list`, `gh secret set --help`, `gh variable set --help`, `apksigner verify --help` (from `$ANDROID_HOME/build-tools/37.0.0`), `gh attestation verify --help`, `gh release verify --help`, `gh run rerun --help` and `openssl enc -help`.

## Create the key

Do this on a trusted machine, never in an agent session, and never inside a git checkout. The key is RSA 4096, PKCS12, valid for 36500 days (Android docs: "Your key should be valid for at least 25 years"). The tool prompts for the store password twice; use a long random one from a password manager. PKCS12 has one password, so the store password is also the key password.

```sh
mkdir -m 700 "$HOME/supersnake-release" && cd "$HOME/supersnake-release" && keytool -genkeypair -v -keystore release.p12 -storetype PKCS12 -alias supersnake -keyalg RSA -keysize 4096 -sigalg SHA384withRSA -validity 36500 -dname "CN=SuperSnake, O=EastwoodNo1"
```

Check it:

```sh
keytool -list -keystore "$HOME/supersnake-release/release.p12"
```

Expected: `Your keystore contains 1 entry` and `supersnake, ..., PrivateKeyEntry`.

## Back it up and test the restore

Make two encrypted copies and keep them in two separate places (for example an encrypted external drive and a password manager's file storage). The store password goes in the password manager, never next to the backup. `openssl enc` prompts for the backup passphrase.

```sh
cd "$HOME/supersnake-release"
openssl enc -aes-256-cbc -pbkdf2 -iter 600000 -salt -in release.p12 -out "$HOME/supersnake-backup.p12.enc"
```

The encrypted file is written outside the key directory. Copy it to the two backup locations, then delete the local `$HOME/supersnake-backup.p12.enc`.

This is AES-CBC, which has no integrity protection: a corrupted or tampered backup can decrypt to garbage without an error. The restore test below (`keytool -list` plus the fingerprint comparison) is what proves the backup is intact.

Tested with LibreSSL 3.3.6 (macOS `/usr/bin/openssl`). Other OpenSSL versions were not tested.

Test the restore into a scratch directory before trusting the backup, and compare the fingerprint with the live key (see the next section):

```sh
mkdir -m 700 "$HOME/supersnake-restore-test" && cd "$HOME/supersnake-restore-test"
openssl enc -d -aes-256-cbc -pbkdf2 -iter 600000 -in <path to the backup>/supersnake-backup.p12.enc -out release.p12
keytool -list -keystore release.p12
cd "$HOME" && rm -r "$HOME/supersnake-restore-test"
```

Expected: the alias `supersnake` and the same SHA-256 fingerprint as the original. Redo the test whenever a backup is moved or re-made.

## Compute the fingerprint

The signing certificate's SHA-256 is what `apksigner` prints and what the repository variable `RELEASE_CERT_SHA256` holds: 64 lowercase hex characters, no colons.

```sh
keytool -exportcert -alias supersnake -keystore "$HOME/supersnake-release/release.p12" | shasum -a 256 | cut -d' ' -f1
```

The prompt goes to the terminal and the certificate to the pipe. The result equals the `Certificate fingerprint (SHA-256)` line of `keytool -list`, lowercased and without colons. A fingerprint is public, not secret.

Proof on 2026-11-02, with a throwaway key made by the exact command above in a temporary directory that was deleted afterwards: the method printed `34bb9af5d873c262b2fab78ca09bb32c4e3bb8e2fab6cd4256f7e536dbde5805`, and `keytool -list` printed the same value as `34:BB:9A:F5:...:58:05`. That key is gone and is not the release key.

## Create the GitHub App

The release job needs an App token because PRs and tags made with `GITHUB_TOKEN` do not start other workflows (spec section 8). In GitHub, open Settings, Developer settings, GitHub Apps, New GitHub App:

- Name `SuperSnake Release`, or `SuperSnake Release EastwoodNo1` if the name is taken.
- Homepage URL `https://github.com/EastwoodNo1/SuperSnake`.
- Webhook: Active unchecked.
- Repository permissions: Contents Read and write, Pull requests Read and write, Metadata Read-only. Every other permission stays No access.
- No account permissions and no events.
- Where can this App be installed: Only on this account.

Then:

1. Generate a private key. GitHub downloads a `.pem` file. Move it to `<path outside any checkout>` with mode 600.
2. Install the App on the account and select only the repository SuperSnake.
3. Note the App's Client ID (it is not secret) and its slug (the last part of the App's public URL). Give the slug and nothing else to the agent session.

Do not add Issues: write unless a proof run shows label calls failing, and decide that with the owner first.

## Add the secrets and variables

Run each command from a terminal, never with the value after `--body`. `gh secret set` reads standard input or a prompt when `--body` is absent. Pass `--repo EastwoodNo1/SuperSnake` if the working directory is not a checkout of it.

The environments `release-please` and `release` must exist first (the settings script creates them).

```sh
base64 -i "$HOME/supersnake-release/release.p12" | tr -d '\n' | gh secret set RELEASE_KEYSTORE_BASE64 --env release
gh secret set RELEASE_KEYSTORE_PASSWORD --env release
gh secret set RELEASE_KEY_PASSWORD --env release
gh secret set RELEASE_KEY_ALIAS --env release
gh secret set RELEASE_APP_PRIVATE_KEY --env release-please < "<path outside any checkout>/<app>.pem"
gh variable set RELEASE_APP_CLIENT_ID
gh variable set RELEASE_CERT_SHA256
```

- `RELEASE_KEYSTORE_BASE64` is the keystore on one line (`base64 -i` is the macOS form; on Linux use `base64 -w0`).
- `RELEASE_KEYSTORE_PASSWORD` and `RELEASE_KEY_PASSWORD` are both the store password, because PKCS12 has one. Type it at the prompt each time.
- `RELEASE_KEY_ALIAS` is `supersnake`.
- `RELEASE_APP_PRIVATE_KEY` is the whole `.pem` file, with its BEGIN and END lines.
- `RELEASE_APP_CLIENT_ID` and `RELEASE_CERT_SHA256` are variables, entered at the prompt. The fingerprint is from the section above.

Read back the names (values are never shown): `gh secret list --env release`, `gh secret list --env release-please` and `gh variable list`.

## Cut a release

1. Merge PRs with Conventional Commit titles to `main`. release-please keeps one release PR open, labelled `autorelease: pending`, that bumps `version.txt` and `CHANGELOG.md`.
2. Merge the release PR when you want to ship. The `release` workflow run starts on `main`.
3. The run builds an unsigned APK, smoke-tests it, then waits. Open the run, review the pending deployment `sign-publish` (the `release` environment) and approve it. Nothing is signed until you do.
4. The job signs, uploads the APK and its checksum to the draft release, publishes it, and the last job verifies the published release.
5. Verify the download by hand, below.

A draft older than an hour in `gh release list --repo EastwoodNo1/SuperSnake` means the approval was not given or a job failed. That list is the only silence detector, so look at it after every merge of a release PR.

## Verify a downloaded APK

Replace `<v>` with the version, for example `1.0.0`. Use `apksigner` from the Android SDK build-tools (`$ANDROID_HOME/build-tools/<version>/apksigner`).

```sh
apksigner verify --print-certs SuperSnake-<v>.apk
sha256sum -c SuperSnake-<v>.apk.sha256
gh attestation verify SuperSnake-<v>.apk --repo EastwoodNo1/SuperSnake
gh release verify v<v> --repo EastwoodNo1/SuperSnake
```

- `apksigner` must print `Number of signers: 1` and a `certificate SHA-256 digest` equal to `RELEASE_CERT_SHA256`.
- `sha256sum -c` must print `SuperSnake-<v>.apk: OK`. Where `sha256sum` is missing, use `shasum -a 256 -c SuperSnake-<v>.apk.sha256`.
- `gh attestation verify` checks the build provenance attestation against this repository. Add `--predicate-type <type>` if the attestation was made with another predicate than the default `https://slsa.dev/provenance/v1`.
- `gh release verify` checks the release attestation GitHub makes for an immutable release.

To see it run, install it on a device or emulator with `adb install SuperSnake-<v>.apk` and play a round. A device that has not run the game before shows Android's system "Viewing full screen" confirmation once, on first launch, on API 31. Tap "Got it". The smoke script (`scripts/release-smoke.sh`) taps it on the emulator, so a person doing the same check by hand has to as well.

## Recovery

- **A job failed before the release was published.** Re-run only the failed jobs, in place: `gh run rerun <id> --failed --repo EastwoodNo1/SuperSnake`. If the tag exists but its draft has no assets, re-run the failed jobs the same way. GitHub allows re-runs for 30 days after the run.
- **Never re-run the whole run** (`gh run rerun <id>` without `--failed`). The `release-please` job would find the release already made, report `release_created` as false, and every later job would be skipped without an error.
- **The `release-please` job itself failed after it created the tag or the draft.** Its re-run cannot hand the tag to the later jobs, for the same reason. The version is burned: the `release-tags` ruleset lets only the App delete a `v*` tag. Ship the next patch: set `release-as` to it in `release-please-config.json` in the PR that fixes the cause.
- **The approval was rejected, or nobody approved it.** A rejected `sign-publish` fails the run; `gh run rerun <id> --failed` asks for approval again. A deployment that waits 30 days without approval fails the run, and by then the 30-day re-run window has closed too, so the version is burned: ship the next patch.
- **A stale draft** shows up in `gh release list`. Delete it only after asking; a draft is not covered by immutability, but its tag stays, so its version is burned.
- **A failure after publish** ships as the next patch version. Immutable releases lock the tag, and even after deleting such a release GitHub says "you cannot reuse the same tag name", so the version number is burned. Tags are never re-pointed or deleted by hand.
- **The approval request is missing.** The `release` environment allows only `main` and needs a reviewer; check the run's Deployments panel and `gh api repos/EastwoodNo1/SuperSnake/environments/release`.
- **The signature check fails.** The job fails closed on an empty, malformed or different fingerprint. Compare `RELEASE_CERT_SHA256` with the fingerprint of the key in `RELEASE_KEYSTORE_BASE64`; do not edit the check.

## Play App Signing migration

Done once, when Play publishing starts (a later plan). The GitHub key stays the app-signing key, as ADR-0005 says:

1. In the Play Console create the app and choose Play App Signing.
2. Upload this key as the app-signing key through the Console's "Use existing key" flow (the Console's own instructions give the export step; follow them on the day).
3. Create a separate upload key (a new PKCS12 file, made as in "Create the key" with another alias and another file), register its certificate as the upload key, and keep it outside GitHub's `release` secrets until the Play workflow needs it.
4. The AAB is signed with the upload key; Play re-signs what players install with the app-signing key, so players who installed from GitHub can update from Play.

## What losing the key means

Installed copies can never update, because Android accepts an update only when it is signed with the same key. The only way forward is a new application ID and a new listing, and every player reinstalls. So:

1. Restore from the offline backup first (see "Back it up and test the restore").
2. If both backups and the GitHub secret are gone, the GitHub secret cannot be read back, so the key is lost.
3. A leaked key is as bad: anyone could sign an update. Treat a leak the same way once the new application ID exists, and revoke the GitHub App key and the secrets.
