---
session: none (reconstructed at the final fold-in from git, gh, Plan 4 and Plan 5; no staged note)
date: 2026-11-13 to 2026-11-20
harness: claude (main session, executor session, Sonnet subagents); the digests of sessions 35559d8c and 154bc22a cover part of this
branch(es): ci/release-workflow, ci/remove-tag-proof, release-please--branches--main, fix/lost-score, fix/back-button, fix/audio-crash, ci/utp-cache, build/gradle-992, test/sturdy-tests, docs/close-plan-4, docs/plan-5, docs/adlc-final and others
evidence_status: Observed for PR and issue titles, dates, bodies and the Release run, read with git and gh on 2026-11-20. Reconstructed for how the work was run, taken from the two staged notes and Plan 5. Unknown where stated.
---

# Goal

Record the end of the project: Plan 4 Tasks 5 to 7 and release 1.0.0, the owner's playtest, the autonomous finish-up and release 1.0.1, together with what the two staged notes of the same period do not show. It does not repeat what those notes say.

# What happened (short chronology)

All dates are merge dates from `gh pr list --state merged`. Observed unless marked.

## 2026-11-13: Task 4 lands

- #130 (smoke re-taps Play, issue #126), #129 (Gradle 9.9.1) and #127 (release environments, tag ruleset, labels; Plan 4 Task 4) merged. Task 4 waited on owner-run settings and secrets before its PR could carry Evidence.

## 2026-11-17 to 2026-11-18: Task 5, the v1.0.0 release

- #134 (mise pin v2026.11.9) merged first as its own bump PR. #135 added release-please and the approval-gated `release.yml` (build, smoke, sign-publish, verify-published). #138 removed the tag proof workflow once the ruleset proof had run. #136, the release-please PR for 1.0.0, merged on 2026-11-17.
- The first `sign-publish` failed closed on a signer fingerprint mismatch. Root cause in the executor note: the variable held a wrong 65-character value. After the variable was set from the keytool pipe, a re-run of only the failed job and the owner's approval of the `release` environment published v1.0.0. The GitHub release list shows v1.0.0 at 2026-11-18 13:51 UTC (Observed).
- Follow-ups on 11-18: #141 (tagged release PRs exempt in `pr-rules`), #142 (apksigner native-access warning fixed at the cause), #145 (stop forcing release 1.0.0). The executor note says a duplicate release PR (#143) was closed unmerged; this was not re-read from `gh`.

## 2026-11-18 to 2026-11-19: playtest and decoupling

- The owner played the published 1.0.0 on the API 37 emulator and reported three bugs, issues #151 (a finished game's high score missing after a force stop), #152 (a crash on the High scores screen) and #153 (no way back from High scores). The issue bodies say none was reproduced at filing; #153's body notes that system Back worked on the emulator but the screen had no on-screen control.
- In parallel, #148 (plugin-bound files made project-neutral, values in `scripts/github/project-settings.json`) and #150 (the live admin pull-request bypass on `main` recorded as a project-owned setting) merged. Both came from the Task 7 plugin work (executor note).

## 2026-11-19 to 2026-11-20: autonomous finish-up

- The owner granted full autonomy on 2026-11-19 (orchestrator note). The main session ran the fixes as a pipeline of Sonnet subagents, one worktree, branch and pull request per issue. Plan 5 records the result as 15 merged fixes:
  - game: #163 (#151), #156 (#153), #167 (#152), #164 (#101), #165 (#102), #158 (#103), #159 (#100)
  - tooling: #155 (#104), #157 (#99), #160 (#98), #166 (#82), #162 (#161), #169 (#168), #172 (#171), #175 (#174)
- Merges used squash with auto-merge. Pull requests must be up to date with `main`, so a local helper loop updated branches that were BEHIND and left the merging to auto-merge (Plan 5, "Merge queue helper"). Reruns of failed emulator jobs were capped at two.
- One laptop power-down interrupted the run (orchestrator note). State was recovered from git, `gh` and `git worktree list`. The Plan 4 close-out worktree kept an unpushed commit that Plan 5 names (Observed in Plan 5). The number of restarts and exactly which local state was lost beyond that are Unknown.
- #162 held Kotlin at 2.4.20, the CodeQL cap (ADR-0006), with a Dependabot ignore rather than a parked PR. Plan 5 says Dependabot then closed #41.
- #175 bumped the Compose BOM and lifecycle to clear lint `GradleDependency` errors. Compose 1.13 requires compileSdk 37.1 or later, so the build compiles against 37.2; targetSdk stays 37.

## 2026-11-20: closing

- #179 bumped Gradle to 9.9.2 because 9.9.1 now failed `:app:lintDebug` (`AndroidGradlePluginVersion`) on every branch, the same shape as #129. It had to merge first.
- #176 made only the `checks` job write the Gradle cache, after warming the unified-test-platform dependencies (issue #170). #173 made the instrumented tests sturdier (issue #97): bounded waits instead of sleeps, a font scale set before launch, and a 120 s timeout on every UI test. Its PR body records two issue bullets it did not do and why.
- #180 added Plan 5 and pointed `CLAUDE.md` at it. #182 adopted plugin v0.4.1 (0 drift reported in its body) and closed Plan 4, moving Plan 4 Task 6 Step 7 to Plan 5 Task 8.
- Release 1.0.1: #146 merged, run 44709700143 succeeded in release-please, build, smoke, sign-publish and verify-published. The owner approved `sign-publish`; the release is immutable, the attestation was verified and the versionCode is 1000001 (reported by the main session; the run and release list were re-read here).
- Task 6 (this fold-in) is issue #183. Issue #181 stays open: emulator legs flake.

