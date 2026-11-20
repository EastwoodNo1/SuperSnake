---
session: 35559d8c-dea6-4515-a650-94781a5140a4
date: 2026-10-28 to 2026-11-20 (one long session, resumed after several context compactions)
harness: claude
branch(es): docs/plan-3-app, docs/plan-4-review (fixes), fix/smoke-resume, ci/setup-gradle-641, fix/smoke-replay, build/gradle-991, plus coordination of ci/release-settings and the issue #82 work; finish-up branches under subagents
evidence_status: Observed for the actions and PR/issue numbers named in the digest and its compaction summaries. Reconstructed for the middle of the session, because the digest omits much of the AI output. Unknown for exact outcomes of the finish-up phase (2026-11-19 to 2026-11-20), which was still running when the digest ends.
---

# Goal

The session started as "Write Plan 3 (App)". It grew into the main coordinating session for the project. The scope then covered:

1. Writing Plan 3.
2. Independently reviewing and fixing Plan 4 (release).
3. Fixing CI blockers (release-smoke flakes, API 37 emulator, Gradle/AGP lint gates).
4. Coordinating two parallel sessions: Plan 4 execution and issue #82.
5. From 2026-11-19, an autonomous finish-up of all open issues and PRs through subagents.

# What happened (short chronology)

## 2026-10-28: Plan 3 written

- The owner asked for Plan 3 using the writing-plans skill, following the active plan's Start here section.
- The AI read the specs, the Plan 2 contract and the domain/data code. It dispatched three research subagents: libGDX, AndroidX versions, and CI emulator plus 16 KB page alignment.
- The research showed that both navigation libraries pull in kotlinx-serialization, which the spec bans.
- The owner answered three questions, one at a time:
  - own back stack
  - keep the emulator-runner action, against the AI's recommendation
  - synthesized beeps
- The plan has ten tasks. ADR-0009 (back stack) and ADR-0010 (synthesized sound) were added, and the spec was updated.
- The AI computed the mockup wall colour's contrast. It was 1.17:1 against the orb, so the colour changed to #2266CC.
- Steps done: fold-in, issue #68, PR #69 with evidence, squash-merge after the required checks.
- The AI recommended executing Plan 3 in a fresh session. The owner agreed.

## Early November: Plan 4 review and fixes

- The owner asked for an agent review of Plan 4 and whether one had been done. The AI checked git and the logs: only an owner review existed.
- A Sonnet reviewer found issues. The AI fixed them in PR #116 and ran a second review.
  - The settings script compared arrays unsorted, so the expected check order was wrong.
  - The Task 4 settings steps were rewritten: a `--check` mode, and owner-run steps.
  - Recovery notes were added.
  - A merged-task resume rule was added.
- The reviewer reported a tag object SHA as a commit SHA. The AI dereferenced it to the commit before pinning.
- release-smoke failed on #116. A system dialog on the API 31 emulator paused the game, and the smoke never tapped Resume. The same failure had occurred on main before.
- The owner then said "PR 116 isn't green". The AI had called the PR ready without checking the red API 37 legs.
  - Investigation showed that only the informational API 37 legs were red and the required checks passed.
  - The AI owned the omission and from then on lists every red check.
- The owner merged #116. Follow-up PRs:
  - #119/#117 (smoke Resume handling)
  - #120 (setup-gradle v6.4.1 pin bump)
  - #130 (retap when the Play tap is swallowed, with 2 new script tests)
  - #129 (Gradle 9.9.1 bump)

## 2026-11-06 to 11-13: coordinator role

- The owner started two new sessions: Plan 4 execution and issue #82 (API 37 emulator). The AI chose to coordinate them.
- It used SendMessage to the sessions, a terminal-tab rename, and a Monitor watcher script on terminal agent events. The watcher went through several fixes: duplicates, and idle reminders misread as permission prompts.
- Issue #82 session: root cause was an AVD config with `target=android-0`. The fix rewrites it before emulator launch and uses the google_apis image. Both API 37 legs passed 61/61 on three consecutive runs (#124).
- The owner @-mentioned a private key file in the Plan 4 session. The harness inlined the key into that transcript. The session reported the key as burned and said it must be rotated. The AI later saved a lesson about it.
- The AI declined to run the settings script itself, even in `--check` mode, because the plan says the owner runs it. It gave the owner a `!` command instead.
- The Gradle 9.9.1 bump failed in CI on a Kover `Task.getAnt()` deprecation. The local run had missed it because tasks were up to date. The owner chose to allowlist it. The AI filed an upstream Kover issue and corrected that issue's body after verifying.
- The owner asked whether the capture hook really saves notes. The AI investigated and found that a resumed session loses work done after its first capture. It filed android-sdlc-skills#13.

