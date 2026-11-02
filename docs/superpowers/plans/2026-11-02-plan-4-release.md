# SuperSnake Plan 4: Release Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Every subagent runs on model Sonnet 5.5: pass `model: "sonnet"` on each Agent call. Steps use checkbox (`- [ ]`) syntax for tracking. Steps labelled **[owner]** are done by the owner, never by an agent.

**Goal:** Ship `v1.0.0` as a signed, attested, immutable GitHub Release APK, built by one workflow whose signing job waits for the owner's approval, with a release-build smoke test on every PR.

**Architecture:**
- `version.txt` holds the SemVer. `versionName` is that string and `versionCode` is computed from it by a pure function in `build-logic/checks`, under the 100% gate.
- `scripts/release-smoke.sh` installs an R8 release APK on an API 31 emulator, starts a game and waits for game over. A new CI job `release-smoke` runs it on every PR with a throwaway key.
- `.github/workflows/release.yml` runs on push to `main`: `release-please` (GitHub App token, environment `release-please`), `build` and `smoke` (no secrets), `sign-publish` (environment `release`, owner approval, apksigner, fingerprint check, attestation, publish) and `verify-published` (no secrets).
- Repo settings (environments, tag ruleset, immutable releases, labels) live in `scripts/github/apply-repo-settings.sh` and `expected-settings.json`.

**Tech Stack:** release-please-action v5.0.0, actions/create-github-app-token v3.3.0, actions/attest v4.2.2, actions/upload-artifact v7.0.2, actions/download-artifact v8.0.2, apksigner and zipalign from build-tools 37.0.0, keytool from the mise JDK, ReactiveCircus/android-emulator-runner v2.38.0, Kotlin 2.4.20 (ADR-0006), AGP 9.5.0. No new library.

**Spec:** read together:
- `docs/superpowers/specs/2026-10-22-supersnake-design.md`, the main spec: §6 (dependencies), §7.2 (rules, row "Release tags are trustworthy"), §8 (release), §9 (onboarding), §11 (versions)
- `docs/adr/0005-signing-key.md`
- the owner decisions of 2026-10-29 and 2026-11-02 in "Decided for Plan 4" below, recorded as ADR-0011 (version scheme) and ADR-0012 (release workflow)

Plans 1 to 3 are in `docs/superpowers/plans/`. Plan 3 built the app this plan releases.

## Start here (for a fresh session)

Together with the spec documents, this plan is self-contained. A new session needs nothing from the conversation that wrote it. Statements marked **[owner]** record the owner's decisions; treat them as settled.

`README.md` (for humans) and `CLAUDE.md` (for agents) point here.

### To start or resume

**AI agent (Claude Code):** open Claude Code in `/Users/r03418/AndroidStudioProjects/SuperSnake` and say:

> Execute `docs/superpowers/plans/2026-11-02-plan-4-release.md` with superpowers:subagent-driven-development, starting at the first task that is not done.

**Human:** work through the tasks in order, by hand. Every step gives the command and the output that means it passed.

### Read first, in this order

1. This file, all of it.
2. Main spec §6, §7.2, §8, §11, and `docs/adr/0005-signing-key.md`.
3. `CONTRIBUTING.md`, `docs/versions.md`, `.github/workflows/ci.yml`.
4. `build-logic/checks`: `PullRequestBody.kt`, `WorkflowSafety.kt`, `Cli.kt` and their tests.
5. `scripts/github/apply-repo-settings.sh` and `expected-settings.json`.
6. `app/build.gradle.kts`.

### Progress lives in git

- **A task is done** when all its checkboxes are ticked in this file on `origin/main`. The task's PR ticks them. Task 6 is ticked by its follow-up PRs.
- **A task is in progress** if any of these exists for it:
  - a worktree (`git worktree list`)
  - its branch (its **Branch** line)
  - an open PR (`gh pr list`)

  Continue there, and re-run the first unticked step's verification before moving on.
- **A task is not started** if none of those exist.
- Run `git pull --ff-only` in the main checkout first; `origin/main` is the record.
- **Start from the first task that is not done.** Never skip a task.

### State at handoff (2026-11-02)

- **Plan 3 is complete.** `main` is at `d4d7260` (`test: wait for each DataStore to close before reopening (#105)`). The issue for this plan is #107.
- **PR #106** (`feat: play the game in full screen`, branch `feat/full-screen`) may still be open. It changes the app, not the release tooling; rebase onto whatever lands first.
- **Dependabot PR #41** (Kotlin 2.4.20 to 2.4.21) is open. CodeQL lists "Kotlin 1.8.0 to 2.4.20" (re-read 2026-10-29), so it stays open (ADR-0006). Releases build on 2.4.20.
- **Issue #82** (API 37 `ui-tests` legs required again) is open. `ui-tests (phone-37)` and `ui-tests (tablet-37)` stay informational.
- **Plugin:** `v0.3.0`, pinned in `.claude/settings.json`.
- **Repo state on 2026-10-29** (`gh api`): no environments, one ruleset (`main-protection`), immutable releases off, no `autorelease` labels, no tags, no `version.txt` or `CHANGELOG.md`, no `docs/release.md`. `app/build.gradle.kts` hardcodes `versionCode = 1` and `versionName = "1.0"`.
- **ADLC capture is on.** Every PR runs the fold-in in the per-task workflow.
- **Never commit** `local.properties`, `.idea/`, `.gradle/`, `.superpowers/`, `.claude/settings.local.json`, any keystore, any `.pem` file or any secret value.

### Decided for Plan 4 **[owner, 2026-10-29 and 2026-11-02]**

