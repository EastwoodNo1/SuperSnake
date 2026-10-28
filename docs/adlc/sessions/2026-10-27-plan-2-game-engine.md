---
session: dfda0bef-0051-4b38-b52e-29d3857384f8
date: 2026-10-27 to 2026-10-28
harness: claude
branches: docs/plan-2-game-engine, feat/board-and-difficulty, feat/game-step, feat/turns-and-pause, feat/input-classification, feat/scores-and-settings, feat/game-session, feat/render-maths, feat/data-datastore, plan-2 review-fixes branch, docs/close-plan-2
evidence_status: Observed for the main-session chronology, PR numbers and the owner's decisions (digest). The digest was truncated in the middle (Tasks 5-7 and parts of several summaries), so details of those tasks are Reconstructed from later summaries. Subagent reports were not read.
---

# Session note: Plan 2 (game engine) written, designed with the owner, and built through subagents

## Goal
Write Plan 2 (game engine) following the active plan's "Start here". The owner then widened the goal: settle the storage dependency question and the game-feel rules before any subagent work. Once those were settled, execute the whole plan through subagents.

## What happened
1. **Plan 2 first draft.**
   - The main agent read Plan 1, the spec and the repo files.
   - It re-verified versions on the day: Kotlin stays at 2.4.20 because CodeQL caps Kotlin there, so Dependabot PR #41 stays open. kotlinx-coroutines is 1.11.0 and DataStore Preferences is 1.3.0.
   - It checked that the plugin templates show 0 drift.
   - It wrote a 9-task plan in a worktree and committed it locally.
2. **Owner redirect.** The owner asked three things:
   - Is okio the only way to do storage, and is it safe enough? If not, brainstorm alternatives.
   - Do a complete brainstorm of the game rules and edge cases found in other Snake apps.
   - Only after both decisions, move on to subagents.
3. **Research.**
   - Two Sonnet researchers ran in the background, one on storage and one on Snake edge cases.
   - The first edge-case report was thin, with searches but no pages opened, so it was sent back for a second pass.
   - The main agent verified the key storage claim itself: only DataStore Preferences needs okio, and `datastore-core` depends only on Kotlin, coroutines and AndroidX annotations. It read the published dependency metadata to confirm.
4. **Decisions with the owner, one topic at a time.**
   - Storage: DataStore core plus a small own text format, with no okio.
   - No grace on a wall hit, as in the original Nokia game.
   - A 3-2-1 countdown before the snake moves.
   - Fixed speed for the whole game.
   - The owner approved the full proposed list of food, game-over, interruption, smooth-movement and points rules.
5. **Design doc** written, and reviewed by a Sonnet "builder" reviewer who looked for anything it would have to guess. The agent then made and listed further calls for the owner to check, and the owner answered "yes".
6. **Plan rewritten** to match the design.
   - Rules C13-C25 were added to the rules file. C5 and C6 were kept and marked replaced.
   - Tests are named after the rule they prove.
   - A hard-stop checkpoint was added to Task 8 if DataStore core fails in a plain Kotlin module or pulls in any third-party library.
7. **PR #47** (plan and design).
   - The strict gate first failed with "SDK location not found", because a fresh worktree lacks the gitignored `local.properties`.
   - The plan's per-task workflow now copies that file into each new worktree.
   - All four required checks passed and the PR was squash-merged.
8. **Subagent-driven execution** of Tasks 1-8: one issue, worktree, Sonnet implementer, Sonnet reviewer and PR each.
   - PRs: #49, #51, #53, #55, #57, #59, #61, #63.
   - The main agent re-ran the gate and the coverage counters itself after each report.
   - A merge script in the workspace waits for the four checks and then squash-merges. It was later given a retry after a failure in the Task 6 and Task 7 merge waits.
