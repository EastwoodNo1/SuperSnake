# Project Evolution Timeline

Chronological record of meaningful development events. Use relative sequence numbers if exact dates are unavailable. Never invent chronology.

Status of each row is Observed (O), Reconstructed (R), Unknown (U) or Prospective (P). Dates are UTC from git or GitHub unless noted. Commit and PR numbers refer to `EastwoodNo1/SuperSnake` unless the plugin repo `android-sdlc-skills` is named.

| Seq. | Date / Session | Cycle | Lifecycle Phase | Trigger | Objective | Key Activity | Output | Validation / Result | Next Step | Evidence |
|---:|---|---|---|---|---|---|---|---|---|---|
| 1 | 2026-10-20, session 37cc1e57 | none | none | Opened a session | Unknown | Two `/model` commands, then `/exit` | Nothing | No-op (O) | n/a | [record](sessions/2026-10-20-no-op-session.md) |
| 2 | 2026-10-20, session b283293d | none | none | Question about the newest model | Check for a newer model in the tool | AI claimed a model was "latest" from memory; the human corrected it | Nothing | Non-project (O) | n/a | [record](sessions/2026-10-20-model-selection-question.md) |
| 3 | 2026-10-20 to 22, session 9666aa57 | CYCLE-001 | Requirements, design | Owner's brainstorming prompt (PROMPT-001) | Settle what to build and why | One-question-at-a-time brainstorm: look, purpose, workflow, rules source, stack, release, review policy | Design spec draft | Owner approvals per question (O from session note) | External review | [record](sessions/2026-10-20-brainstorm-design-and-plan-1.md) |
| 4 | 2026-10-22 | CYCLE-002 | Review, design | Owner asked for an external review (PROMPT-003) | Find flaws before planning | Independent agent: 1 blocker, 7 major, 9 minor. AI verified the blocker (CodeQL Kotlin cap) itself | Revised spec; ADR-0006 content; approved design `e900708` | Owner approved all fixes (O) | Write Plan 1 | commit `e900708`; session record |
| 5 | 2026-10-22 | CYCLE-003 | Planning | Approved spec | Executable foundation plan | Plan 1 written with 10 tasks; `.gitignore` overwritten by `git init`, then restored | `7ca0478` Plan 1 | Self-found error fixed (O) | Make plan session-independent | commit `7ca0478` |
| 6 | 2026-10-22 to 23 | CYCLE-003 | Planning (rework) | Owner: plan must not depend on the session (PROMPT-004, 005) | Fresh-session handoff | "Start here" section, resume rules, README and CLAUDE.md, cold-read subagent test, fixes | `c51cd73`, `730e1e7` and prerequisite commits `380cda8`..`b32fb27` | Cold-read agent found gaps; fixed (O) | Execute Plan 1 | commits in `git log` (O); session record |
| 7 | 2026-10-23 | CYCLE-004 | Implementation | Owner: execute Plan 1 (PROMPT-006) | Task 1 skeleton, JDK 25 pinned by mise, zizmor added to spec | Commits `2277541`, `916fd16`, `26d66b6`; strict-Gradle allowlist; brew install failed, switched to mise | Skeleton, `mise.toml`/`mise.lock`, `scripts/gradle-strict.sh` | Allowlist shown to bite on a new deprecation (O from session note) | Task 2 | commits (O) |
| 8 | 2026-10-26 11:03 | CYCLE-004 | Implementation | Task 2, after owner "go" (PROMPT-012) | Repo rules, security policy, PR template | PR #2 | Public repo baseline | Merged (O) | Task 3 | PR #2 |
| 9 | 2026-10-26 11:11 | CYCLE-004 | Implementation | Task 3 | Quality gates proven on one type | `802bffe` Direction; `c0f9872` Kover 100%, ktlint CLI, strict Lint (PR #4) | Gate config | TDD red then green (O from session note) | Task 4 | PR #4, commits |
| 10 | 2026-10-26 11:24 | CYCLE-004 | Design change | Repo allowed rebase only; owner wanted squash | Reverse the merge-method decision | PR #8 switches settings, ruleset, spec, plan | ADR-0007 (squash) | Merged (O) | Continue Tasks 4 to 7 | PR #8, ADR-0007 |
| 11 | 2026-10-26 11:18 to 12:03 | CYCLE-004 | Implementation | Tasks 4 to 7 | Own checkers, hooks | PRs #6, #10, #12 (hardening), #14, #16 (fix), #18 (hooks), #20 (hardening) | `build-logic/checks`, `.githooks`, `scripts/bootstrap.sh` | Each PR through implementer then reviewer; two hardening PRs followed review findings (R) | Task 8 | PR list |
| 12 | 2026-10-26 13:09 to 13:49 | CYCLE-004 | Implementation | Task 8, 9 | CI, CodeQL, Dependabot, dependency verification, required checks, onboarding docs, ADRs 0001 to 0007 | PRs #22, #25, #27, #30. A `workflow` token scope was missing and the owner granted it. PR #23 "update stuff" was closed unmerged | Workflows, 4 required checks, onboarding | Merged (O) | Task 10 | PRs |
| 13 | 2026-10-26 13:39 to 15:06 | CYCLE-004 | Feedback | Dependabot PR #28 failed checks | Understand why | Cause: Kotlin bump past the CodeQL cap (ADR-0006). Left open as a reminder (DEC-021); closed 15:06 by the split in PR #40 (R) | Reminder, then a split Kotlin group | Root cause stated in session note (O) | Plan 2 re-check | PR #28, #40, #41 |
| 14 | 2026-10-26 14:22 | CYCLE-004 | Design | Owner asked about release-please (PROMPT-011) | Adopt release-please with an App token | PR #32 (docs only; implementation in Plan 4) | Spec §8 | Merged (O) | Plan 4 | PR #32 |
| 15 | 2026-10-26 14:26 to 14:47 | CYCLE-004 | Implementation | Task 10 | Plugin repo `android-sdlc-skills` v0.1.0 | Plugin PRs #2, #4 (CI shellcheck fix, template resync); SuperSnake PRs #34 (install), #36 (install docs, gh bump) | Plugin v0.1.0 installed | Merged (O) | Final review | PRs |
| 16 | 2026-10-26 14:56 to 15:23 | CYCLE-004 | Review, release | Final whole-plan review | Close Plan 1 | PR #39 marks Plan 1 complete and records an open release design point; #40 re-runs PR rules on edits; plugin PR #6 v0.1.1 with tag protection after owner approval; PR #43 pins v0.1.1 | Plan 1 complete | Merged (O) | Plan 2 not started | PRs |
| 17 | 2026-10-27 14:10 to 15:17 | CYCLE-005 | Design, implementation (plugin repo) | Owner wants portable automatic lifecycle docs | adlc-docs skill, agent, hooks | Plugin PR #8: spec, plan, spike, 16 commits, hardening wave, CI fix | Plugin 0.2.0 | 88 tests pass; CI green; real-CLI smoke test (R, from PR body) | Adopt in SuperSnake | plugin PR #8; [record](sessions/2026-10-27-adlc-feature-build-reconstructed.md) |
| 18 | 2026-10-27 | CYCLE-006 | Adoption, documentation | Plugin 0.2.0 available | Adopt ADLC docs in SuperSnake | Issue #44; commit `ae6c44d` with templates; this first fold-in with historical reconstruction | `docs/adlc/` populated | Pending validation: privacy scan by the main agent (P) | PR for the adoption; Plan 2 | issue #44, `ae6c44d` |

## Chronology Notes

- Sessions 9666aa57 and 68b8cc9f span several days; the session notes do not map every action to a day. Git commit dates anchor the rows above (O).
- Everything from PR #2 on happened on 2026-10-26 (UTC), within about four and a half hours: PR creation times 11:03 to 15:16 and the last merge at 15:23.
- Issue numbers and PR numbers share one counter, so each PR number directly follows its issue (for example issue #1 and PR #2 are a pair with matching titles; observed in the lists).
- No game feature beyond the `Direction` type exists yet (O, from the git log). Plan 2 has not been written (O, plan "State at handoff").

## Update: Plans 2 and 3 (second fold-in, 2026-10-28)

| Seq. | Date / Session | Cycle | Lifecycle Phase | Trigger | Objective | Key Activity | Output | Validation / Result | Next Step | Evidence |
|---:|---|---|---|---|---|---|---|---|---|---|
| 19 | 2026-10-27 to 28, session dfda0bef | CYCLE-007 | Planning, design | Write Plan 2 from Start here (PROMPT-015) | Plan the game engine | Plan draft; owner redirect on okio and edge cases (PROMPT-016); research; decisions; design doc | Plan 2, design doc, ADR-0008, rules C13-C25 | Owner approved; PR #47 merged with four checks (O) | Execute | PR #47; [record](sessions/2026-10-27-plan-2-game-engine.md) |
| 20 | 2026-10-27 to 28, same session | CYCLE-007 | Implementation, review | Go signal "start task 1 once it's merged" (PROMPT-017) | Build Tasks 1-8 through subagents | Implementer, reviewer, verify, PR and merge per task | PRs #49 to #63 | Gate and coverage re-run by the controller (O) | Final review | PRs #49, #51, #53, #55, #57, #59, #61, #63 |
| 21 | 2026-10-28, same session | CYCLE-007 | Review, closure | All tasks merged | Find cross-task defects, hand off | Whole-range review: 0 Critical, 4 Important; fix PR; Task 9 handoff | PRs #65, #67; `:domain` 154 branches and `:data` 18 branches covered | Re-review confirmed fixes (O) | Plan 3 | `850604b` |
| 22 | 2026-10-28 | CYCLE-008 | Planning, design | Plan 3 from Start here (PROMPT-018) | Plan the Android app | Three research subagents; three owner questions; contrast fix | Plan 3, ADR-0009, ADR-0010 (commit `e11d295`) | Owner approved; execution in a fresh session (R, reported by the main agent) | Execute Plan 3 | [record](sessions/2026-10-28-plan-3-planning-reconstructed.md) |

Notes: the Plan 2 session record is a captured note whose digest was truncated in the middle (GAP-009). The Plan 3 record was reconstructed, because the session had not ended.

## Update: AGP 9.5.0 bump (third fold-in, 2026-10-28)

| Seq. | Date / Session | Cycle | Lifecycle Phase | Trigger | Objective | Key Activity | Output | Validation / Result | Next Step | Evidence |
|---:|---|---|---|---|---|---|---|---|---|---|
| 23 | 2026-10-28, Plan 3 execution session (not yet captured) | CYCLE-009 | Maintenance, feedback | AGP 9.5.0 became stable; lint's AndroidGradlePluginVersion check started failing `:app:lintDebug` (reported by the main agent, R) | Keep the strict gate green before Plan 3 Task 1 | The owner chose a separate AGP bump PR before Task 1 (DEC-041); issue #71, branch `build/agp-9.5.0` | Commits `5d55438` (bump) and `c3fe5e5` (spec and Plan 3 record the new version) | Pending validation: checks on the PR | Plan 3 Task 1 (#70) | issue #71, `5d55438`, `c3fe5e5` |

Notes: this fold-in also copied two staged notes as session records: the ADLC feature session (an observed capture that supersedes the reconstructed record of the same day as the better source, GAP-014) and the Plan 3 planning session (observed capture, closes GAP-012). The AGP session itself has no staged note yet; row 23 rests on the main agent's context and git (R).

## Update: Plan 3 execution, Plan 4 start and the Plan 4 review (fourth fold-in, 2026-11-04)

| Seq. | Date / Session | Cycle | Lifecycle Phase | Trigger | Objective | Key Activity | Output | Validation / Result | Next Step | Evidence |
|---:|---|---|---|---|---|---|---|---|---|---|
| 24 | 2026-10-28 to 2026-11-02, session c5ff6184 | CYCLE-010 | Implementation, review, validation | Opening prompt to run Plan 3 subagent-driven (PROMPT-021) | Build the playable app (10 tasks) | AGP bump first (#72), Tasks 1 to 4 (#73, #76, #77, #79), Task 5 CI UI tests with 5 fix rounds (#81), Tasks 6 to 9 (#88, #90, #92), Task 10 (#94) | Plan 3 closed; guard script `scripts/check-ui-test-results.sh`; `HoldableClock`; plugin v0.3.0 adopted | Per-task review, whole-range review, CI. API 37 legs informational (DEC-043) (O for the note's own summaries; Tasks 6 to 9 R) | Full screen, then Plan 4 | PRs #72 to #94; issue #82 |
| 25 | 2026-11-02, same session | CYCLE-010 | Implementation, review | Owner asked for full screen (PROMPT-024) | Full-screen play; follow-up fixes | StoresTest flake fix (#105); full screen (#106) broke Restart and Play again, caught in review, fixed with a generation counter and RED tests; issues #97 to #104 filed | PRs #105, #106 | Review caught the regression before merge (O) | Plan 4 | PR #105, #106 |
| 26 | 2026-11-02, same session | CYCLE-010 | Planning, design | Owner asked for online research on release practice (PROMPT-023) | Plan the release | Research, owner confirmation, ADR-0011, ADR-0012, Plan 4 written and merged (#108); Tasks 1 to 3 merged (#110, #112, #114) | Plan 4, ADR-0011, ADR-0012, `docs/release.md`, `release-smoke` job | Owner merge comment on #108; checks on each PR (O) | Task 4 (owner-gated steps) | PRs #108, #110, #112, #114 |
| 27 | 2026-11-04, issue #115, branch `docs/plan-4-review-fixes` | CYCLE-011 | Review, rework (plan) | Owner asked whether Plan 4 had been reviewed; answer: only by the owner (PROMPT-025) | Review the plan independently before Task 4 | Sonnet subagent review: "holds with fixes", 4 critical, 8 important, minors; main session spot-checked 4; fixes applied; second Sonnet re-review of the diff | Plan and `docs/release.md` fixes (DEC-049 to DEC-052); setup-gradle bump split to a `ci:` PR | Spot-checks C1, C4, I1, I5 confirmed (O, reported by the main agent); re-review of the diff: ready for Task 4, no blocking fix (O, reported by the main agent) | Task 4 | issue #115 |

Notes: the review happened after Tasks 1 to 3 had merged, not before execution started. Rows 24 to 26 rest on a capture whose digest was size-capped (GAP-016).

## Update: release-smoke resume fix (fifth fold-in, 2026-11-05)

| Seq. | Date / Session | Cycle | Lifecycle Phase | Trigger | Objective | Key Activity | Output | Validation / Result | Next Step | Evidence |
|---:|---|---|---|---|---|---|---|---|---|---|
| 28 | 2026-11-05, issue #117, branch `smoke-resume` (main agent context, SRC-014) | CYCLE-012 | Debugging, implementation, validation | `release-smoke` timed out on PR #116 and on a main run; the API 31 emulator started a system settings SIM dialog, which paused the game, and the smoke never tapped Resume | Make the smoke survive a system interruption | `scripts/release-smoke.sh` taps the game's own Resume, restricted to the game's package; two new tests (`resumesAfterAnInterruption`, `ignoresResumeFromOtherPackages`); Plan 4 test counts updated to 27 in Task 5 Step 4 | Script fix and tests | RED 1 failure, then GREEN 17 passed (as reported by the main agent, R); CI result Pending validation | Merge, then Plan 4 Task 4 | issue #117, PR #116 |
| 29 | 2026-11-05, same context | CYCLE-012 | Review, process | PR #116 was reported ready while the informational API 37 legs were red | Record a reporting error | The agent called #116 ready; the owner said "PR 116 isn't green"; the agent corrected its report; the safety classifier blocked the agent's merge command, so the owner merged #116 | Corrected report; owner merge | Red legs were the known #82 goldfish crash (`hasReadColorBufferDma`), red on every main run since 2026-11-02 (R, main agent) | n/a | PR #116, issue #82 |

Note: session 3c11e9a7 (2026-11-05, plugin `/plugin` and `/reload-plugins` only) was also folded; it is a no-op record.