## 2026-11-19 to 11-20: finish-up

- Mid-way, the owner wrote: "i want to finish up the whole project, solving all bugs and issues ... can you recommend an agentic and autonomous approach which is based on best practice for ADLC". Then: "you have full autonomy, i want to interact as little as possible and just finish up".
- The AI dispatched Sonnet subagents in parallel for:
  - #153 (no way back)
  - #151 (lost high score)
  - #152 (crash)
  - CI issues #99, #98 and #41
  - #104, #103, #100
  - #101 then #102
  - #82 PR 2 (make the API 37 legs required)
  - CI dependency flakes
  - #97 sturdier tests
  - the Compose BOM bump
  - a plugin template sync to v0.4.1
- It merged PRs with `--squash --auto`, replaced a Dependabot bump with a verified one, and opened a PR to close Plan 4.
- A laptop power-down interrupted the run. The AI recovered state from git and `gh`, paused all subagents on request, then resumed.
- The session ends with the AI checking what was left and syncing plugin templates.

# Key prompts

All are reconstructed or short, with no private data. Verbatim quotes below passed the privacy filter.

- Verbatim prompt: "Write Plan 3 (App) with superpowers:writing-plans, following the active plan's Start here."
- Verbatim prompt: "review this plan with sub-agent and make sure the plan will hold the implementation, but before doing that, has a review already been done?"
  - Output: the AI found that no independent review existed and then ran one.
  - Problem: the owner's next prompt used "plan 3" where Plan 4 was meant. The AI interpreted it as Plan 4 and said so.
- Verbatim prompt: "PR 116 isn't green"
  - Initial: the AI said the PR was ready.
  - Problem: the AI had not checked the informational red legs.
  - Refinement: it investigated, explained the red legs and asked how to proceed. It now lists every red check in "ready" reports.
  - Result: a durable lesson (report-every-red-check).
- Verbatim prompt: "i meant to rename the terminal tab name, not the claude session"
  - The AI had renamed sessions instead of terminal tabs, and it then fixed this.
- Verbatim prompt: "you have full autonomy, i want to interact as little as possible and just finish up, you can initiate and coordinate as many subagent as needed to finish up asap"
  - This started the finish-up phase, run as a pipeline of subagents and auto-merges.
- Reconstructed prompt intent - not verbatim: the owner asked for two ready-made prompts so that the owner and AI could fix issue #82 and continue Plan 4 in parallel. They also asked whether subagents or new sessions are better. The AI recommended new sessions.

# Decisions (alternatives and observable rationale)

- Own back stack instead of navigation-compose or navigation3. Rationale: both pull in the banned kotlinx-serialization. The owner chose it.
- Keep ReactiveCircus emulator-runner, against the AI's recommendation of an own script. The owner chose it. The AI noted that the action had no recent issue activity and said so honestly in the PR.
- Synthesized beeps instead of audio assets (ADR-0010).
- Wall colour changed from the mockup value for contrast, measured by script.
- Plan 4 review: an independent agent review, then a second review after the fixes, before executing.
- Allowlist the Kover `Task.getAnt()` deprecation plus an upstream issue, instead of failing the strict gate or waiting. The owner chose this.
- Separate bump PRs for the setup-gradle pin, Gradle 9.9.1 and AGP, because a new stable release can break the lint gate.
- The AI declined to run the settings script, following the plan's owner-only rule.
- New sessions rather than in-session subagents for the Plan 4 and #82 work. The AI recommended it and the owner took it.
- 2026-11-19: the owner granted full autonomy, so the AI took decisions on the owner's behalf (finish-up pipeline). The AI also said that approving a release environment stays human.

# Failures and rework

- Called #116 ready while informational legs were red. Caught by the owner. Fixed by changing how "ready" is reported.
- release-smoke flakes, two distinct causes. Both were root-caused with artifacts and fixed test-first (RED then GREEN, 17 then 19 tests):
  1. A system dialog paused the game and Resume was never tapped.
  2. The Play tap was swallowed and never retried.
