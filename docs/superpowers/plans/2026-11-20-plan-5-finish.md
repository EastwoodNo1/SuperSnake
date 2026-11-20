# SuperSnake Plan 5: Finish Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Every subagent runs on model Sonnet 5.5: pass `model: "sonnet"` on each Agent call. Steps use checkbox (`- [ ]`) syntax for tracking. Steps labelled **[owner]** are done by the owner, never by an agent.

**Goal:** Finish the project. That means:
- merge the last open PRs
- bring the `android-sdlc` plugin back to `0 drift` and release it as `v0.4.1`
- close Plan 4
- fold every session into `docs/adlc/`
- publish `v1.0.1` with the v1.0.0 bug fixes
- end with empty PR and issue lists, apart from anything the owner chooses to keep

**Architecture:** no new code. The plan coordinates:
- PRs that already exist
- one plugin release
- one documentation PR
- one release through the workflow Plan 4 built (`release.yml`: release-please, then build, smoke, the approval-gated sign-publish, and verify-published)

**Tech stack:** unchanged from Plan 4, plus the versions recorded in `docs/versions.md` on `main`. That includes Compose BOM 2026.11.01, lifecycle 2.11.1, compileSdk 37.2 and codeql-action v4.39.3. Kotlin stays at 2.4.20, the CodeQL cap (ADR-0006).

**Spec:** the main spec `docs/superpowers/specs/2026-10-22-supersnake-design.md`, and Plan 4 (`docs/superpowers/plans/2026-11-02-plan-4-release.md`) Tasks 6 and 7, which this plan finishes.

## Start here (for a fresh session)

This file is self-contained. A new session needs nothing from the conversation that wrote it. `CLAUDE.md` and `README.md` point here.

### To start or resume

**AI agent (Claude Code):** open Claude Code in `/Users/r03418/AndroidStudioProjects/SuperSnake` and say:

> Execute `docs/superpowers/plans/2026-11-20-plan-5-finish.md` with superpowers:subagent-driven-development, starting at the first task that is not done.

**Human:** work through the tasks in order. Every step gives the command and the output that means it passed.

### Read first, in this order

1. This file, all of it.
2. `CLAUDE.md` and `CONTRIBUTING.md`.
3. Plan 4, Task 6 (release 1.0.0) and Task 7 (closing Plan 4).
4. `docs/versions.md`.

### Progress lives in git

- **A task is done** when all its checkboxes are ticked in this file on `origin/main`. Each task's PR ticks them. Tasks with no PR of their own (Tasks 1, 2 and 8) are ticked by the PR of Task 4.
- **A task is in progress** if its worktree (`git worktree list`), its branch or its PR (`gh pr list`) exists. Continue there.
- **Never redo merged work.** Check with `gh pr list --state merged --limit 40`.
- Run `git pull --ff-only` in the main checkout first.

### State at handoff (2026-11-20, `main` at `8ab8f09`)

The owner gave the agents full autonomy to finish on 2026-11-19, with minimal interaction. Agents merge PRs themselves: squash plus auto-merge works for the agent account. The only owner steps are the ones marked **[owner]**, and Task 8 sends them as one batch.

Merged since v1.0.0, each closing its issue:

| PR | What | Closes |
|---|---|---|
| #163 | the game-over save cannot be cancelled; a quit game with a non-zero score is recorded | #151 |
| #156 | Back button on High scores and Settings | #153 |
| #167 | AudioTrack leak and DataStore IOException crash paths | #152 |
| #164 | the menu follows the animation scale; the live flag resets on leave | #101 |
| #165 | back and countdown decisions moved to `:domain` | #102 |
| #158 | domain edge cases | #103 |
| #159 | beep PCM built once per process | #100 |
| #155 | AGP release date source | #104 |
| #157 | the ui-test results guard requires tests > skipped | #99 |
| #160 | backup rules check tightened | #98 |
| #166 | API 37 legs required in the settings files | #82 |
| #162 | Dependabot holds Kotlin at the CodeQL cap; Dependabot then closed #41 | #161 |
| #169 | plugin-portal marker POM checksums trusted as a fallback | #168 |
| #172 | codeql-action v4.39.3, superseding Dependabot #132 | #171 |
| #175 | Compose BOM 2026.11.01, lifecycle 2.11.1, compileSdk 37.2 | #174 |