1. **Version scheme** (ADR-0011).
   - `version.txt` in the repo root holds the bare SemVer and a newline. release-please's `simple` type rewrites it; no marker comment.
   - `versionCode = major*1_000_000 + minor*1_000 + patch`. The build fails when minor > 999, patch > 999 or major > 2099 (Play's maximum is 2,100,000,000). `versionName` is the SemVer.
2. **Release-build smoke test.** One script, `scripts/release-smoke.sh`, runs in a PR job and again in the release workflow. It fails on any `FATAL EXCEPTION`, on a missing game-over text and on a run that interacted with nothing.
3. **One release workflow with an approval gate on signing** (ADR-0012). The jobs, environments and secrets are in "Exact values".
4. **App permissions:** Contents: write and Pull requests: write only. Add Issues: write only if a proof run shows label calls failing, and ask the owner first.
5. **Signing key** (ADR-0005, unchanged in substance): RSA 4096, PKCS12, validity 36500 days, made by the owner with `keytool` outside any git checkout, with an encrypted offline backup whose restore was tested.
6. **Spec changes** in Task 3: §8 (App credentials live in `release-please`, the release job is gated by a reviewer), §7 row "Release tags are trustworthy" (open point resolved), §11 (engine and attestation rows), ADR-0005 (one line), ADR-0012.
7. **First release is `1.0.0`**, forced by `release-as` in the release-please config, removed in the PR right after. A failure after the tag exists is fixed by shipping `1.0.1`, never by re-tagging.
8. **`0.0.0` maps to versionCode 1** **[owner, 2026-11-02]**: the placeholder before the first release keeps today's code 1, so an existing install upgrades; every real version uses the formula.
9. **Release PRs are exempt from the issue-link and Evidence rules** **[owner, 2026-11-02]**: only PRs labelled `autorelease: pending`. Task 5 Step 1 is already answered: approved.
10. **`release-smoke` is the 6th required check** **[owner, 2026-11-02]**. Task 2 Step 8 is already answered: yes.

### Exact values

| Item | Value |
|------|-------|
| Environment `release-please` | deployment branches: `main` only; no reviewers; secret `RELEASE_APP_PRIVATE_KEY` |
| Environment `release` | deployment branches: `main` only; reviewer: the owner; prevent self-review off; admins cannot bypass; secrets `RELEASE_KEYSTORE_BASE64`, `RELEASE_KEYSTORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD` |
| Repo variables | `RELEASE_APP_CLIENT_ID` (App client ID), `RELEASE_CERT_SHA256` (lowercase hex SHA-256 of the signing certificate, 64 characters, no colons) |
| Token | `actions/create-github-app-token` with `client-id`, `permission-contents: write`, `permission-pull-requests: write`; no `repositories` input, so it is limited to this repo |
| Jobs of `release.yml` | `release-please`, `build`, `smoke`, `sign-publish`, `verify-published` |
| release-please outputs used | `release_created`, `tag_name`, `version`, `sha` |
| Tag ruleset | name `release-tags`, target `tag`, include `refs/tags/v*`; rules `creation`, `update`, `deletion`, `non_fast_forward`; bypass list: the release App only (`Integration`, mode `always`) |
| Labels | `autorelease: pending`, `autorelease: tagged` |
| Immutable releases | `PUT /repos/{o}/{r}/immutable-releases`, read back as `enabled: true` |
| Files | `version.txt` (`0.0.0` until the first release), `release-please-config.json`, `.release-please-manifest.json`, `docs/release.md`, `docs/adr/0011-version-code.md`, `docs/adr/0012-release-workflow.md` |
| Scripts | `release-smoke.sh`, `sign-apk.sh`, `sign-throwaway.sh`, `verify-signature.sh`, `check-release-ref.sh`, `publish-release.sh`, `test-release-scripts.sh`, all in `scripts/` |
| Release assets | `SuperSnake-<version>.apk` and `SuperSnake-<version>.apk.sha256` |
| Keystore | `$HOME/supersnake-release/release.p12`, alias `supersnake`, outside every checkout |
| Smoke defaults | package `io.github.eastwoodno1.supersnake`; `SMOKE_TIMEOUT_SECONDS=120`; output directory `build/release-smoke`; Play button text `Play`; game-over text starts `Game over. Score` (`app/src/main/res/values/strings.xml`: `play`, `phrase_game_over`) |
| Emulator for the smoke | API 31, `google_apis`, `x86_64`, profile `pixel_7`, the same options as `ui-tests (phone-31)` |

`release-please-config.json` (the shape is from research; the `release-as` line is removed in Task 6):

```json
{
  "$schema": "https://raw.githubusercontent.com/googleapis/release-please/main/schemas/config.json",
  "release-type": "simple",
  "include-component-in-tag": false,
  "draft": true,
  "force-tag-creation": true,
  "changelog-sections": [
    {"type": "feat", "section": "Features"},
    {"type": "fix", "section": "Bug Fixes"},
    {"type": "perf", "section": "Performance"},
    {"type": "revert", "section": "Reverts"}
  ],
  "packages": {
    ".": {
      "version-file": "version.txt",
      "release-as": "1.0.0"
    }
  }
}
```

`.release-please-manifest.json`:

```json
{
  ".": "0.0.0"
}
```

The keytool command (the owner runs it, Task 4):

```sh
mkdir -m 700 "$HOME/supersnake-release" && cd "$HOME/supersnake-release" && keytool -genkeypair -v -keystore release.p12 -storetype PKCS12 -alias supersnake -keyalg RSA -keysize 4096 -sigalg SHA384withRSA -validity 36500 -dname "CN=SuperSnake, O=EastwoodNo1"
```

Pinned actions (all re-read on 2026-11-02 and unchanged from the 2026-10-29 research; every executor re-verifies on the day it uses them):

```text
actions/checkout@3d3c42e5aac5ba805825da76410c181273ba90b1
gradle/actions/setup-gradle@3f5f9adaf7d9fecd50b5935e54106014257a94e6
actions/upload-artifact@cf430e030ddbb5b0abf93d22962f4752f3646cd9
actions/download-artifact@9000827ccba6bdab643e8b6fd33ac0654aef8333
ReactiveCircus/android-emulator-runner@a421e43855164a8197daf9d8d40fe71c6996bb0d
googleapis/release-please-action@45996ed1f6d02564a971a2fa1b5860e934307cf7
actions/create-github-app-token@64ab729a6294000f5aa4a4bc0b73d1f32bc45510
actions/attest@1e69f48acb82d1966a394da916b4c1698aa569d6
```

### Research facts used by this plan (verified 2026-10-29; SHAs re-read 2026-11-02)

- **Versions, all `gh api repos/<repo>/releases/latest` plus `git/ref/tags/<tag>` (lightweight tags, so the SHA is the commit):**
  - release-please-action v5.0.0 (2026-04-22), create-github-app-token v3.3.0 (2026-10-12), actions/attest v4.2.2 (2026-08-04), download-artifact v8.0.2 (2026-10-07), upload-artifact v7.0.2 (2026-10-07).
  - `actions/attest-build-provenance` v4.2.2 is only a wrapper: its README says "new implementations should use `actions/attest` instead". Use `actions/attest`.
- **release-please-action (§6, owner-approved 2026-10-26):** 2,560 stars, last push 2026-08-28, 139 open issues and PRs, 1 issue opened and 0 closed in the 30 days to 2026-10-29. Runs on `node24` and is Google's own action. Its bundled engine is `release-please` `^17.6.0` at v5.0.0 (`package.json` at the tag), not 17.11.2 as `docs/versions.md` and spec §11 say; Task 3 corrects both. Open issue #1227 ("A release PR merged mid-run is re-proposed as an empty duplicate release PR") is a known risk.
- **release-please behaviour** (googleapis/release-please `docs/customizing.md`, `docs/manifest-releaser.md`, `schemas/config.json`, `src/strategies/simple.ts`, `src/updaters/default.ts`, `src/github-api.ts`):
  - `simple` updates `CHANGELOG.md` and `version.txt`; the updater writes `<version>\n` and nothing else, and `version.txt` must already exist.
  - `include-component-in-tag: false` gives `v<version>`. `draft: true` needs `force-tag-creation: true`; with it, `createRelease` creates `refs/tags/<tag>` at `release.sha` first, **ignores an HTTP 422 on the ref**, then creates the draft. If the tag ruleset blocked the App, the swallowed 422 would leave a draft with no tag until publish. Hence the proof workflow in Task 5 and the tag-exists check in `check-release-ref.sh`.
  - A merged release PR is found by its `autorelease: pending` label; release-please then swaps it for `autorelease: tagged`. A merged PR without the label produces no release.
  - Outputs: `release_created`, `tag_name`, `version`, `sha` (root component), also `true` for drafts. `release-as` is documented as deprecated in favour of a `Release-As:` footer; the owner chose it for the single first release.
  - Labels: the permission map lists add and remove label under both `issues: write` and `pull-requests: write`; issue threads 1072 and 1074 show pre-created labels fixing failures. The two labels are created in Task 4.
- **Immutable releases** (GA 2025-10-28; docs "Preventing changes to your releases"): assets and tag are locked at publish; the documented flow is draft, attach assets, publish; existing releases stay mutable, so enable it before the first release. API `GET`/`PUT`/`DELETE /repos/{owner}/{repo}/immutable-releases`; today it returned `{"enabled":false,"enforced_by_owner":false}`. A published immutable release gets a release attestation (`gh release verify`).
- **Environments** (docs "Deployments and environments"): the branch rule is matched against the run's `GITHUB_REF`; secrets in an environment are unavailable until required reviewers approve; with prevent self-review off, the owner can approve their own job; reviewers and wait timers are available on public repos of the free plan; admins can be barred from bypassing.
- **Rulesets:** `creation`, `update`, `deletion` and `non_fast_forward` restrict everyone except bypass actors; GitHub Apps can be bypass actors (`actor_type: Integration`). The `refs/tags/v*` pattern is untested on GitHub. **Evaluate mode is conditional on Enterprise plans** in the docs source (`repo-rules-enterprise`), and this is a personal repo, so Task 4 expects the API to refuse it.
- **Signing:** apksigner, not AGP's undocumented `android.injected.signing.*`. `--ks-pass env:VAR` keeps passwords off the command line. A throwaway-key run on the real unsigned APK passed on 2026-10-29: `Verifies`, v3 scheme only (minSdk 31), `Number of signers: 1`, `certificate SHA-256 digest` printed in lowercase hex, `zipalign -c -P 16 4` still passed after signing, the unsigned APK gave `DOES NOT VERIFY`.
- **Key:** Android docs: "Your key should be valid for at least 25 years", and for Play it must be valid past 2033-10-22. RSA is the conservative choice, not a documented rule.
- **versionCode** (developer.android.com versioning): positive integer, strictly increasing, Play maximum 2,100,000,000. `major*1_000_000 + minor*1_000 + patch` has maximum 2,099,999,999 at 2099.999.999.
- **Release secrets design** (`plan-4-release-design.md`): Thunderbird and Signal split "build unsigned" from "sign"; SLSA L3 says secret material "MUST NOT be accessible to the environment running the user-defined build steps"; `actions/attest` alone is SLSA Build L2. `actions/attest` needs `id-token: write`, `attestations: write` and `artifact-metadata: write` (its README, 2026-11-02).
- **CodeQL** lists "Kotlin 1.8.0 to 2.4.20" (https://codeql.github.com/docs/codeql-overview/supported-languages-and-frameworks/); the newest stable Kotlin is 2.4.21, so no bump here.

### Machine prerequisites

Plan 3's list still holds (mise, JDK through mise, gh as `EastwoodNo1`, git, the `phone37` AVD). Plan 4 adds:

```sh
"$ANDROID_HOME/build-tools/37.0.0/apksigner" --version
~/.local/bin/mise exec -- keytool -help | head -1
shellcheck --version | grep '^version'
jq --version
~/.local/bin/mise exec -- gh release verify --help | head -1
```

Expected: an apksigner version, a keytool usage line, shellcheck `version: 0.9.0` (the CI runner's version; run it at that version before pushing), a jq version and the `gh release verify` usage line.

### Owner-only steps

Agents never see secret values and never run the settings script (the classifier blocks them). In order:

| # | Step | Task |
|---|------|------|
| O1 | Answer the open owner questions: `release-smoke` required (Task 2), pr-rules exemption (Task 5) | 2, 5 |
| O2 | Create the GitHub App "SuperSnake Release": contents write, pull requests write, metadata read, no webhook, installed on this repo only. Generate a private key. Give the session the App slug | 4 |
| O3 | Run `keytool`, make two encrypted offline backups, test a restore, compute the certificate SHA-256 | 4 |
| O4 | Create the environments (the script does), then add secrets with `gh secret set --env` and variables with `gh variable set` | 4 |
| O5 | Run `scripts/github/apply-repo-settings.sh` | 2 (if yes), 4 |
| O6 | Run the tag-creation negative test and the proof workflow | 5, 6 |
| O7 | Merge the release PR and approve the `sign-publish` deployment | 6 |

### How the owner works

- Solo. Evidence for every claim. No comments in code or config. Latest stable versions, verified on the day. Minimal third-party dependencies.
- Claude reviews run locally from Claude Code. There is no Claude GitHub App, no Claude Code Action and no Anthropic API key in GitHub.
- **Plan approval covers:**
  - one GitHub issue and one PR per task in this repo
  - the actions named in "Exact values"; none is a library
  - in Task 4: the environments, the `release-tags` ruleset, immutable releases and the two labels, applied by the owner with the settings script
  - in Task 5: the temporary `release-proof.yml` workflow and the owner's one-off tag test
  - in Task 6: the tag `v1.0.0` and the published release, made by the release workflow
  - in Task 7: one PR, the tag `v0.4.0` and a GitHub release in `EastwoodNo1/android-sdlc-skills`
- **Stop and ask the owner before:**
  - anything outward-facing this plan doesn't list
  - deleting anything it doesn't list (a stale draft release or a burned tag included)
  - any change to a decision in the specs or in "Decided for Plan 4"
  - adding any library or action this plan doesn't name, adding Issues: write to the App, or using any token other than the ones named, including when a step fails without it

### After Plan 4 is merged

Start a fresh session and write the next plan, "Play publishing, later": the upload key, Play App Signing, the AAB and Play credentials through OIDC. `docs/release.md` already describes the key migration.

## Roadmap (this plan is 4 of 4)

1. **Foundation.** Done (Plan 1).
2. **Game engine.** Done (Plan 2).
3. **App.** Done (Plan 3).
4. **Release (this plan).**

The next step is **Play publishing, later**; it is not planned yet.

## Global Constraints

Copied from the specs, Plan 3 and the decisions above. Every task's requirements include this section.

**Code and commits**
- **No comments** anywhere: not in `*.kt`, `*.kts`, `*.xml`, `*.toml`, `*.yml`, `*.yaml`, `*.sh`, `*.pro`, `*.properties`, and not in the JSON files (JSON has none; the `$schema` key is data). `checkNoComments` enforces the listed extensions. The why goes in the commit message.
- **Commits:** Conventional Commits, with a body that has **Why:** and **What:** for `feat`, `fix`, `refactor`, `perf`.
  - Subagent commits end with `Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>`.
  - The orchestrator uses the line its own session's system reminder gives.
- **Evidence:** every "passes / works / done" claim carries the exact command and its real output, a CI run link, or a screenshot (main spec §7.5). A claim about GitHub settings carries the `gh api` read-back.

**Versions and dependencies**
- No new library. New actions are first-party, except release-please-action (owner-approved) and the emulator runner (already approved). Each task that uses an action re-verifies it that day and records it in `docs/versions.md` (source URL and date).
- **Never add:** the Plan 3 list (navigation, kotlinx-serialization directly, KTX, DI frameworks, Robolectric, Espresso directly, any Gradle plugin beyond AGP, Kotlin, Compose and Kover). Never add a signing plugin or put signing config in `app/build.gradle.kts`.
- Kotlin stays at 2.4.20 (ADR-0006).

**Workflows** (`:checkWorkflows` and zizmor must stay clean)
- Every `uses:` is pinned by full commit SHA. Top-level `permissions: {}`; each job grants only what it needs. `persist-credentials: false` on every checkout.
- Event data, step outputs and secrets reach `run:` only through `env:`, never interpolated. A secret appears only in the `env:` of the step that needs it.
- No `pull_request_target`, no `workflow_run`.

**Signing**
- No signing material in the repo, a worktree, an artifact or a log. The keystore is decoded into `$RUNNER_TEMP` in the `sign-publish` job only, and deleted in an `always()` step.
- A signature check that cannot tell right from wrong fails closed: an empty, malformed or different fingerprint fails the job.

**Process**
- Every task gets its own GitHub issue, worktree and branch, a squash-merged PR whose title is a Conventional header of at most 64 characters, and a PR body with `Closes #<issue>` and a filled `## Evidence`.
- **Required checks on `main`:** `checks`, `pr-rules`, `analyze (java-kotlin)`, `analyze (actions)`, `ui-tests (phone-31)`, plus `release-smoke` once the owner answers yes in Task 2. `ui-tests (phone-37)` and `ui-tests (tablet-37)` are informational until #82.

## Per-task workflow

**Before the task's Step 1:**
1. `git -C /Users/r03418/AndroidStudioProjects/SuperSnake pull --ff-only`
2. `~/.local/bin/mise exec -- gh issue create --title "<task title>" --body "<one-paragraph why>"`. Note the number.
3. `git -C /Users/r03418/AndroidStudioProjects/SuperSnake worktree add ../SuperSnake-<name> -b <branch>`, then work in `/Users/r03418/AndroidStudioProjects/SuperSnake-<name>`.
4. `cp /Users/r03418/AndroidStudioProjects/SuperSnake/local.properties /Users/r03418/AndroidStudioProjects/SuperSnake-<name>/`

**Implementer dispatch** (subagent-driven): the prompt says **capture the RED output before writing any main code**. The task review checks that RED came first and that the plan diff ticks only the steps that were done. Shell scripts get the same rule: `scripts/test-release-scripts.sh` fails before the script exists.

**Shell script checks.** Every new script is run through `shellcheck` 0.9.0 and the repo's `checkNoComments`, and every negative test asserts the exit code explicitly (a bare `! cmd` in the middle of a `set -e` script never fails).

**Finish:**
1. Strict gate, from the worktree:

   ```sh
   ~/.local/bin/mise exec -- scripts/gradle-strict.sh check :checkNoComments :checkWorkflows :checkReleaseManifest :checkBackupRules :app:assembleRelease
   ~/.local/bin/mise exec -- scripts/gradle-strict.sh :checks:check :checks:koverVerify
   ```

   Expected: `BUILD SUCCESSFUL` both times, with no deprecation other than the allowlisted one.
2. Zizmor, for tasks that touch `.github/`: `~/.local/bin/mise exec -- zizmor .github/`. Expected: no findings.
3. Tick this task's checkboxes in this plan, in the same branch.
4. ADLC fold-in, as `CLAUDE.md` "Fold-in before a pull request" says: run `android-sdlc:adlc-documenter` (`model: "sonnet"`) with `MODE: fold-in`, `STAGED: <dir>` and `REPO: <worktree>` if `staged/` has notes; run its `scan.sh` until it exits 0; commit `docs(adlc): fold in session notes`; save the lessons as memories; delete the folded files.
5. Push and open the PR:
   - `git push -u origin <branch>`. The pre-push hook runs the strict `check`.
   - `~/.local/bin/mise exec -- gh pr create --title "<PR title>" --body-file <file>`, with `## Why`, `## What`, `Closes #<issue>`, `## Evidence`, and the attribution line the session's system reminder gives.
6. Wait for the required checks with `gh pr checks <n> --watch` and paste the result in a PR comment. Squash-merge only when they are green: `gh pr merge <n> --squash --delete-branch`.
7. `git worktree remove ../SuperSnake-<name>` from the main checkout, then `git pull --ff-only`.

## Review Focus

1. **A re-run after publish, or a release PR merged twice.** Expected: the workflow does not fail half-way and never publishes twice; a re-run finds the release already published and exits successfully. Pinned in Task 5 (`publishSkipsWhenAlreadyPublished`, `publishUploadsThenPublishesDraft`).
2. **The keystore secret missing, wrong or from a different key.** Expected: signing fails and the fingerprint check fails closed; nothing is uploaded. Pinned in Task 2 (`wrongAliasFails`, `missingKeystoreFails`, `signApkNeverPrintsSecrets`) and Task 5 (`fingerprintMismatchFailsClosed`, `emptyFingerprintFailsClosed`, `unsignedApkFailsVerification`).
3. **A tag that is not on `main`, or does not exist, or disagrees with `version.txt`.** Expected: `build` fails before anything is signed. Pinned in Task 5 (`tagOffMainFails`, `missingTagFails`, `versionMismatchFails`).
4. **versionCode overflow or a malformed version.** Expected: the build fails with a message naming the field, and a lower code never ships. Pinned in Task 1 (`minorOverLimitRejected`, `patchOverLimitRejected`, `majorOverLimitRejected`, `malformedRejected`, `monotonicAcrossBumps`).
5. **A smoke test that passes without doing anything.** Expected: no Play button found, no tap, no game-over text or a `FATAL EXCEPTION` all fail it. Pinned in Task 2 (`noPlayButtonFailsTheSmoke`, `missingGameOverFailsTheSmoke`, `fatalExceptionFailsTheSmoke`) and by the real crashing build.

---

### Task 1: Version file and versionCode

**Branch:** `feat/version-code` · **PR title:** `feat: derive versionCode from version.txt`

**Files:**
- Create: `version.txt` (`0.0.0` and a newline), `build-logic/checks/src/main/kotlin/sdlc/checks/VersionCode.kt`, `build-logic/checks/src/test/kotlin/sdlc/checks/VersionCodeTest.kt`, `docs/adr/0011-version-code.md`
- Modify: `app/build.gradle.kts`, `CONTRIBUTING.md` (one line under "Rules": the formula and the limits)

**Interfaces:**
- Produces: `object VersionCode { fun of(semver: String): Int }` in package `sdlc.checks`. It trims surrounding whitespace, accepts only `MAJOR.MINOR.PATCH` of digits without leading zeros, and throws `IllegalArgumentException` with a message naming the version and the offending field (`major`, `minor`, `patch` or `format`). It returns `major*1_000_000 + minor*1_000 + patch`, except that `0.0.0` returns `1` so the unreleased placeholder keeps today's `versionCode` and a development build still installs over earlier ones.

- [x] **Step 1: Write the failing `VersionCodeTest`**

  | Test | Assertions |
  |------|------------|
  | `oneZeroZero` | `of("1.0.0") == 1_000_000` |
  | `twoThreeFour` | `of("2.3.4") == 2_003_004` |
  | `trailingNewlineAccepted` | `of("1.2.3\n") == 1_002_003` |
  | `middleOfRange` | `of("999.999.999") == 999_999_999` |
  | `highestAccepted` | `of("2099.999.999") == 2_099_999_999` |
  | `placeholderKeepsCodeOne` | `of("0.0.0") == 1` |
  | `minorOverLimitRejected` | `of("1.1000.0")` throws, message contains `minor` and `1.1000.0` |
  | `patchOverLimitRejected` | `of("1.0.1000")` throws, message contains `patch` |
  | `majorOverLimitRejected` | `of("2100.0.0")` throws, message contains `major` |
  | `hugeNumberRejected` | `of("99999999999.0.0")` throws, message contains `major` (not a bare `NumberFormatException`) |
  | `malformedRejected` | for `""`, `"1.0"`, `"1.0.0.0"`, `"v1.0.0"`, `"1.0.0-rc.1"`, `"1.a.0"`, `"01.0.0"`, `"-1.0.0"`, `"1. 0.0"`: throws, message contains `format` |
  | `monotonicAcrossBumps` | codes for `1.0.0`, `1.0.1`, `1.1.0`, `1.999.999`, `2.0.0` are strictly increasing |

- [x] **Step 2: Run them and confirm they fail**

  Run: `~/.local/bin/mise exec -- ./gradlew :checks:test --tests 'sdlc.checks.VersionCodeTest'`
  Expected: compilation fails with `Unresolved reference 'VersionCode'`.

- [x] **Step 3: Implement `VersionCode.of` in `VersionCode.kt`.** Parse with one regex, then `toLongOrNull` per field so overflow is a limit error, not a crash.
- [x] **Step 4: Run `:checks:check :checks:koverVerify`.** Expected: `BUILD SUCCESSFUL`, `missed="0"` for LINE and BRANCH.
- [x] **Step 5: Wire `app/build.gradle.kts` to `version.txt`.** `versionName` is the trimmed file text and `versionCode` is `VersionCode.of(text)`; the file is read with `providers.fileContents(rootProject.layout.projectDirectory.file("version.txt")).asText` so the configuration cache tracks it.
  - **Decision rule:** first try `buildscript { dependencies { classpath("sdlc:checks") } }` in `app/build.gradle.kts` (the included build substitutes `sdlc:checks`, as the root build's `checks` configuration already does). If Gradle cannot resolve it, create a small included build `build-logic/version` holding the same function and tests, and say why in ADR-0011. Record the choice and the reason either way.
- [x] **Step 6: Prove it on the APK, three ways**
  1. `~/.local/bin/mise exec -- ./gradlew :app:assembleRelease && "$ANDROID_HOME/build-tools/37.0.0/aapt2" dump badging app/build/outputs/apk/release/app-release-unsigned.apk | head -1`. Expected: `versionCode='1' versionName='0.0.0'`.
  2. Set `version.txt` to `1.2.3` and repeat. Expected: `versionCode='1002003' versionName='1.2.3'`.
  3. Set it to `1.1000.0` and run `:app:assembleRelease`. Expected: `BUILD FAILED` naming `minor`.

  Restore `version.txt` to `0.0.0`. All three outputs go in Evidence.
- [x] **Step 7: Write ADR-0011** (version scheme, formula, limits, the `0.0.0` rule, the wiring choice, alternatives rejected: `major*10000+...`, commit count, date-based, from `plan-4-research.md` section 5) and the `CONTRIBUTING.md` line.
- [ ] **Step 8: Finish per the per-task workflow**

---

### Task 2: Release smoke-test script and PR job

**Branch:** `ci/release-smoke` · **PR title:** `ci: smoke-test the R8 release APK on an emulator`

**Files:**
- Create: `scripts/release-smoke.sh`, `scripts/sign-apk.sh`, `scripts/sign-throwaway.sh`, `scripts/test-release-scripts.sh`
- Modify: `.github/workflows/ci.yml` (job `release-smoke`; a `checks` step that runs the script tests), `docs/versions.md`, `CONTRIBUTING.md` (rules table), and, if the owner says yes, `scripts/github/expected-settings.json` and `scripts/github/apply-repo-settings.sh`

**Interfaces:**
- Produces:
  - `scripts/sign-apk.sh <in.apk> <out.apk>`: reads `KS_FILE`, `KS_PASS`, `KEY_ALIAS`, `KEY_PASS` from the environment, runs `apksigner sign` with `--ks-pass env:KS_PASS` and `--key-pass env:KEY_PASS`, then `apksigner verify`. It prints no secret and exits non-zero on any failure.
  - `scripts/sign-throwaway.sh <in.apk> <out.apk>`: makes a 2048-bit PKCS12 key with a random password in a temporary directory, calls `sign-apk.sh`, and removes the key on exit.
  - `scripts/release-smoke.sh <apk>`: environment `PACKAGE`, `SMOKE_TIMEOUT_SECONDS` (120), `OUT_DIR` (`build/release-smoke`). Exit 0 only when it installed the APK, tapped the node whose text is `Play` found in a `uiautomator dump`, saw a dump containing `Game over. Score` before the timeout, and `adb logcat -d` has no `FATAL EXCEPTION`. It always leaves `ui-dump.xml`, `logcat.txt` and `game-over.png` (a screenshot, also on failure) in `OUT_DIR`. A run that found no Play node counts as zero interactions and fails.
  - `scripts/test-release-scripts.sh <unsigned.apk>`: runs every test, prints `PASS <name>` per test and `<n> passed`, exits 1 on any failure. Later tasks add tests to it.
  - CI check name `release-smoke`.

- [ ] **Step 1: Write the failing `scripts/test-release-scripts.sh`.** Each test runs in a subshell. The smoke tests put a fake `adb` first on `PATH`; it answers `install`, `logcat`, `shell uiautomator dump`, `shell input tap` and `exec-out screencap` from files the test writes.

  | Test | Assertions |
  |------|------------|
  | `signedApkVerifies` | `sign-throwaway.sh` on the unsigned APK exits 0; `apksigner verify` on the output exits 0 and prints `Verifies` |
  | `wrongAliasFails` | `sign-apk.sh` with an alias that is not in the keystore exits non-zero and writes no output APK |
  | `missingKeystoreFails` | `KS_FILE` pointing at a missing file exits non-zero |
  | `signApkNeverPrintsSecrets` | with `KS_PASS=topsecretpass`, stdout and stderr of a passing run and of a failing run never contain `topsecretpass` |
  | `noPlayButtonFailsTheSmoke` | a dump without a `Play` node exits non-zero, with zero taps recorded |
  | `missingGameOverFailsTheSmoke` | Play found and tapped, no game-over text before `SMOKE_TIMEOUT_SECONDS=2` exits non-zero |
  | `fatalExceptionFailsTheSmoke` | game-over text present but logcat holds `FATAL EXCEPTION` exits non-zero |
  | `smokePassesAndKeepsEvidence` | Play found, tapped once, game-over text present, clean logcat exits 0 and `OUT_DIR` holds the three files |

- [ ] **Step 2: Run it and confirm it fails.** Run: `bash scripts/test-release-scripts.sh app/build/outputs/apk/release/app-release-unsigned.apk`. Expected: the scripts under test do not exist; no `PASS` line, exit 1.
- [ ] **Step 3: Implement the three scripts.** Poll the dump every second; compute the tap point from the node's `bounds="[x1,y1][x2,y2]"`; grep `adb logcat -d` for `FATAL EXCEPTION`. Run `shellcheck` 0.9.0 on all four scripts. Expected: no output.
- [ ] **Step 4: Run the script tests.** Expected: eight `PASS` lines and `8 passed`.
- [ ] **Step 5: Add the `release-smoke` job to `ci.yml`.** Job outline (`name: release-smoke`, `runs-on: ubuntu-24.04`, `timeout-minutes: 45`, `permissions: contents: read`):
  1. checkout, mise, locked tools, SDK packages, KVM, `setup-gradle`, exactly as `ui-tests` does
  2. `Build release APK`: `mise exec -- ./gradlew :app:assembleRelease`
  3. `Sign with a throwaway key`: `scripts/sign-throwaway.sh app/build/outputs/apk/release/app-release-unsigned.apk build/release-smoke/app-release.apk`
  4. emulator step, same inputs as the `phone-31` leg (`api-level: "31"`, `target: google_apis`, `profile: pixel_7`, `disable-animations: false`), `script: scripts/release-smoke.sh build/release-smoke/app-release.apk`
  5. `actions/upload-artifact` with `if: always()`, name `release-smoke`, path `build/release-smoke/`

  Add to `checks`, after the 16 KB step, a step `Release script tests` running `scripts/test-release-scripts.sh app/build/outputs/apk/release/app-release-unsigned.apk`. Run `:checkWorkflows` and zizmor. Expected: `BUILD SUCCESSFUL`, no findings.
- [ ] **Step 6: Prove the smoke fails on a crashing build.** On a local scratch branch (never pushed), add `error("smoke proof")` at the top of `MainActivity.onCreate`, build the release APK, sign it with `sign-throwaway.sh` and run the script on `phone37`. Expected: exit non-zero, `logcat.txt` shows `FATAL EXCEPTION` with `smoke proof`. Then run it on the unmodified build. Expected: exit 0 and a `game-over.png` showing the game-over overlay. Both outputs go in Evidence; discard the scratch branch.
- [ ] **Step 7: Push and watch `release-smoke` pass on the CI emulator.** Run: `gh pr checks <n> --watch`. Expected: `release-smoke` passes, and its artifact holds `game-over.png`. If it fails only because the API 31 image is too slow under SwiftShader, raise `SMOKE_TIMEOUT_SECONDS` in the job; if it still fails, stop and ask the owner.
- [ ] **Step 8: [owner, answered 2026-11-02: yes] Ask whether `release-smoke` becomes the 6th required check.** Recommendation: **yes**: it catches R8, libGDX-native and ProGuard breaks that no other check builds, uses no secret and runs for every PR. The cost is one more required job (about 10 minutes) and that PRs open before this merges, including #106 and Dependabot #41, must rebase once. Record the answer in the PR.
- [ ] **Step 9: If yes, make it required.** Add `{"context": "release-smoke", "integration_id": 15368}` to `required_status_checks` in both `expected-settings.json` and the `ruleset_body` of `apply-repo-settings.sh`. **[owner]** run `scripts/github/apply-repo-settings.sh EastwoodNo1/SuperSnake`. Expected: `all settings match`. Update the `CONTRIBUTING.md` rules table (six required checks) and this plan's required-checks line under "Process". If no, record "informational" in the table instead.
- [ ] **Step 10: Record the sources in `docs/versions.md`** (no new action here; note that the smoke replaces Plan 3 Task 9's manual run).
- [ ] **Step 11: Finish per the per-task workflow**

---

### Task 3: Release documentation and decisions

**Branch:** `docs/release-design` · **PR title:** `docs: add release runbook, ADR-0012 and spec updates`

**Files:**
- Create: `docs/release.md`, `docs/adr/0012-release-workflow.md`
- Modify: `docs/adr/0005-signing-key.md`, `docs/superpowers/specs/2026-10-22-supersnake-design.md` (§7.2 row "Release tags are trustworthy", §8, §11 rows), `docs/versions.md`, `CONTRIBUTING.md` (the "Rules that later plans add" line), `README.md` (a "Releases" line pointing at `docs/release.md`)

**Interfaces:**
- Consumes: the values in "Exact values". Task 4 and Task 6 follow `docs/release.md` literally, so it must contain the exact commands.
- Produces: `docs/release.md` sections: Create the key (the keytool command from "Exact values", run outside any checkout), Back it up and test the restore, Compute the fingerprint, Create the GitHub App, Add the secrets and variables, Cut a release, Verify a downloaded APK, Recovery, Play App Signing migration, What losing the key means.

- [ ] **Step 1: Write `docs/release.md`.** Required content, each with the exact command:
  - **Fingerprint:** `keytool -exportcert -alias supersnake -keystore "$HOME/supersnake-release/release.p12" | shasum -a 256 | cut -d' ' -f1` (64 lowercase hex characters, the format `apksigner` prints and `RELEASE_CERT_SHA256` holds).
  - **Secrets:** `gh secret set RELEASE_KEYSTORE_BASE64 --env release` fed from `base64 -i release.p12 | tr -d '\n'` (one line); `RELEASE_KEYSTORE_PASSWORD` and `RELEASE_KEY_PASSWORD` (both the store password, because PKCS12 has one) and `RELEASE_KEY_ALIAS` (`supersnake`) from a prompt; `RELEASE_APP_PRIVATE_KEY` into `release-please` from the `.pem` file; `gh variable set RELEASE_APP_CLIENT_ID` and `RELEASE_CERT_SHA256`. No secret value ever appears in a command line or shell history.
  - **Cut a release:** merge the release PR; the `release` run opens an approval request; approve `sign-publish`; then run the verification below.
  - **Verify an APK:** `apksigner verify --print-certs SuperSnake-<v>.apk` (digest equals `RELEASE_CERT_SHA256`), `sha256sum -c SuperSnake-<v>.apk.sha256`, `gh attestation verify SuperSnake-<v>.apk --repo EastwoodNo1/SuperSnake`, `gh release verify v<v> --repo EastwoodNo1/SuperSnake`.
  - **Recovery:** a failed job before publish is re-run in place (`gh run rerun <id> --failed`); a stale draft is visible in `gh release list` and the owner deletes it only after asking; a failure after publish ships as the next patch version, because the tag name is burned (docs: "you cannot reuse the same tag name"); tags are never re-pointed. If the tag exists but its draft has no assets, re-run the failed jobs.
  - **Detect silence:** a draft older than an hour in `gh release list` means the approval was not given or a job failed.
  - **Key loss:** installed copies can never update; a new application ID and a new listing are the only way forward. Restore from the offline backup first.
  - **Play App Signing migration:** upload this key to Play App Signing through the Play Console, create a separate upload key, and keep the GitHub key as the app-signing key; steps as in ADR-0005.
- [ ] **Step 2: Write ADR-0012** "Release workflow with an approval-gated signing job" (status accepted 2026-11-02, owner). Context: the design question (`release` environment limited to `v*` tags versus a release job on `main`). Decision: the five-job workflow, environments, reviewer, tag ruleset with the App as only bypass, immutable releases, split build and sign. Alternatives rejected: a tag-triggered second workflow (the App key must be reachable from `main` anyway, a silent-failure mode, an untestable proof); the keystore outside GitHub (breaks ADR-0005's "only CI signs" and the attestation); AGP injected signing (undocumented, passwords on the command line). Evidence: a summary of `plan-4-release-design.md` with these quotes, each with its URL:
  - "A workflow job cannot access environment secrets until approval is granted by a reviewer" (GitHub secure-use docs)
  - "Once an immutable release is published, its associated Git tag is locked to a specific commit, cannot be changed, and cannot be deleted while the release exists" (immutable releases docs)
  - "Such secret material MUST NOT be accessible to the environment running the user-defined build steps" (SLSA build requirements, Build L3)
  - "Artifact attestations by itself provides SLSA v1.0 Build Level 2" (GitHub docs)
  - the Thunderbird and Signal split of unsigned build and signing

  State the consequences honestly: the reviewer is a click by the same person (self-review allowed because the repo is solo), the tag ruleset has one bypass actor and so differs from `main-protection`'s empty list, evaluate mode is unavailable, and a failure after the tag exists is fixed forward.
- [ ] **Step 3: Edit ADR-0005 and the spec.**
  - ADR-0005 gains one line under Decision: "The `release` environment requires the owner's approval and allows only `main`."
  - Spec §8: the App's client ID is a variable and its private key a secret in the `release-please` environment; the release job is gated by a required reviewer; the workflow has the five jobs; assets go to the draft and `sign-publish` publishes it; immutable releases are on before the first release; the token is minted with the two permissions only; labels are pre-created.
  - Spec §7.2 "Release tags are trustworthy": drop the open point; write "`release` environment limited to `main` with the owner as required reviewer; tag ruleset on `v*`: creation only by the release App, no update, deletion or force push".
  - Spec §8 "Signing key" bullet "The GitHub App's ID and private key live in the same environment" is replaced by the `release-please` placement.
  - Spec §11: the release-please row's engine becomes "bundled `^17.6.0` (the action's `package.json` at v5.0.0)"; the `actions/attest-build-provenance` row becomes `actions/attest` v4.2.2 (commit `1e69f48acb82d1966a394da916b4c1698aa569d6`); add rows for `actions/download-artifact` v8.0.2 and `actions/upload-artifact` v7.0.2.
  - Reversed decisions propagate: `grep -rn "same environment\|v\* tags\|attest-build-provenance\|17.11.2" docs README.md CONTRIBUTING.md CLAUDE.md` must list only lines that are correct or marked as superseded.
- [ ] **Step 4: Update `docs/versions.md`** with rows for release-please-action (with the §6 evidence and the honest note), create-github-app-token, attest, download-artifact, each with SHA, source URL and 2026-11-02 verification, and fix the engine statement.
- [ ] **Step 5: Check the docs.** Run: `~/.local/bin/mise exec -- scripts/gradle-strict.sh :checkNoComments` and the grep from Step 3. Expected: `BUILD SUCCESSFUL`; the grep shows no stale claim.
- [ ] **Step 6: Finish per the per-task workflow**

---

### Task 4: Repo settings for releases

**Branch:** `ci/release-settings` · **PR title:** `ci: add release environments, tag ruleset and labels`

**Files:**
- Modify: `scripts/github/apply-repo-settings.sh`, `scripts/github/expected-settings.json`, `docs/release.md` (corrections found while following it), `CONTRIBUTING.md` (the "Rules that later plans add" line)

**Interfaces:**
- Produces, in `expected-settings.json` (the script builds its API bodies from these values, so there is one source) and read back by the script:
  - `environments`: for `release-please` and `release`: `deployment_branch_policy` (`custom_branch_policies: true`, `protected_branches: false`), `branch_policies` (`["main"]`), `reviewers` (empty, and the owner's user id for `release`), `prevent_self_review` (false), `can_admins_bypass` (`true` for `release-please`, `false` for `release`), `secrets` (sorted names), as in "Exact values".
  - `variables`: `["RELEASE_APP_CLIENT_ID", "RELEASE_CERT_SHA256"]`.
  - `release_tags`: ruleset `release-tags` with `target`, `enforcement`, `include`, `rule_types`, and `bypass_actors` (the App's numeric id as `actor_id`, `actor_type: Integration`, `bypass_mode: always`).
  - `release_labels`: `["autorelease: pending", "autorelease: tagged"]`.
  - `immutable_releases`: `{"enabled": true}`.
- The script keeps its contract: idempotent writes, then a comparison, ending with `all settings match` or `MISMATCH: <key>` and exit 1. Label names are URL-encoded with `jq -rn --arg n "$name" '$n|@uri'`. Branch policies are listed before they are created.

- [ ] **Step 1: [owner] Create the GitHub App** (O2): "SuperSnake Release", contents write, pull requests write, metadata read, no webhook, installed on `EastwoodNo1/SuperSnake` only; generate the private key and keep the `.pem` outside every checkout. Give the session the App slug. The session reads the public ids with `gh api apps/<slug> --jq '{id, client_id}'` and puts `id` into `expected-settings.json`; it never sees the `.pem`.
- [ ] **Step 2: [owner] Create the signing key** (O3) with the keytool command from "Exact values", on a trusted machine, never in an agent session. Make two encrypted backups in separate places and restore one into a scratch directory to prove it (`keytool -list -keystore <restored>` shows alias `supersnake`). Compute the fingerprint as `docs/release.md` says. Tell the session only that this is done.
- [ ] **Step 3: Extend `expected-settings.json` first, then `apply-repo-settings.sh`.** Run the script's comparison before the writes exist. Expected: `MISMATCH` for `environments`, `release_tags`, `release_labels`, `immutable_releases` (RED).
- [ ] **Step 4: Implement the writes.**
  - Environments: `PUT repos/$repo/environments/<name>` with the policy, reviewers, `prevent_self_review` and `can_admins_bypass`; then `POST .../deployment-branch-policies` with `{"name":"main","type":"branch"}` for each missing policy.
  - Ruleset `release-tags`: create or update by name, body from `expected-settings.json`.
  - Labels: create or update each name with a colour and description.
  - `PUT repos/$repo/immutable-releases`.
  - Read each back with `gh api` and add the keys to the `jq` object. Secrets and variables are read-only checks: `repos/$repo/environments/<name>/secrets`, `repos/$repo/actions/variables`, names only.
  - Set `release_tags.enforcement` to `evaluate` first. If the API answers 422 (Evaluate mode needs an Enterprise plan; expected on this personal repo), set it to `active` and say so in the PR. The App is already a bypass actor, so `active` is safe for its own tag.
- [ ] **Step 5: [owner] Add the secrets and variables** (O4) with the commands in `docs/release.md`: the four `release` secrets, `RELEASE_APP_PRIVATE_KEY` in `release-please`, the two variables. Check: `gh api repos/EastwoodNo1/SuperSnake/environments/release/secrets --jq '[.secrets[].name]|sort'` lists exactly the four names.
- [ ] **Step 6: [owner] Run the script** (O5): `scripts/github/apply-repo-settings.sh EastwoodNo1/SuperSnake`. Expected: `all settings match`.
- [ ] **Step 7: Prove the check fails when a setting is wrong.** Temporarily set `can_admins_bypass` to `true` for `release` in `expected-settings.json` and run the script's read-back (the **[owner]** runs it). Expected: `MISMATCH: environments` and exit 1. Revert and run again. Expected: `all settings match`. Both outputs go in Evidence.
- [ ] **Step 8: [owner] Prove the ruleset blocks a human.** With the owner's own `gh` login, run `gh api -X POST repos/EastwoodNo1/SuperSnake/git/refs -f ref=refs/tags/v0.0.0-denied -f sha=$(git rev-parse HEAD)`. Expected: HTTP 422 or 403 naming the ruleset. If a tag was created instead, the pattern does not match: delete it with `gh api -X DELETE repos/EastwoodNo1/SuperSnake/git/refs/tags/v0.0.0-denied`, fix `include`, and repeat.
- [ ] **Step 9: Finish per the per-task workflow.** The Evidence section holds the RED output, `all settings match`, the mismatch proof and the tag refusal.

---

### Task 5: release-please and the release workflow

**Branch:** `ci/release-workflow` · **PR title:** `ci: add release-please and the approval-gated release`

**Files:**
- Create: `release-please-config.json`, `.release-please-manifest.json` (both from "Exact values"), `.github/workflows/release.yml`, `.github/workflows/release-proof.yml`, `scripts/verify-signature.sh`, `scripts/check-release-ref.sh`, `scripts/publish-release.sh`
- Modify: `scripts/test-release-scripts.sh`, `build-logic/checks/src/main/kotlin/sdlc/checks/PullRequestBody.kt`, `build-logic/checks/src/test/kotlin/sdlc/checks/PullRequestBodyTest.kt`, `build-logic/checks/src/main/kotlin/sdlc/checks/WorkflowSafety.kt` and its test (only if a rule must learn `environment:`; nothing requires it today), `CONTRIBUTING.md`, `docs/superpowers/specs/2026-10-22-supersnake-design.md` §7.2

**Interfaces:**
- Consumes: Task 2's `sign-apk.sh`, `release-smoke.sh`, `sign-throwaway.sh`; Task 4's environments, variables, labels and ruleset.
- Produces:
  - `scripts/verify-signature.sh <apk> <expected-sha256>`: runs `apksigner verify --print-certs`; exits 0 only if the first output line is `Verifies`, there is exactly one signer, and its SHA-256 digest equals the expected value (compared lowercase). An expected value that is empty or not 64 hex characters exits non-zero before `apksigner` runs.
  - `scripts/check-release-ref.sh <sha> <tag> <version>`: exits 0 only if the tag exists on `origin` and points at `<sha>`, `<sha>` is an ancestor of `origin/main` (`git merge-base --is-ancestor`), and `version.txt` at `<sha>` equals `<version>`.
  - `scripts/publish-release.sh <tag> <apk> <checksum-file>`: if `gh release view <tag> --json isDraft --jq .isDraft` is `false`, prints `already published` and exits 0; otherwise `gh release upload <tag> <apk> <checksum-file> --clobber`, then `gh release edit <tag> --draft=false`.
  - `PullRequestBody.validate(body: String, labels: Set<String>)` keeps its signature. New rule: a PR with the label `autorelease: pending` is exempt from both the issue-link rule and the Evidence rule.

- [ ] **Step 1: [owner, approved 2026-11-02] Approve the pr-rules exemption (a main spec §7 rule change).** The release PR's body is generated by release-please and rewritten on every push to `main`, so it can carry neither `Closes #n` nor an `## Evidence` section. Today the `dependencies` label waives only the issue link (a Dependabot PR still needs a hand-filled Evidence section, per `CONTRIBUTING.md`); this exemption waives both, for the label `autorelease: pending` only. Anyone who can label a PR could use it, exactly as with `dependencies`; the other five required checks and the squash-only ruleset still apply. **Recommendation: approve.** Without it the release PR can never merge. Record the decision in the PR and in spec §7.2 (rows "Every PR links an issue" and "Evidence section present and non-empty"). **Do not start Step 2 until the owner answers.**
- [ ] **Step 2: Write the failing tests.**
  - `PullRequestBodyTest`:

    | Test | Assertions |
    |------|------------|
    | `autoreleasePendingWaivesIssueLinkAndEvidence` | `validate("## Release notes", setOf("autorelease: pending"))` is empty |
    | `autoreleaseTaggedDoesNotWaive` | the same body with `autorelease: tagged` has both errors |
    | `dependenciesStillNeedsEvidence` | an empty-evidence body with `dependencies` has only the Evidence error |
    | `autoreleaseDoesNotChangeOrdinaryPrs` | a valid body and a body without a link behave as before with `setOf("bug")` |

  - `scripts/test-release-scripts.sh` (they use a throwaway key, a temporary git repo with a bare `origin`, and a fake `gh` on `PATH` that logs its arguments):

    | Test | Assertions |
    |------|------------|
    | `fingerprintMatchPasses` | sign with the throwaway key, read its digest from `apksigner`, `verify-signature.sh` with it exits 0 |
    | `fingerprintMismatchFailsClosed` | a different 64-hex value exits non-zero |
    | `emptyFingerprintFailsClosed` | `""`, `"abc"` and a value with colons exit non-zero, and `apksigner` is never called (the fake records no call) |
    | `unsignedApkFailsVerification` | the unsigned APK with any fingerprint exits non-zero |
    | `tagOnMainPasses` | tag at a commit on `origin/main`, matching `version.txt`, exits 0 |
    | `tagOffMainFails` | tag at a commit not reachable from `origin/main` exits non-zero |
    | `missingTagFails` | no tag on `origin` exits non-zero (the swallowed-422 case) |
    | `versionMismatchFails` | `version.txt` `1.0.1` against version `1.0.0` exits non-zero |
    | `publishUploadsThenPublishesDraft` | with `isDraft` true, the fake `gh` log shows `upload ... --clobber` before `edit ... --draft=false`, and nothing else |
    | `publishSkipsWhenAlreadyPublished` | with `isDraft` false, exit 0, output `already published`, no `upload` and no `edit` in the log |

- [ ] **Step 3: Run both and confirm they fail.** Run: `~/.local/bin/mise exec -- ./gradlew :checks:test --tests 'sdlc.checks.PullRequestBodyTest'` and `bash scripts/test-release-scripts.sh app/build/outputs/apk/release/app-release-unsigned.apk`. Expected: the first fails on the new assertions; the second prints no `PASS` for the ten new tests.
- [ ] **Step 4: Implement the three scripts and the `PullRequestBody` exemption.** Run `shellcheck` 0.9.0 on the scripts, then `:checks:check :checks:koverVerify` (100% LINE and BRANCH) and the script tests. Expected: `BUILD SUCCESSFUL`, `18 passed`.
- [ ] **Step 5: Write `release-please-config.json` and `.release-please-manifest.json`** exactly as in "Exact values".
- [ ] **Step 6: Write `release.yml`.** Top-level `name: Release`, `on: push: branches: [main]`, `permissions: {}`, `concurrency: {group: release, cancel-in-progress: false}`. Jobs (every job has `runs-on: ubuntu-24.04` and a `timeout-minutes`):

  | Job | needs, if | environment | permissions | Does |
  |-----|-----------|-------------|-------------|------|
  | `release-please` | none | `release-please` | `{}` | mint the App token, run release-please; job outputs `release_created`, `tag_name`, `version`, `sha` from the step outputs |
  | `build` | `release-please`; `release_created == 'true'` | none | `contents: read` | checkout `sha` with `fetch-depth: 0`; `check-release-ref.sh "$SHA" "$TAG" "$VERSION"`; the same tools steps as `checks`; strict gate with `:checkNoComments :checkWorkflows :checkReleaseManifest :checkBackupRules :app:assembleRelease`; `:checks:check :checks:koverVerify`; the 16 KB `zipalign` check; upload `app-release-unsigned.apk` as `unsigned-apk` |
  | `smoke` | `build` | none | `contents: read` | download `unsigned-apk`; `sign-throwaway.sh`; the API 31 emulator step; `release-smoke.sh`; upload `build/release-smoke/` as `release-smoke` |
  | `sign-publish` | `release-please`, `build`, `smoke` | `release` | `contents: write`, `attestations: write`, `id-token: write`, `artifact-metadata: write` | see below |
  | `verify-published` | `release-please`, `sign-publish` | none | `contents: read` | `gh release download` the two assets; `sha256sum -c`; `verify-signature.sh` with the `RELEASE_CERT_SHA256` variable; `gh attestation verify --repo`; the API 31 emulator step; `release-smoke.sh` on the downloaded APK; upload evidence as `release-smoke-published` |

  The `uses:` lines (copy the pins from "Exact values"; the outline of the first job):

  ```yaml
  release-please:
    environment: release-please
    permissions: {}
    steps:
      - id: app-token
        uses: actions/create-github-app-token@64ab729a6294000f5aa4a4bc0b73d1f32bc45510
        with:
          client-id: ${{ vars.RELEASE_APP_CLIENT_ID }}
          private-key: ${{ secrets.RELEASE_APP_PRIVATE_KEY }}
          permission-contents: write
          permission-pull-requests: write
      - id: release
        uses: googleapis/release-please-action@45996ed1f6d02564a971a2fa1b5860e934307cf7
        with:
          token: ${{ steps.app-token.outputs.token }}
          config-file: release-please-config.json
          manifest-file: .release-please-manifest.json
  ```

  `sign-publish` steps, in order, with the secrets only in the `env:` of the step that needs them: checkout `sha`; `actions/download-artifact` `unsigned-apk`; SDK packages (`build-tools;37.0.0`); decode `RELEASE_KEYSTORE_BASE64` into `$RUNNER_TEMP/release.p12` and run `sign-apk.sh` to `$RUNNER_TEMP/dist/SuperSnake-<version>.apk` with `KS_PASS`, `KEY_ALIAS`, `KEY_PASS`; `verify-signature.sh` against `vars.RELEASE_CERT_SHA256`; `zipalign -c -P 16 -v 4` on the signed APK; `sha256sum` into `SuperSnake-<version>.apk.sha256` from inside `$RUNNER_TEMP/dist` (so `sha256sum -c` works for downloaders); `actions/attest@1e69f48acb82d1966a394da916b4c1698aa569d6` with `subject-path` the APK; `publish-release.sh` with `GH_TOKEN: ${{ github.token }}`; and last `if: always()` `rm -f "$RUNNER_TEMP/release.p12"`. If `GITHUB_TOKEN` cannot upload to or publish the draft (HTTP 403), stop and ask the owner; the App key is not available in this environment by design.
- [ ] **Step 7: Write `release-proof.yml`.** A temporary workflow: `on: workflow_dispatch`, `permissions: {}`, one job in environment `release-please` that mints the App token (same inputs) and, with `GH_TOKEN` set to it, creates `refs/tags/v0.0.0-proof` at `GITHUB_SHA`, reads it back, deletes it, and fails if any of the three calls fails. It proves what the swallowed 422 would hide: the App can create and delete a `v*` tag under `release-tags`, and the token is scoped as intended. Task 6 removes it.
- [ ] **Step 8: Run the local checks.** Run: `~/.local/bin/mise exec -- scripts/gradle-strict.sh :checkWorkflows :checkNoComments` and `~/.local/bin/mise exec -- zizmor .github/`. Expected: `BUILD SUCCESSFUL`, no findings. If `WorkflowSafety` needs a new rule for `environment:`, add it test-first; otherwise leave it.
- [ ] **Step 9: Update the docs.** `CONTRIBUTING.md`: the two exemption rows and the release-tag row; spec §7.2 with the owner's decision; `docs/versions.md` if any pin changed.
- [ ] **Step 10: Finish per the per-task workflow** (the PR's required checks include the new `PullRequestBodyTest` cases through `:checks:check`).
- [ ] **Step 11: After merge, verify the first `release-please` run.** Run: `gh run list --workflow Release --limit 1` and `gh pr list --search 'release 1.0.0'`. Expected: the run succeeded; the PR `chore(main): release 1.0.0` exists, its title is 26 characters, `version.txt` reads `1.0.0` in its diff. Then run `gh pr checks <n>` on it. Expected: the required checks run (the App token is not `GITHUB_TOKEN`, so they do) and `pr-rules` passes through the exemption; `gh pr view <n> --json labels` shows `autorelease: pending`. If the log shows a label error, stop and ask the owner about Issues: write. If the body is cut off by GitHub's length limit because the first changelog covers the whole history, say so and ask before adding `bootstrap-sha`.
- [ ] **Step 12: [owner] Run the proof workflow** (O6): `gh workflow run release-proof.yml --ref main`, then `gh run watch`. Expected: success, and `gh api repos/EastwoodNo1/SuperSnake/git/ref/tags/v0.0.0-proof` answers 404 afterwards. A failure means the ruleset or the App is wrong: stop, fix Task 4's settings, and run it again before Task 6. Paste the run link in the PR.

---

### Task 6: First release v1.0.0

**Branch:** `release/v1.0.0` (the release PR itself comes from release-please; the follow-ups use `ci/remove-tag-proof` and `ci/drop-release-as`) · **PR titles:** `ci: remove the tag proof workflow`, `ci: stop forcing release 1.0.0`

**Files:**
- Delete: `.github/workflows/release-proof.yml`
- Modify: `release-please-config.json` (remove `release-as`), `docs/release.md` (the verified commands), this plan (tick Tasks 5 and 6), `README.md` (link the latest release)

**Interfaces:**
- Consumes: everything above. Produces the published release `v1.0.0` with `SuperSnake-1.0.0.apk` and `SuperSnake-1.0.0.apk.sha256`.

- [ ] **Step 1: Remove the proof workflow.** PR `ci: remove the tag proof workflow`, merged through the normal gates. Expected: `gh workflow list` no longer lists `release-proof`.
- [ ] **Step 2: Check the starting state.** Run: `gh api repos/EastwoodNo1/SuperSnake/immutable-releases`, `gh api repos/EastwoodNo1/SuperSnake/rulesets --jq '.[] | [.name,.enforcement] | @tsv'` and `gh release list`. Expected: `{"enabled":true,...}`, `release-tags` is `active` (`evaluate` was refused, or was switched in an owner-run settings change), `main-protection` is `active`, and no release exists.
- [ ] **Step 3: [owner] Merge the release PR `chore(main): release 1.0.0`** (O7) once its required checks are green and its diff shows `version.txt` at `1.0.0` and a changelog.
- [ ] **Step 4: Watch the run.** Run: `gh run watch` on the `Release` run. Expected, in order: `release-please` creates the tag and a **draft** (`gh release list` shows `Draft v1.0.0`; `git ls-remote --tags origin v1.0.0` prints the merge commit), `build` and `smoke` pass, `sign-publish` waits for approval.
- [ ] **Step 5: [owner] Approve the `sign-publish` deployment** (O7) in the run page, after reading the job's commands.
- [ ] **Step 6: Verify the published release.** In a fresh directory outside any checkout:

  ```sh
  gh release download v1.0.0 --repo EastwoodNo1/SuperSnake --dir "$(mktemp -d)"
  ```

  In that directory run each command and paste its real output:
  - `sha256sum -c SuperSnake-1.0.0.apk.sha256`. Expected: `SuperSnake-1.0.0.apk: OK`.
  - `"$ANDROID_HOME/build-tools/37.0.0/apksigner" verify --print-certs SuperSnake-1.0.0.apk`. Expected: `Verifies`, one signer, and a digest equal to the `RELEASE_CERT_SHA256` variable.
  - `gh attestation verify SuperSnake-1.0.0.apk --repo EastwoodNo1/SuperSnake`. Expected: verification succeeded.
  - `gh release verify v1.0.0 --repo EastwoodNo1/SuperSnake`. Expected: exit 0 (the release is immutable).
  - `gh release view v1.0.0 --json assets --jq '[.assets[].name]'`. Expected: exactly the APK and the `.sha256`.
  - `verify-published` is green in the run.
- [ ] **Step 7: Install on `phone37` and play.** `adb uninstall io.github.eastwoodno1.supersnake` (an earlier debug build has another key), `adb install SuperSnake-1.0.0.apk`, play to game over, then `adb shell am force-stop io.github.eastwoodno1.supersnake`, start it again and check that the high score shows. Expected: `Success`, a game-over screenshot and a high-score screenshot after the cold start, `adb logcat -d | grep -c 'FATAL EXCEPTION'` prints `0`, `aapt2 dump badging` prints `versionCode='1000000' versionName='1.0.0'`.
- [ ] **Step 8: Remove `release-as`.** PR `ci: stop forcing release 1.0.0`. Expected afterwards: the next `release-please` run opens no PR, or one that proposes the version the commits imply. If a PR proposing `1.0.0` again appeared between the release and this merge, close it unmerged.
- [ ] **Step 9: Record the evidence** in `docs/release.md` (the run link, the outputs above) and link the release from `README.md`.
- [ ] **Step 10: If a step failed after the tag existed.** Do not re-tag, delete the tag or re-run `release-please`. Re-run the failed jobs in place if the draft is unpublished. If the release was published or the fix needs code, ship `1.0.1` through a normal release PR and say so in `docs/release.md`. Ask the owner before deleting a draft.
- [ ] **Step 11: Finish per the per-task workflow** for each PR in this task.

---

### Task 7: Plugin v0.4.0 and closing Plan 4

**Branch:** `docs/close-plan-4` · **PR title:** `docs: mark Plan 4 complete and hand off to Play`

**Files:**
- Plugin repo `/Users/r03418/AndroidStudioProjects/android-sdlc-skills`:
  - `android-ci` templates and skill text: `release.yml`, the release-please config and manifest, the `release-smoke` job, the release scripts and `test-release-scripts.sh`, `VersionCode` and its test, the settings additions
  - `sdlc-rules` skill text: the release steps and the failure policy
  - its own issue, branch, PR, tag `v0.4.0` and GitHub release with notes
- This repo: `.claude/settings.json` (`ref` to `v0.4.0`), this plan's "State at handoff", `README.md`, `CLAUDE.md`

- [ ] **Step 1: Plugin PR.** Update the templates and skill text, then run `scripts/verify-templates.sh /Users/r03418/AndroidStudioProjects/SuperSnake`. Expected: `0 drift`. Merge through the plugin repo's own checks, tag `v0.4.0` from its `main`, and publish a GitHub release with notes (the plan approves this).
- [ ] **Step 2: Adopt the new plugin version.** Set `ref` in `.claude/settings.json` to `v0.4.0`, then run:

  ```sh
  claude plugin marketplace add EastwoodNo1/android-sdlc-skills#v0.4.0 --scope project
  claude plugin install android-sdlc@android-sdlc-skills --scope project
  claude plugin list
  ```

  Expected: `Version: 0.4.0`, `Status: ✔ enabled`.
- [ ] **Step 3: Prove the whole release on `main`'s head.** Run the strict gate and `:checks:check :checks:koverVerify`. Expected: `BUILD SUCCESSFUL`. Run `scripts/github/apply-repo-settings.sh EastwoodNo1/SuperSnake` through the owner. Expected: `all settings match`. Link the green required checks and the `Release` run of Task 6.
- [ ] **Step 4: Check rule coverage.** For each new rule (version formula, release tags, immutable releases, signed APK fingerprint, smoke test) name the test or the run that proves it, with `grep -rn` output. Put the table in the PR body.
- [ ] **Step 5: Update the handoff text.** Mark Plan 4 complete with PR numbers, the release link, `main` head, Dependabot #41 and #82 state, and the next action ("Play publishing, later"). Update `README.md` ("Plan 4 (release) is complete") and point `CLAUDE.md` "Start of every session" at this plan.
- [ ] **Step 6: Finish per the per-task workflow**