- Subagent factual error: a tag object SHA given as a commit SHA. Caught and dereferenced by the AI.
- A local Gradle run missed a deprecation. The CI strict gate caught it. Lesson: use `--rerun-tasks` with the deprecation trace to reproduce.
- A Kover issue body claimed the wrong modules. The AI verified and edited it.
- The key file was exposed by an @-mention in another session. Rotation was recommended.
- The watcher misread idle notifications as permission prompts and sent duplicates. The AI fixed the deduplication.
- Misrouted SendMessage to a socket belonging to another project's session.
- Capture limitation found: a resumed session loses later work. Filed upstream.
- A laptop power-down interrupted autonomous work. The AI recovered from git, `gh` and the worktree list, which is why "progress lives in git" mattered.

# Human contribution

- Set the direction and priorities, and chose between options at the decision points.
- Overruled the AI on the emulator runner.
- Asked the review question and caught the premature "ready".
- Held the owner-only steps: the settings script, secrets, merging when the classifier blocked the AI.
- Granted autonomy for finish-up.

# AI contribution

- Research subagents, plan writing, and a self-review before commit.
- Review orchestration and the fixes that followed.
- CI failure root-causing from logs and artifacts.
- Coordination of the parallel sessions.
- Fold-ins, issue and PR hygiene, and a parallel subagent pipeline for finish-up.

# Concepts and principles learned

- A plan reviewed by an independent agent before execution catches contract and ordering errors.
- Research dependencies, including transitive ones, before choosing a library.
- Informational failures still belong in a "ready" report.
- Reproduce flakes from artifacts, then fix them test-first.
- Local green is not CI green: reproduce the gate's flags.
- Never @-mention secret files.
- Coordinating sessions through events has failure modes (misrouting, duplicate or misread events).
- A resumed session's capture gap is a process limitation to record.

# Durable lessons (candidates for assistant memory)

- Always list every red check, informational ones included, in a "ready" report, because the owner treats green as the whole PR.
- Always dereference annotated tags to commits before pinning a SHA, because a reviewer or subagent may give the tag object.
- Always re-run strict gates with `--rerun-tasks` and a deprecation trace to match CI, because up-to-date tasks hide deprecations.
- Never @-mention key or keystore files, because the file contents are inlined into the transcript.
- Verify subagent report claims against the source, because they contain factual errors.
- Recommend new sessions for long parallel workstreams, with the main session coordinating.

# Presentation candidates

- Headline: "Review the plan before you build it."
  - Message: an independent review plus a re-review found real ordering and contract errors.
  - Example: the settings check order, and the tag-object SHA.
- Headline: "'Ready' means every check."
  - Message: the owner's one-line correction changed the reporting rule.
- Headline: "From one session to a fleet."
  - Message: planning, then coordination, then autonomous finish-up with subagents. The cost was misrouted and misread events, and the benefit was parallelism.
- Headline: "Test-first fixes for a flaky smoke test."
  - Example: RED then GREEN script tests for the Resume and retap cases.
- Headline: "Own back stack."
  - Message: research found a transitive-dependency conflict and the owner chose to avoid the dependency.

# Evidence

- PRs: #69 (Plan 3), #116 (Plan 4 review fixes), #119, #120, #124, #127, #129, #130, #163, #166 and others in the finish-up.
- Issues: #68, #82, #115, #117, #126, #151, #152, #153; Kover upstream #842; android-sdlc-skills#13.
- Files: `docs/superpowers/plans/2026-10-28-plan-3-app.md`, `docs/superpowers/plans/2026-11-02-plan-4-release.md`, `docs/adr/0009-own-back-stack.md`, `docs/adr/0010-synthesized-sound.md`, `scripts/release-smoke.sh`, `scripts/test-release-scripts.sh`, `scripts/gradle-strict.sh`, `docs/versions.md`.
- Commits named in the digest: c0e7e61 (Plan 3 plan merge), 44fbf63 (#130).

# Gaps and uncertainty

- The digest omits most AI text and tool output, so details of the finish-up subagents' results are not reconstructable from it. Final PR and issue outcomes after 2026-11-19 are Unknown.
- Which model wrote which commit: the digest shows both Opus 5.5 and Sonnet 5.5 attribution lines in different places. Unknown.
- The session was resumed several times. Per android-sdlc-skills#13, this note may cover only part of the work. Earlier fold-ins captured part of it as reconstructed notes.
- Whether the exposed key was rotated is not shown in the digest.