Notes on three of these:
- **#152:** the owner's own crash was never reproduced. #167 fixed the most likely cause. Reopen #152 if a crash recurs with a logcat.
- **#166:** the setting applies only after the owner runs the settings script (Task 8).
- **#175:** Compose 1.13 requires compileSdk 37.2.

Open at handoff:
- **Gradle 9.9.2 bump** (branch `build/gradle-992`, its own issue): Gradle 9.9.2 was released on 2026-11-20. Lint's `AndroidGradlePluginVersion` check now fails `:app:lintDebug` on every branch, so nothing can be pushed and every PR's `checks` job is red until this merges. It follows #129's procedure. Merge it first.
- **#176** (branch `ci/utp-cache`, closes #170): only `checks` writes the Gradle cache, and it warms the UTP dependencies first. Green, queued for auto-merge.
- **#173** (branch `test/sturdy-tests`, closes #97): sturdier instrumented tests. Green, queued for auto-merge, BEHIND `main`. Its PR body records two issue bullets that were not done and why.
- **#146**: the release-please PR for 1.0.1. Task 7 handles it.
- **#154**: the Plan 4 Task 7 issue. Worktree `../SuperSnake-close-plan-4`, branch `docs/close-plan-4`, has the unpushed commit `1c5ea94` (adopt plugin v0.4.0). Task 5 finishes it.

