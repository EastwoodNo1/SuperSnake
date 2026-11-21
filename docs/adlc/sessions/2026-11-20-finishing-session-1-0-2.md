---
session: f877dcd1-a1bb-45dd-8b93-5fc6576f63d9
date: 2026-11-20
harness: claude
branch(es): docs/adlc-final, ci/codeql-gradle-cache, test/orb-samples-in-countdown, docs/plugin-v0.4.2, test/scores-pass (fix for the third flake), chore orchestrator and AGP branches, fix/board-after-bars
evidence_status: Observed from the session digest (commands, PR numbers, reported outputs). Later digest text is truncated in places. Command output was not re-verified by the note writer.
---

# Session note: project finish-up, flaky-test closure, 1.0.2 release

## Goal
The owner (EastwoodNo1) gave the orchestrator full autonomy to finish SuperSnake: capture and fold in both long sessions, tick the Plan 5 and Plan 4 boxes, mark the README complete, merge, and clean up. The owner then widened it step by step: close every GitHub issue and PR, tag and adopt plugin v0.4.2, fix the last issue, and close the release.

## What happened (chronology)
1. **Final ADLC fold-in (#184, closes #183).**
   - Verified state from the handover, the plan's "Start here" section, `git log`, `gh pr list` and `git worktree list`.
   - Backfilled the Plan 4 executor session by removing its id from `captured`. The capture gap is plugin issue #13: an id already in `captured` is never captured again.
   - Ran the documenter on Sonnet. The orchestrator re-ran the privacy gate and extra greps itself.
   - Plan 5 Tasks 1, 5, 6 and 7 were ticked. The 1.0.1 release was re-verified: checksum, attestation, `versionCode`.
   - Task 8 and Plan 4 Task 6 Step 7 stayed unticked because they need the owner.
   - Two commits passed hooks, and the pre-push zizmor and strict `check` passed. #184 merged before the API 37 legs finished, because the live ruleset did not yet require them.
2. **Issue #181 (CI flakiness) closed, reopened twice, then closed.**
   - CodeQL failed on `main` with `Could not find kotlin-gradle-plugins-bom`. Cause: it was the only Gradle job without `setup-gradle`.
   - #185 added a read-only Gradle cache to `codeql.yml`. It also added a `:checkWorkflows` rule, written test-first (4 of 32 failed, then 32 of 32 passed). The log showed `Cache hit`, then `Entries: 1 restored, 0 saved`.
   - #185's `ui-tests (phone-37)` leg then failed `AccessibilityTest.reduceMotionStopsAnimation`.
     - Cause: five orb samples took at least 3.3 s but the countdown lasts 3 s.
     - The fix (#186) failed locally before the change (`sample 4 taken after the countdown`) and passed 6 of 6, three times.
   - #186's `checks` job then failed in the DataStore test `GameSessionOverStoreTest`.
     - Cause: the cold start cancelled the old storage without waiting for it to close.
     - It was not reproducible locally (0 failures in 25 runs). The fix follows the `StoresTest` pattern, and the PR said so.
   - #188 (docs only) failed `ui-tests (phone-37)` in `LayoutScreenshotTest`. If the first pass scored a point, the landscape pass never saw "No scores yet".
     - #189 clears the scores per pass. RED was proven by seeding a score between passes.
   - On #189, `ui-tests (tablet-37)` hung. The log showed 65 of 66 tests with a logcat, and `SettingsTest.controlSchemeCanBeChosen` had none.
3. **Test Orchestrator (#194, closes #190) and AGP 9.5.2 (#192, closes #191).**
   - The orchestrator asked the owner before adding a test dependency, as the rules require. The owner chose it.
   - Each test now gets its own process: one wedged test fails alone in about 2 minutes instead of running into the 60-minute limit.
   - Cost: the UI legs are about 3 to 6 minutes slower. The full suite passed locally (66 passed). Checksums were cross-checked against Google's published hashes.
   - AGP 9.5.2 was published the same day and broke lint on every branch. It was bumped following the #123 procedure, with dependency-verification metadata regenerated.
4. **Rotation deadlock (#193, fixed in #195).**
   - Native stacks captured during the hang showed a deadlock. The main thread waited in `GLSurfaceView.onWindowResize` for the GL thread. The render thread waited for a surface sync that needs the main thread.
   - Trigger: hiding the system bars shrinks the padding, so the board resizes during its first-frame sync.
   - Fix: create the board only after the bars settle, or after the existing 1.5 s fallback (10 lines in `GameScreen.kt`).
   - Statistical evidence on the local `tablet37` emulator: before, 4 of 20 runs hung. After, 30 of 30 passed. The full suite passed with 66 tests.
5. **Plugin v0.4.2.**
   - android-sdlc-skills #19 synced three templates and added one sentence to the skill. `verify-templates.sh` printed `0 drift`.
   - The tag was annotated and dereferenced to the merge commit. SuperSnake #188 adopted it in `.claude/settings.json`, `CLAUDE.md` and `README.md`.
   - Local install was still blocked by an unpinned marketplace entry in the owner's user settings. This stayed an owner item and no user settings were edited.
   - The orchestrator wrote an eight-item handover for the plugin agent, outside both repos. Items: capture gap #13, install troubleshooting, required checks needing the settings script, a flaky-UI-test practice, Test Orchestrator guidance, new-stable-dependency blocking, the GL deadlock lesson, and a per-job setup-gradle check.
6. **Release 1.0.2.**
   - Release-please opened #196 after the #195 fix. Its `ui-tests (phone-31)` leg failed once in `LayoutScreenshotTest` (app never idle after rotating on the menu).
   - The rerun passed, and 12 of 12 local runs passed. It was treated as a one-off. The cause is not established.
   - The owner approved `sign-publish`. The orchestrator verified the published 1.0.2: immutable release, `shasum` OK, attestation verified, `versionCode='1000002'`, signer matching the repo's `RELEASE_CERT_SHA256`.
   - It installed 1.0.2 on the owner's `phone37` emulator. The owner reported that the new version works well.
   - Final state: no open issues or PRs, only `main` checked out.
7. A last request to fold in this session started the capture. The session id then goes into `captured`, so later work would repeat the gap in plugin issue #13.

## Key prompts
- Verbatim prompt: "fix the last issue that you can find in github, nothing more". It set a tight scope. The orchestrator fixed #193 only and left release PR #196 untouched until asked.
- Verbatim prompt: "what about the open issue #181, doesnt that needs to be taken care of aswell?". The owner challenged a premature close. This led the orchestrator to check #186's own failed CI and fix a second flake in the same PR.
- Verbatim prompt: "tag the plugin v0.4.2 and adopt it too".
- Reconstructed prompt intent - not verbatim: close all issues and PRs, then close the release. The orchestrator read this as merging the release PR #196 so that 1.0.2 gets published. It stated that reading before acting.
- Reconstructed prompt intent - not verbatim: the owner asked for a handover document and a ready-to-paste prompt for the plugin agent, then asked it to double-check completeness against new findings.
- Refinement chain: Initial (#185 closes #181) -> Output (CodeQL fix merged) -> Problem (#185's own phone-37 leg failed, and further flakes showed up on #186, #188 and #189) -> Refinement (reopen #181 each time, fix each cause with a RED) -> Result (#181 closed by #189, then the hang class handled by #194 and #195).

## Decisions
- **Merge #184 before the API 37 legs finished.** The live ruleset did not require them yet. Alternative: wait. Rationale: the rules allowed it. The orchestrator reported the pending legs and named the settings script as the owner fix.
- **Use the Test Orchestrator (#194).**
  - Alternatives: bound the tests another way, or only rerun.
  - The owner approved the new dependency after being asked.
  - Rationale: isolation turns a 60-minute hang into a 2-minute failure, at a 3 to 6 minute cost per leg.
- **Fix the rotation deadlock app-side (#195).**
  - Alternatives: test on a real device first, or work around it in tests only.
  - The owner told the agent to fix the last issue.
  - Rationale: the freeze is in Android's display code, so it plausibly also affects real devices.
- **Treat the #196 phone-31 failure as a one-off after a rerun and a clean local loop of 12.** This is a judgment call with no root cause (GAP below).
- **Bump AGP 9.5.2 immediately (#192).** It broke lint on every branch, and the #123 procedure was followed.
- **No subagents for the small CodeQL fix.** Rationale: it was faster done directly. A documenter subagent was used for the fold-in, on Sonnet.
- **Cleaning up the handover folder.** Planned and listed in the handover's own steps.

## Failures and rework
- #181 was closed too early through #185 and reopened twice. Each time a new test bug appeared in the same family: orb timing, DataStore reopen, score between passes. The lesson is to wait for every leg of the final run before claiming closure.
- Auto-merge merged PRs with red or pending legs (#184, #185, #188) because the API 37 legs were not yet required.
- A removal command was blocked as too broad. It was rewritten to download into fresh uniquely named folders.
- The orchestrator wrote a line in its own message that looked like a system notification, then corrected it in the next message.
- After a headless emulator boot, the owner's `phone37` no longer had the release app installed: it cold-booted and the snapshot was missing. The cause is uncertain (it may be this session's boot).
- #186's DataStore race was never reproduced locally.

## Human contribution
- Autonomy grant and scope control ("nothing more").
- Challenged premature closure of #181.
- Chose the Test Orchestrator dependency.
- Approved `sign-publish`.
- Playtested 1.0.2.
- Requested the plugin v0.4.2 release and a handover for the plugin agent.

## AI contribution
- Root-cause analysis from CI logs, logcat and native thread dumps.
- Test-first fixes with measured RED/GREEN, including statistical loops.
- Dependency checksum cross-checks.
- Release verification.
- Plugin template sync, a version bump and a handover document.
- Memory updates.

## Concepts and principles learned
- A flaky test is a bug until proven otherwise. Get a deterministic RED, or state the evidence honestly when none is possible.
- Intermittent failures need statistical before/after evidence (4 of 20 versus 0 of 30).
- Per-test process isolation bounds the blast radius of a hang.
- A hosted GL view inside Compose can deadlock when resized during first-frame sync.
- Required checks only take effect after the settings script runs.
- Close an issue only after every leg of the final run is green.

## Durable lessons
- Always wait for every leg of the final CI run before reporting an issue closed, because #181 was reopened twice after an early close and auto-merge ignores legs the ruleset does not require.
- Always loop an intermittent test before and after a fix and report the counts, because one green run proves nothing.
- Never boot or reuse the owner's AVD for test runs, because the owner's installed app and snapshot were lost.
- Keep the capture gap in mind: a mid-session capture puts the id in `captured`, so later work is lost (plugin #13).
- Ask the owner before adding a test dependency, and cross-check new artifact checksums against the publisher's hashes.

## Presentation candidates
- **Headline:** "One issue, four causes: how closing a flaky-CI issue three times found a real deadlock."
  - Key message: each reopen came from the agent's own evidence. Statistical RED/GREEN made the final fix credible.
  - Example: the #193 table (4 of 20 hung, then 30 of 30 passed).
- **Headline:** "Autonomy with guardrails."
  - Key message: full autonomy still stopped at approvals, dependencies and user settings.
  - Example: the AskUserQuestion before the Test Orchestrator.
- **Headline:** "When the process documents its own gap."
  - Key message: the capture gap (plugin #13) was found, worked around by hand, and written into the handover.

## Evidence
- PRs in SuperSnake: #184, #185, #186, #188, #189, #192, #194, #195, #196. The merge commits named in the digest are `1d91a5b`, `4270fe3`, `0fb0056`, `28df360`, `3ce133a`.
- Issues closed: #181, #183, #187, #190, #191, #193. Plugin PR #19 and the v0.4.2 release.
- Release 1.0.2: Release run 44804016463.
- Files: `.github/workflows/codeql.yml`, `build-logic/checks/.../WorkflowSafety.kt`, `GameScreen.kt`, `docs/versions.md`, `gradle/verification-metadata.xml`, `.claude/settings.json`.
- Handover for the plugin agent: written outside the repos, not committed.

## Gaps and uncertainty
- The root cause of the #196 phone-31 `LayoutScreenshotTest` hang is unknown.
- The #186 DataStore race has no local reproduction.
- Whether the owner's `phone37` app loss was caused by this session is unknown.
- The #195 fix has not been tested on a real device.
- The playtest outputs were reported as "works well", not as command output.
- Work after this capture will not be recorded, because the session id is in `captured`.
- The digest was partly truncated, so some table rows and the full handover prompt were not seen.
- The exposed-key rotation (GAP-032) was asked of the owner. No answer is visible.
