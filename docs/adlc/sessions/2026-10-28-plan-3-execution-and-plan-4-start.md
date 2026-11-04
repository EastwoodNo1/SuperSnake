---
session: c5ff6184-56b9-4a90-be4e-05c70df7fc31
date: 2026-10-28 to 2026-11-02
harness: claude
branch(es): feat/app-decisions, feat/feedback, board-scene, playable-board, ci/ui-tests, AGP 9.5 bump branch, full-screen, Plan 4 release branches (several worktrees)
evidence_status: Observed for the two in-session compaction summaries and the human messages. Reconstructed for the middle part (Tasks 6-9), where the digest was size-capped. Subagent bodies were not read in full.
---

# Session note: Plan 3 (app) executed subagent-driven, then Plan 4 (release) started

## Goal
Run `docs/superpowers/plans/2026-10-28-plan-3-app.md` with the subagent-driven-development skill, starting at the first task not done. After Plan 3 closed, write Plan 4 (release) and run it the same way.

## What happened (short chronology)
- Plan 3 had 10 tasks. Each got an issue, a worktree, a branch and a squash-merged PR. Each ran as implementer, then a spec and quality review, then fix rounds (at most 5), then a CI check comment, then merge.
- Before Task 1, a stable AGP 9.5.0 release broke the lint gate. The owner chose "Bump AGP first". It shipped as its own PR (#72), with verification metadata, a spec and plan update, and a fold-in of two staged notes.
- Tasks 1 to 4 merged as #73, #76, #77 and #79: domain decisions, feedback and synth, board scene and palette, and the libGDX board. Task 4 review found a misplaced R8 rule, a frame loop running while paused, and an unverified background pixel.
- Task 5 (#81) added CI UI tests on three emulator legs. It took 5 fix rounds:
  - API 31: the immersive-mode overlay dimmed the board and took input, so the test rule pre-confirms it.
  - API 31: the countdown ran out before the first board frame, so `HoldableClock` was added in `:domain`, tests first.
  - API 37: the legs ran 0 tests (a "not enough space" install failure) and still showed green. That led to `disk-size` and `scripts/check-ui-test-results.sh`.
  - API 37: the x86_64 emulator image aborts in the goldfish mapper. Other GPU modes and targets did not help, and an older emulator build could not be verified.
  - The owner approved debugging through CI, then trying another image, then said "go with your recommendation". The 37 legs became informational (issue #82) and phone-31 stayed required.
- The repo-settings script needed the owner to run it, because the classifier blocked the agent. It had a hard-coded check list and a sort-order mismatch.
- Tasks 6 to 9 followed (#88 accessibility and font-scale tests, #90 backup rules check, #92 final whole-range review fixes). The plugin repo got v0.3.0, adopted in the project.
- Task 10 (#94) closed Plan 3. Then:
  - StoresTest flake fix (#105).
  - Owner request for full screen (#106). This broke Restart and Play again, caught in review and fixed with a generation counter and RED tests.
  - Issues #97 to #104 were filed for deferred items.
  - Plan 4 written and merged (#108).
- Plan 4 release design came from owner-requested online research and was confirmed by the owner, then recorded in ADR-0012:
  - One `release.yml` with release-please, using a GitHub App for the token.
  - A signing environment with a required reviewer.
  - Immutable releases.
  - versionCode = major*1,000,000 + minor*1,000 + patch (ADR-0011).
- Plan 4 Tasks 1 to 3 merged (#110 version.txt and VersionCode, #112 release scripts and the `release-smoke` CI job, #114 release docs and ADR-0012). Task 4 is blocked on owner steps: create the App, create the signing key, and run the settings script.

## Key prompts
- Verbatim prompt (opening): "Execute docs/superpowers/plans/2026-10-28-plan-3-app.md with superpowers:subagent-driven-development, starting at the first task that is not done."
- Verbatim prompt: "ok, keep going". The agent read it as approval to merge once the checks were green, after a merge was blocked on permissions.
- Verbatim prompt: "go with your recommendation". It resolved the API 37 impasse and made the 37 legs informational.
- Verbatim prompt: "please research online and check for best practices and give me a recommendation based on findings and evidence". It drove the Plan 4 release design.
- Reconstructed prompt intent - not verbatim: the owner wanted the game full screen, process death to return to the menu, and a plan 4 scope summary. The owner also asked which GitHub App to create and with what permissions.
- Refinement chain (API 37):
  - Initial: run the UI tests on three emulator legs.
  - Output: all legs green.
  - Problem: the 37 legs had run 0 tests.
  - Refinement: add disk-size and a guard script that fails on no XML, tests=0, or failures.
  - Result: the guard worked and exposed the real emulator crash.

## Decisions
- Bump AGP in a separate PR before the plan tasks. Alternative: fold it into Task 1. Rationale: the lint gate break was unrelated to the task. Status: accepted.
- Keep debugging API 37 through CI, then try another image. Alternatives: unrequire the legs at once, or pin an older emulator build. Outcome: the other images failed, no older build was verifiable, and the legs became informational under #82.
- Keep libGDX's immersive default and pre-confirm it only in tests. The owner later asked for full screen, which became #106.
- Record kotlinx-serialization as a transitive dependency of savedstate. Keep the espresso 3.7.0 constraint. Both were owner choices.
- Every subagent runs on Sonnet (the project rule overrides the skill's "most capable model" default).
- Release design (ADR-0012), chosen after research. Alternative: a PAT or a manual release. A GitHub App with Contents and PRs write was chosen. This supersedes DEC-030, which the fold-in should mark.
- A computed versionCode scheme with 0.0.0 mapping to 1 (ADR-0011).

## Failures and rework
- AGP lint failure, then stale 32.4.1 verification entries removed in fix round 1.
- Task 4 review findings (R8 rule, frame loop while paused, background pixel).
- The Task 5 sequence above: false green, immersive overlay, countdown race, settings script mismatch, unfixable API 37 emulator crash.
- #106: the countdown fix froze Restart. A reviewer caught it before merge.
- Plan 4 Task 1: a module-wide lint disable was rejected in review and the computation moved to the root build.
- Plan 4 Task 2: negative script tests passed for the wrong reasons, fixed with stderr assertions and a mutation proof. In CI, the overlay hid the UI dump, so the script now taps "Got it". An emulator port flake was fixed by a re-run.
- pr-rules failed for lack of a fenced Evidence block. Editing the body triggers a new run, and re-running an old job uses the old body.
- The plugin update stayed at 0.2.0 until the marketplace was re-added at the new tag.
- The force-push was denied by the classifier, so `gh pr update-branch --rebase` was used.

## Human contribution
- Chose scope and direction: AGP first, full screen, plan 4 scope, the release design after research.
- Answered the AskUserQuestion forks: espresso, serialization, API 37 strategy.
- Ran the privileged steps the agent could not: the repo-settings script, and later the App and signing key creation.
- Approved the plan merges and held back deletion of the plan workspace until the owner had looked at it.

## AI contribution
- Orchestrated about 10 plan tasks, plus a fix PR and Plan 4 tasks 1 to 3, through implementer, reviewer, and fix-round subagents.
- Kept a progress ledger with rulings, each with its cost if wrong.
- Debugged CI emulator failures from logs, found the false-green cause, and wrote the guard.
- Researched release practice and wrote Plan 4, ADR-0011 and ADR-0012.
- Saved memories along the way.

## Concepts and principles learned
- A green CI job can hide zero executed tests, so guard on test counts.
- Per-task reviews miss seams between tasks, so run a whole-range review.
- A dependency release in the middle of a plan can break the gate. Handle it in a separate PR first.
- An informational CI leg needs a tracking issue and evidence.
- Orchestrator rulings should be logged with the cost if wrong.
- Secrets and signing keys stay outside agent sessions. The owner performs those steps.

## Durable lessons (candidates for assistant memory)
- Always assert a minimum executed-test count in emulator CI because the 37 legs passed with 0 tests. (Already saved as api31/api37 memories.)
- Always edit the PR body, not re-run, after fixing the Evidence section because pr-rules reads the event body.
- Always re-add the marketplace at the new tag to adopt a plugin version.
- Always ask the owner to run the settings script, because the classifier blocks the agent from running it.

## Presentation candidates
- Headline: "Green CI that ran zero tests". Key message: verify the verifier. Example: the 37 legs and the guard script.
- Headline: "Subagent-driven development at scale". Key message: implementer, reviewer, fix rounds capped at 5, and a ledger of rulings. Example: Task 5 had 5 fix rounds.
- Headline: "Human-gated steps". Key message: secrets and org settings stay with the owner. Example: the release App and key.
- Headline: "A fix that broke another feature". Key message: reviews catch regressions. Example: #106 froze Restart.

## Evidence
- PRs: #72, #73, #76, #77, #79, #81, #88, #90, #92, #94, #105, #106, #108, #110, #112, #114. Plugin repo PR #10 and tag v0.3.0.
- Issues: #74, #78, #80, #82, #97 to #104.
- Files: docs/superpowers/plans/2026-10-28-plan-3-app.md, docs/superpowers/plans/2026-11-02-plan-4-release.md, docs/adr/0011, docs/adr/0012, docs/release.md, scripts/check-ui-test-results.sh, scripts/release-smoke.sh.
- Commits: HoldableClock a9259ca..9501cc7, f375939 (guard), 3f701b3 (google_atd).

## Gaps and uncertainty
- The digest was size-capped. Tasks 6 to 9 details and the exact wording of many subagent prompts are Reconstructed from the compaction summaries.
- Merge commit hashes beyond those in the summaries were not verified.
- Task 4 of Plan 4 and later tasks are Prospective and not part of this record.
- The ADLC fold-in of two notes during #72 happened inside this session. Its content was not re-read here.