Known CI behaviour:
- **Emulator legs flake.** Different tablet-37 tests fail from run to run, and whole jobs sometimes die on a Maven Central "could not find" during Gradle configuration. A rerun of the failed jobs (`gh run rerun <id> --failed`) usually passes. Task 3 files this.
- **New stable dependencies trip the lint gate.** A new stable dependency fails `:app:lintDebug` with `GradleDependency` or `AndroidGradlePluginVersion` on every PR. The fix is a separate bump PR first (as #175 did), never suppressing the check.

### Decisions taken on the owner's behalf (2026-11-19)

Each is stated in its PR, so the owner can veto it:
- **#163:** a game left with a non-zero score is recorded once, including through Restart.
- **#156:** a Back button on both High scores and Settings.
- **#164:** `live` stays true on the game-over screen until the game is left, so rotating there keeps the game over.
- **#165:** the no-decision rule covers composables.
- **#162:** Kotlin is held by a Dependabot ignore, not left as a parked PR.
- **#175:** compileSdk 37.2.
- **#176:** only `checks` writes the Gradle cache.
- **This plan:** Plan 4 closes once Task 6 Step 7 passes on 1.0.1 (Plan 4 Task 7 option A). One `docs(adlc)` fold-in at the end instead of one per PR.

## Global Constraints

Everything in `CLAUDE.md` and `CONTRIBUTING.md` applies:
- worktrees; test-first where code changes
- no comments in code or config
- Conventional Commits with `Why:` and `What:` and the writing model's `Co-Authored-By:` line
- PR bodies with `Closes #<n>`, a fenced `## Evidence` section and the Claude Code footer
- squash merge only

Also:
- **Evidence:** never claim a step passed without the command and its real output.
- **Report every check by name**, informational ones included.
- **Settings script:** agents never run `scripts/github/apply-repo-settings.sh`, in any mode. The **[owner]** runs it.
- **Secrets:** agents never see secret values, and never @-mention or paste a key file. Before any fold-in commit, `grep -rn "PRIVATE KEY\|MII" docs/adlc` must print nothing.
- **Stop and ask the owner before:**
  - anything outward-facing this plan doesn't list. This plan lists:
    - merging PRs
    - the plugin PR, its tag and its release
    - deleting the stale Gradle cache entry
    - the 1.0.1 release
  - deleting anything this plan doesn't list
  - changing a decision in the spec
- **Subagents** get this file's path and the task text, run on model sonnet, and do not touch `docs/adlc` or `.git/adlc`. Only the main session folds in, in Task 6.

### Merge queue helper

PRs must be up to date with `main` before they merge. To merge several queued PRs, run this loop in the background from the main checkout:

```bash
while :; do
  q=$(gh pr list --json number,autoMergeRequest -q '[.[]|select(.autoMergeRequest!=null)|.number]|sort|.[]')
  [ -z "$q" ] && { echo "queue empty"; exit 0; }
  p=$(echo "$q" | head -1)
  case "$(gh pr view $p --json mergeStateStatus -q .mergeStateStatus)" in
    BEHIND) gh pr update-branch $p ;;
    DIRTY) echo "conflict #$p"; exit 1 ;;
  esac
  sleep 120
done
```

It only updates branches. Auto-merge merges each PR when its checks are green. Rerun a failed emulator job at most twice. Investigate anything red after that.

---

### Task 1: Land the open PRs

**Branch:** none (existing PRs)

- [x] **Step 0: Merge the Gradle 9.9.2 bump PR first.** (Done in #179, closing #178.) Find it with `gh pr list --search "bump Gradle to 9.9.2"`. If it does not exist, create it: worktree `../SuperSnake-gradle-992`, following PR #129. The steps are:
  - verify the version from services.gradle.org
  - run the wrapper task twice, with the distribution SHA-256
  - strip generated comments
  - refresh `verification-metadata.xml` for any new embedded Kotlin
  - update the `docs/versions.md` row
  - run `scripts/gradle-strict.sh --rerun-tasks -Dorg.gradle.deprecation.trace=true check :app:lintDebug`

  Expected: the PR is `MERGED`, and `:app:lintDebug` on `main` passes.
- [ ] **Step 1: Merge #176, then #173.**
  - Confirm both have auto-merge: `gh pr view <n> --json autoMergeRequest`. If not, run `gh pr merge <n> --squash --auto`.
  - Run the merge queue helper, and rerun flaky emulator jobs as above.
  - Expected: `gh pr view 176 --json state -q .state` and the same for 173 both print `MERGED`, and `gh issue view 170 --json state` and the same for 97 both print `CLOSED`.
- [x] **Step 2: Replace the stale Gradle cache entry.** Done 2026-11-20: stale entries 10828776947 and 10807844476 were deleted and the CI run on `main` rerun; `gh cache list` now shows a 648.14 MiB entry (id 10830599340) and neither stale id. #176's PR body explains why: the basic cache provider never overwrites an exact key hit, and the existing 129 MiB entry lacks the UTP dependencies.
  - Run `gh cache list`, and delete the entry #176 names, or any entry under 200 MiB with the current key, with `gh cache delete <id>`.
  - Then re-run the newest `CI` run on `main`: `gh run list --workflow CI --branch main --limit 1`, then `gh run rerun <id>`.
  - Expected: after it finishes, `gh cache list` shows a new entry of roughly 600 MiB or more.
- [ ] **Step 3: Remove merged worktrees.** For each worktree other than `main` whose PR is merged, check that `git -C <dir> status --porcelain` prints nothing, then run `git worktree remove <dir>` and `git branch -D <branch>`. Expected: `git worktree list` shows `main` and the `close-plan-4` and `plan-5` worktrees, if their tasks are still open.

### Task 2: Plugin `v0.4.1`

**Repo:** `EastwoodNo1/android-sdlc-skills`, local clone `/Users/r03418/AndroidStudioProjects/android-sdlc-skills`. Follow that repo's own contribution rules: issue first, worktree `../android-sdlc-skills-<name>`, Conventional Commits, `Closes #` and `## Evidence`, required check `verify`.

Start only after Task 1, because the templates must equal the final `main`.

- [x] **Step 1: Measure drift.** Measured by the plugin PR; see Step 3. Make a clean SuperSnake checkout: `git -C /Users/r03418/AndroidStudioProjects/SuperSnake worktree add --detach ../SuperSnake-plugin-src origin/main`. In a plugin worktree, run `scripts/verify-templates.sh /Users/r03418/AndroidStudioProjects/SuperSnake-plugin-src`.
  - The script is a byte-for-byte `cmp` of every tracked file under `skills/git-hooks/templates` and `skills/android-ci/templates` against the same path in SuperSnake. There is no neutralising step.
  - Expected: a list of drifted files. Changes since v0.4.0 (tag `a9e3e86`) include `ci.yml`, `codeql.yml`, `release.yml`, `dependabot.yml`, `bootstrap.sh`, `check-ui-test-results.sh`, the backup-rules check in `build-logic/checks`, `scripts/github/*`, `verification-metadata.xml`-related docs, and the new `scripts/test-ui-test-guard.sh`.
- [x] **Step 2: Sync.**
  - Copy every drifted file verbatim from the clean checkout, and add new template files.
  - Update the skill text that describes changed behaviour: the required API 37 legs, the ui-test guard, the backup rules, the Kotlin Dependabot cap, `cache-read-only` on non-`checks` jobs, compileSdk 37.2, and the plugin-portal `also-trust` checksums.
  - Bump `.claude-plugin/plugin.json` to `0.4.1`.
  - Expected: `scripts/verify-templates.sh ../SuperSnake-plugin-src` prints `0 drift`, and the plugin's local checks (shellcheck, `zizmor .github/`, `claude plugin validate .`) pass.
- [x] **Step 3: PR, merge, tag, release.** Done: issue android-sdlc-skills#16, PR #17 merged, annotated tag `v0.4.1` dereferences to commit `87aba64599579da645ee02f6ddd593c9e971ebc4`, release https://github.com/EastwoodNo1/android-sdlc-skills/releases/tag/v0.4.1; 0 drift against SuperSnake `111ff96`.
  - Open the plugin PR and wait for `verify` to pass.
  - Squash-merge it.
  - Tag `v0.4.1` on the merge commit (when pinning a tag, dereference annotated tags to the commit).
  - Publish a GitHub release with notes listing the synced changes.
  - Remove `../SuperSnake-plugin-src`.
  - Expected: `gh release view v0.4.1 --repo EastwoodNo1/android-sdlc-skills` shows the release.

### Task 3: Track the emulator flakiness

**Branch:** none

- [x] **Step 1: File the issue.** Filed as #181. Title: `test: tame emulator flakiness in the ui-tests legs`. Include:
  - main run `44600765067`: tablet-37 failed `AccessibilityTest.keyboardReachesEveryMenuItem` and `FullScreenTest.barsAreHiddenAgainWhenWindowFocusReturnsWithThemShown`
  - main run `44598412152`: phone-31, phone-37 and checks failed
  - #173: `awaitText("No scores yet")` hit its 20 s timeout on tablet-37
  - Maven Central "could not find" during configuration
  - the rerun workaround

  Expected: the issue number. Fixing it is optional before the release. If the agent fixes it, it gets its own branch and PR, test-first, and is merged before Task 7.

### Task 4: Commit this plan

**Branch:** `docs/plan-5-finish` · **PR title:** `docs: add Plan 5 to finish the project` · **Closes** #177

- [x] **Step 1:** This file, plus the `CLAUDE.md` "Start of every session" pointer to it, merged to `main`. Expected: `git show origin/main:docs/superpowers/plans/2026-11-20-plan-5-finish.md` prints this file. The PR that merges it ticks this step, and the Tasks 1 to 3 steps already done by then.

### Task 5: Close Plan 4

**Branch:** `docs/close-plan-4` (worktree `../SuperSnake-close-plan-4`, commit `1c5ea94`) · **PR title:** `docs: mark Plan 4 complete and hand off to Play` · **Closes** #154

- [x] **Step 1: Rebase and adopt v0.4.1.** Rebased on `origin/main` d42c460 and pinned `v0.4.1` in `.claude/settings.json`, `CLAUDE.md` and `README.md`. `claude plugin marketplace add EastwoodNo1/android-sdlc-skills#v0.4.1 --scope project` failed with "its source doesn't match its extraKnownMarketplaces entry in user or managed settings"; user settings were not edited. `claude plugin install` answered "already installed (scope: project) — the marketplace now offers 0.4.1 (installed: 0.4.0)", and `claude plugin list` still shows 0.3.0 (user, project) and 0.4.0 (project), so the `Version: 0.4.1` expectation is not met until the owner aligns the user-settings entry.
  - Rebase on `origin/main`.
  - Change `ref` in `.claude/settings.json`, the install commands and the expected version in `CLAUDE.md` and `README.md` from `v0.4.0` to `v0.4.1`.
  - Run `claude plugin marketplace add EastwoodNo1/android-sdlc-skills#v0.4.1 --scope project`, `claude plugin install android-sdlc@android-sdlc-skills --scope project` and `claude plugin list`. Expected: `Version: 0.4.1`, `Status: ✔ enabled`.
  - The `add` may fail because the user settings have an unpinned `extraKnownMarketplaces` entry for this marketplace. Never edit user settings. Record the failure and the `plugin list` output instead.
- [x] **Step 2: Plan 4 Task 7 Step 3.** Ran `scripts/gradle-strict.sh check :checkNoComments :checkWorkflows :checkReleaseManifest :checkBackupRules` (`BUILD SUCCESSFUL in 20s`) and `scripts/gradle-strict.sh :checks:check :checks:koverVerify` (`BUILD SUCCESSFUL in 3s`) on the rebased head; `:app:assembleRelease` was not run here (release-smoke covers it in CI). The settings script is in the owner batch (Task 8). On `main`'s head, run:
  - the strict gate: `scripts/gradle-strict.sh check :checkNoComments :checkWorkflows :checkReleaseManifest :checkBackupRules :app:assembleRelease`
  - `:checks:check :checks:koverVerify`

  Expected: `BUILD SUCCESSFUL` for both. Link the green required checks of `main`'s last CI run. The settings script run happens in Task 8.
- [x] **Step 3: Plan 4 Task 7 Step 4.** Table in the PR body. Put the rule-coverage table in the PR body, with `grep -rn` output showing the test or run that proves each rule:
  - version formula: `VersionCodeTest`
  - release tags and immutable releases: `expected-settings.json`
  - signed APK fingerprint: `verify-signature.sh`, against `RELEASE_CERT_SHA256` in `release.yml`
  - smoke test: the `release-smoke` job and `test-release-scripts.sh`
- [x] **Step 4: Plan 4 Task 7 Step 5.** Update the handoff text.
  - In the Plan 4 file, mark Plan 4 complete, with PR numbers, the v1.0.0 release link, `main`'s head, and that #41 and #82 are closed.
  - Note that Plan 4 Task 6 Step 7 moves to this plan's Task 8, played on 1.0.1. Its 1.0.0 attempt found bugs #151 to #153.
  - Set the next action to "Play publishing, later".
  - In `README.md`, say "Plan 4 (release) is complete", and that Plan 5 finishes the project.
- [ ] **Step 5: Finish per the per-task workflow.**
  - Push, open the PR and run the merge queue helper.
  - Plan 4 Task 7 Steps 3–6 and Plan 4 Task 6 Step 11 are ticked in this PR.
  - Plan 4 Task 6 Step 7 is ticked by Task 8's follow-up.

### Task 6: Final ADLC fold-in

**Branch:** `docs/adlc-final` · **PR title:** `docs(adlc): fold in session notes` · **Closes:** a new issue `docs(adlc): fold in the Plan 4 and finish-up sessions`

The state folder is `<state>` = `$(git rev-parse --path-format=absolute --git-common-dir)/adlc`.

Capture misses long or resumed sessions: a session id already in `<state>/captured` is never captured again (EastwoodNo1/android-sdlc-skills#13). Nothing after 2026-11-09 was captured, except a re-capture of session `3e97d016` (#82).

- [ ] **Step 1: Re-capture the long sessions.**
  - The finish-up orchestrator session `35559d8c-dea6-4515-a650-94781a5140a4` removed its own id from `<state>/captured` before ending (2026-11-20), so its SessionEnd hook captures it all.
  - For the Plan 4 executor session `154bc22a-c328-4f67-b352-4ccb8535fd77`, once it has ended, delete its line from `<state>/captured`. Do the same for any later session that ended while listed but has work after its capture.
  - Then run `bash ~/.claude/plugins/cache/android-sdlc-skills/android-sdlc/<version>/skills/adlc-docs/scripts/backfill.sh --harness claude`.
  - Expected: `<state>/staged/` holds a note for each of those sessions. Logs are in `<state>/log/`.
- [ ] **Step 2: Fold in.**
  - In the worktree, run `android-sdlc:adlc-documenter` with a prompt starting `MODE: fold-in`, then `STAGED: <state>/staged` and `REPO: <worktree path>`.
  - Ask it to:
    - deduplicate `2026-11-06-3e97d016.md`, which is already partly folded as `docs/adlc/sessions/2026-11-06-issue-82-api37-fix.md`
    - reconstruct anything not captured from git, PRs and plans, labelled **Reconstructed**
  - It must cover:
    - Plan 4 Tasks 5–7 and the v1.0.0 release
    - the owner's playtest and bugs #151–#153
    - the autonomous finish-up and its decisions
    - the parallel subagent and merge-queue approach
    - the CI flake investigations (#168, #170)
    - the two machine restarts and the lost local state
    - the capture gap itself
- [ ] **Step 3: Privacy gate.** Run `bash <adlc-docs>/scripts/scan.sh docs/adlc "<state>"` until it exits 0, and `grep -rn "PRIVATE KEY\|MII" docs/adlc`. Expected: the scan exits 0 and the grep prints nothing.
- [ ] **Step 4: Commit, PR, lessons, clean-up.**
  - Commit `docs(adlc): fold in session notes`, then open the PR and merge it.
  - Save the documenter's lessons as memories, checking for duplicates first.
  - Delete the folded notes from `<state>/staged/` only after the merge.

### Task 7: Release 1.0.1

**Branch:** `release-please--branches--main` (PR #146)

- [ ] **Step 1: Check the release PR.** release-please refreshes #146 after each merge. Run `gh pr view 146 --json title,body`. Expected:
  - the title is `chore(main): release 1.0.1`
  - the changelog lists the fixes from #163, #156 and #167, and the other changes
  - `version.txt` reads `1.0.1` in the diff
- [ ] **Step 2: Merge.**
  - Wait for green checks; `pr-rules` exempts tagged release PRs.
  - Squash-merge #146.
  - Expected: a `Release` workflow run starts on `main`: `gh run list --workflow Release --limit 1`.
- [ ] **Step 3: [owner] Approve `sign-publish`.** The run waits on the `release` environment. The owner approves it in the GitHub UI (Task 8).
- [ ] **Step 4: Verify the release**, as Plan 4 Task 6 did for 1.0.0. Expected:
  - `gh release view v1.0.1` shows `SuperSnake-1.0.1.apk` and its `.sha256`, and the release is immutable
  - `gh attestation verify SuperSnake-1.0.1.apk --repo EastwoodNo1/SuperSnake` passes
  - the `verify-published` job is green
  - `aapt2 dump badging` prints `versionName='1.0.1'`, with the versionCode the formula gives (ADR-0011)

### Task 8: Owner batch and final state

**Branch:** the last checkbox ticks go in a small PR, `docs: close Plan 5`, closing #177's follow-up issue, or in Task 6's PR if it is still open.

- [ ] **Step 1: Send the owner one message** with these three items, with exact commands:
  1. **[owner]** Settings:
     - `! cd ~/AndroidStudioProjects/SuperSnake && scripts/github/apply-repo-settings.sh EastwoodNo1/SuperSnake`
     - then the same command with `--check`. Expected: `all settings match`. This applies #166, the required API 37 legs.
  2. **[owner]** Approve the 1.0.1 `sign-publish` job (Task 7 Step 3).
  3. **[owner]** Play on 1.0.1. This is Plan 4 Task 6 Step 7. On the `phone37` emulator:
     - `gh release download v1.0.1 --repo EastwoodNo1/SuperSnake` in a fresh temporary folder
     - `adb uninstall io.github.eastwoodno1.supersnake`
     - `adb install SuperSnake-1.0.1.apk`
     - Play to game over with at least one orb eaten, run `adb shell am force-stop io.github.eastwoodno1.supersnake`, and start the app again.

     Expected: the high score shows after the cold start; `adb logcat -d | grep -c 'FATAL EXCEPTION'` prints `0`; High scores and Settings each have a working Back button.
- [ ] **Step 2: Record the owner's outputs** in the closing PR's Evidence. Tick Plan 4 Task 6 Step 7 and this task.
- [ ] **Step 3: Final state.** Expected:
  - `gh pr list` is empty
  - `gh issue list` shows only issues the owner chose to keep, such as Task 3's
  - `git worktree list` shows only `main`
  - the folder `/Users/r03418/AndroidStudioProjects/.supersnake-finish` (the local handover and helper scripts) is deleted
  - `README.md` says the project is complete and Play publishing is the next step