9. **Final whole-range review** on Sonnet found no Critical issues and 4 Important ones. All of them are at the seam between the engine and the Android app. One fix PR (#65) addressed them, and a scoped re-review confirmed it.
10. **Task 9 (PR #67)** closed the plan with a rule-to-test table and a "Plan 3 contract" in the handoff. Everything was merged by main commit `850604b`. Coverage was 100% line and branch in `:domain` (154 branches, 315 lines) and `:data` (18 branches, 51 lines).
11. The owner asked how to continue. The answer: exit, open a new session and write Plan 3 from the plan's Start here.

## Key prompts
- Verbatim, project-only. Initial:
  > Write Plan 2 (game engine) following the active plan's Start here.
- Refinement: Initial -> Output -> Problem -> Refinement -> Result.
  - Initial: the plan above.
  - Output: a nine-task plan that used DataStore Preferences, with an okio checkpoint.
  - Problem: the owner doubted okio as a long-term dependency, and wanted the edge-case rules researched properly before any code.
  - Refinement (Reconstructed prompt intent - not verbatim): the owner paraphrased that okio should be judged on community size and safety, and alternatives brainstormed if it fails. A complete brainstorm of rules, including cases solved in other Snake apps, should make the engine smooth. After both decisions, move to subagents.
  - Result: okio removed, 13 new rules (C13-C25), a rewritten plan and a hard-stop proof in Task 8.
- Verbatim: "hit the count as in the original nokia game", "countdown 3-2-1, then move", "keep speed fixed like the original nokia", "yes, go with option 1". Short owner rulings that set the game-feel rules, each defaulting to the Nokia original.
- Verbatim: "ok, start task 1 once it's merged". This was the go signal for the autonomous subagent run.

## Decisions
- **Storage.**
  - Chosen: DataStore core plus an own text format, `settings.txt` and `high_scores.txt`.
  - Rejected: DataStore Preferences with okio (needs a spec §6 amendment and a second runtime library, pinned at 3.17.0). A third own-storage option was also offered, and the digest truncates it.
  - Rationale: Google's safe-write and corruption recovery are kept, and no third-party code is added. Okio itself looked healthy: 9,054 stars, recent pushes, no known vulnerabilities at the pinned version. The reason against it was the spec's rule of minimal dependencies.
  - Owner confirmed.
- **No grace period on a wall hit.** Owner ruling. Alternative: forgiveness near walls, which was proposed by the research.
- **Countdown 3-2-1** at every start and resume. Alternatives: move at once, or wait for the first turn. Owner chose the countdown.
- **Fixed speed.** The alternative was to speed up as the snake grows. Owner chose fixed, as in the original.
- **Turn queue.**
  - Two turns wait in a queue.
  - A new turn is checked against the last queued direction.
  - A repeat of the same direction is ignored.
  - A full queue replaces the last turn.
  - Based on research plus the agent's proposals, accepted by silence and then confirmed.
- **Agent calls for owner review.** One countdown everywhere, with TalkBack "3, 2, 1, Go". Pause clears queued turns. A long stall gives one step with no catch-up. If DataStore core fails, stop and ask. A win is saved like a loss, with a tie rule for the high-score table. The owner approved them with "yes".
- **Final-review rulings made by the agent.**
  - Fix `:data` visibility by using `api`.
  - Mark the game ended before the save starts, and absorb disk errors in `:domain`.
  - Do not change code for the one-shot frame events. Document a contract for Plan 3 instead.
  - Add a seam test with a hand-edited unsorted scores file.
  - Reword rule C20, which contradicted itself about queued turns surviving rotation: only snake, orb and score survive.
- **All subagents on Sonnet.** The project rule overrides the skill's default of the most capable model.
- **Process choice.** Implementer and reviewer were separate agents. The controller re-verified the gate, coverage and commit trailers itself.

## Failures and rework
- **Thin research.** The first edge-case report was opinion with no sources. It was detected by the main agent, sent back, and the second pass was better sourced. Gaps remain: nothing reliable was found on Google or Nokia Snake tail-cell and grace behaviour.
- **Fresh worktree had no `local.properties`.** The strict gate failed with "SDK location not found". Root cause: a gitignored file does not come with a new worktree. Fix: copy it in the plan's per-task workflow.
- **Plan checkbox gap in Task 4.** The report claimed Step 5 was done but the checkbox was unticked. The reviewer caught it. The agent confirmed that Tasks 1-3 had ticked theirs, so it was a real gap, and it was fixed in a scoped fix round.
- **Tests written after code in Tasks 6 and 8.** The failing-test-first rule was not strictly followed. The PRs say so, and it was saved as a lesson.
- **Merge-wait script failed on Tasks 6 and 7.** A retry was added. The exact cause is not in the visible digest.
- **Duplicate and stale subagent messages.** The Task 8 implementer repeated its report three times while it said it had background work. It was stopped so it could not act on stale state.
- **Four Important defects found only by the whole-range review.** Per-task reviews could not see the seams. See the lessons.
- **Contradictory rule C20** was found only at the final review.

## Human contribution
- Pushed back on an unexamined dependency and demanded research before the rules were final.
- Set the game-feel rules by reference to the Nokia original.
- Defined the sequencing: decisions first, then subagents.
- Approved the design twice and gave the go for autonomous execution.
- Set the hard stop for DataStore: the agent halts and asks if the proof fails.

## AI contribution
- Researched and verified storage claims from the published artifact metadata.
- Asked one question at a time and offered options with a recommendation.
- Wrote the design and the 9-task plan with named rule-to-test mapping.
- Orchestrated Sonnet implementers and reviewers, ran independent verification, and kept a ledger and merge script.
- Triaged the 28 deferred findings in the final review.
- Saved two memory lessons mid-session.

## Concepts and principles learned
- Verify a researcher's key claims yourself before they drive a decision.
- Encode one-shot data contracts (events, announcements) explicitly for the next plan.
- Make engine decisions live in `:domain` so ViewModels never branch.
- A whole-range review catches seam defects that per-task reviews cannot.
- A proof task with a hard stop turns an owner's constraint (no third-party library) into a checkable gate.
- Name tests after rules to get a traceable rule-to-test table.

## Durable lessons
- Research dependencies and edge-case rules with the owner before a plan is final, and default game-feel proposals to the Nokia original. The owner challenged okio and asked for a rules brainstorm. (Already saved as memory.)
- Implementer dispatches must demand the RED (failing) run before any code, and the controller must verify report claims. Tasks 6 and 8 skipped it, and one report claimed a ticked checkbox that was not ticked. (Already saved as memory.)
- A fresh git worktree lacks gitignored files such as `local.properties`. The per-task workflow must copy it, or the first gate fails.
- Run a whole-range review after all per-task reviews, because the four Important defects were all cross-task seams.
- Verify a subagent's key claim against primary artifacts (published dependency metadata) before a decision depends on it.

## Presentation candidates
- **"The owner questioned a dependency and the plan changed."**
  - Message: one human question (is okio safe, and is it the only way?) led to research, a verified alternative, a spec-compatible design and a hard-stop proof gate.
  - Example: the okio checkpoint was replaced with a DataStore-core proof in Task 8.
- **"Per-task reviews are not enough."**
  - Message: all four Important defects appeared only at the seams, in a final whole-range review.
  - Example: `:data` hid the types it exposes, so the app would not have compiled against it.
- **"Game feel by decision, not by default."**
  - Message: each feel rule was proposed with options, the owner ruled, and the rule became a numbered rule (C13-C25) plus a named test.
  - Example: 3-2-1 countdown, no grace, fixed speed.
- Visualization: a pipeline of owner decisions -> design -> plan -> 8 tasks (implementer -> reviewer -> verify -> PR -> merge) -> final review -> handoff.

## Evidence
- PRs: #47 (plan and design, closes issue #46), #49, #51, #53, #55, #57, #59, #61, #63 (Tasks 1-8), #65 (final-review fixes, issue #64), #67 (Task 9, handoff).
- Main commits: `74bbdbd` (#47 merged), `06eb257`, `48d871a`, `35a5bab`, `ede91d3`, `b995937` (Task 8), `850604b` (final).
- Files: `docs/superpowers/plans/2026-10-27-plan-2-game-engine.md`, `docs/superpowers/specs/2026-10-28-engine-feel-and-storage-design.md`, `docs/game-rules.md` (C13-C25, B1-B6), ADR-0008 (own storage format over DataStore core).
- Coverage: `:domain` 154 branches and 315 lines, `:data` 18 branches and 51 lines, all covered.

## Gaps and uncertainty
- The middle of the digest (Tasks 5-7 and parts of the summaries) was truncated. Their individual findings are not recorded here.
- The third storage option and several agent calls are cut off in the digest.
- The subagent reports and the ledger were deleted at the end of the session, so only git history and PR bodies remain as the record.
- The Task 6 and Task 7 merge-wait failures have no visible root cause.
- The session was long, with no explicit timestamps for most steps.
- This session's staged note will be folded in with Plan 3's first PR, as the session itself said.