## CI flake investigations

| Issue | Symptom | Cause and fix | Status |
|---|---|---|---|
| #168 | 8 job failures in 6 runs, 11-17 to 11-19: the Kotlin plugin marker POM not resolved | Dependency verification rejected the portal fallback; #169 adds the plugin portal checksum as `also-trust` for the three marker POMs, verification stays strict | Closed |
| #170 | `junit-platform-engine` and launcher 1.13.3 not found in ui-tests | One basic-cache entry, saved by whichever job finished first, could lack the UTP dependencies (inferred, not proven); #176 lets only `checks` write the cache and warm them | Closed |
| none | Gradle 9.9.2 released; `:app:lintDebug` fails | Separate bump, #179 (issue #178) | Closed |
| #97 and #173 | FontScaleGameTest hung on API 31 when `font_scale` was changed mid-run | Set before launch plus a 120 s timeout; the earlier mid-run attempt stalled (issue #97 text) | Closed |
| #181 | Different tablet-37 tests fail from run to run; some jobs die on a Maven Central "could not find" | Not root-caused. Lead: `codeql.yml` has no Gradle cache. A stale 129 MiB cache entry was deleted on 2026-11-20 | Open |

# Key prompts

- Reconstructed prompt intent - not verbatim: the owner asked the agent to play the game on the emulator after v1.0.0 (executor note). Output: the defects of #151 to #153. Problem: none reproduced by the agent. Refinement: deterministic JVM tests for #151, Monkey and code review for #152, a visible Back button for #153. Result: #163, #167, #156.
- The finish-up prompts are verbatim in the orchestrator record.

# Decisions

Recorded as DEC-063 onward in the decision log. Main alternatives:

- Quit games: record only games that end (the existing rule) versus record any game left with a non-zero score. The second was chosen; backgrounding is not recorded because the app only pauses (#163).
- `live` on game over: clear at LOST/WON versus clear on leave. The first push cleared it at LOST/WON, which made recreate on the game-over screen restore the menu and failed `gameOverIsNotAnnouncedAgainAfterRecreate`; it was changed to clear on leave (#164).
- Kotlin cap: Dependabot ignore versus a parked PR (#162).
- Gradle cache: per-job key (needs a new action) versus one writing job (#176).
- #152: ship fixes for what could be found (an AudioTrack failure Monkey reproduced, a DataStore IOException path) and say plainly it may not be the owner's crash (#167).

# Failures and rework

- #164: first push failed an announcement test, redone as above.
- The Gradle 9.9.2 release broke lint on every branch, blocking all pushes until #179.
- Emulator and dependency flakes (table above); rerun, cap, then investigate.
- A 1.0.0 fingerprint variable error and the release PR label exemption are in the executor record.
- Local state lost to a laptop restart: Unknown extent.

# Human contribution

- Played 1.0.0 and filed what the agents could not find (#151 to #153).
- Granted autonomy on 2026-11-19 and kept the owner-only steps: approving the `release` environment, the settings script, and the play check.
- The one-line "decoupled from the project" direction behind #148 (executor note).

# AI contribution

- Ran and merged a parallel Sonnet pipeline with one PR per issue, each stating its decision so the owner could veto it.
- Investigated flakes from run artifacts, wrote Plan 5, closed Plan 4, ran release 1.0.1 and verified it.

# Concepts and principles learned

- With autonomy, put each owner-level decision in the PR body so it can be vetoed afterwards.
- A bug the agent cannot reproduce can still be improved by the crash paths a monkey run and a code review find. Say the reproduction is missing.
- A shared cache needs one deliberate writer.
- A new stable release can break the lint gate on every branch; the bump comes first, alone.

# Durable lessons (candidates for assistant memory)

- Always state decisions taken on the owner's behalf in the PR body, because the owner reviews them after the merge.
- Never claim an unreproduced bug fixed; state what was fixed and what was not proven (#167).
- Always cap rerun attempts and then investigate, because reruns hide flakes.

# Presentation candidates

- Headline: "Fifteen fixes in a day: the pipeline." Example: Plan 5's table of 15 merged fixes.
- Headline: "The bug we never reproduced." Example: #152 and #167.
- Headline: "Whose cache is it?" Example: #170 and #176.

# Evidence

PRs #127 to #182 and release run 44709700143; issues #151 to #153, #168, #170, #181; `docs/superpowers/plans/2026-11-02-plan-4-release.md`, `docs/superpowers/plans/2026-11-20-plan-5-finish.md`.

# Gaps and uncertainty

- The executor note calls #136 "another session's work investigated for a phone-37 failure". `gh` shows #136 as the release-please PR for 1.0.0. The note is probably mistaken; git is taken as correct.
- The settings script, the 1.0.1 play check and the approval of the sign-publish job are recorded from the main session's report only (Reconstructed).
- #152's crash is Unknown. The extent of lost local state is Unknown.
