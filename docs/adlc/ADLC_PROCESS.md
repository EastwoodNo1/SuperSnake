# Project Development Lifecycle

> Human-readable entry point for the project's actual SDLC and AI-assisted/agentic development lifecycle.

Evidence labels: **Observed**, **Reconstructed**, **Unknown**, **Prospective**. Documentation IDs (`REQ-`, `DEC-`, `PROMPT-`, `CYCLE-`, `GAP-`) were allocated at the first fold-in on 2026-10-27 and did not exist earlier.

## 1. Project Overview

### Problem / Opportunity
Observed (spec §1): a fun classic Snake game for Android phones and tablets, built so it also serves as a reference project, a teaching ground for AI-assisted SDLC, and a "skill factory" whose CI, git hooks and SDLC rules are exported as a reusable plugin.

### Intended Outcome
Observed (spec §1): a signed APK of a playable, accessible neon-arcade Snake on GitHub Releases, with every process rule enforced by a machine, and a plugin another project can install to get the same setup on day one. Play Store publishing comes later. Non-goals: iOS, online features, ads, analytics, multiplayer.

### Development / Learning Objective
The owner runs the project solo, with AI agents in every phase. The aim is to learn and show what a disciplined AI-assisted lifecycle looks like: evidence for every claim, gates that cannot be skipped, plans that survive a change of session, and documentation of the process itself.

## 2. Documentation Status

- Historical reconstruction: **Complete for the accessible evidence (first fold-in, 2026-10-27)**; open gaps are listed in the register
- Earliest reliably documented point: 2026-10-20 (first captured session); first commit 2026-10-22 (`e900708`)
- Current lifecycle position: Plan 1 (foundation) complete on 2026-10-26; plugin `adlc-docs` released and adopted on 2026-10-27; Plan 2 (game engine) complete on 2026-10-28; Plan 3 (app) written and approved on 2026-10-28; AGP 9.5.0 bump (issue #71) in progress before Task 1
- Major known gaps: day-one conversation, the ADLC feature session's prompts (not captured), per-PR evidence not re-read; see [register](SOURCE_AND_UNCERTAINTY_REGISTER.md)
- Last documentation checkpoint: 2026-10-28 (third fold-in, in the AGP 9.5.0 PR)
- Last folded session: 35559d8c-dea6-4515-a650-94781a5140a4 (2026-10-28, Plan 3 planning) and ea322ed1-6e1c-4b6f-a10c-b553a417707d (2026-10-27, ADLC feature build), in the third fold-in. Before that: dfda0bef-0051-4b38-b52e-29d3857384f8 (2026-10-27 to 2026-10-28), plus one reconstructed record for the Plan 3 planning session (2026-10-28). Earlier: 68b8cc9f-ea4d-4cec-b57b-b8efd66c289f (2026-10-23 to 2026-10-26). Folded in the same pass: 37cc1e57, b283293d, 9666aa57; plus one reconstructed record for 2026-10-27

## 3. Actual Lifecycle Overview

| Phase | Purpose | Inputs | Key Activities | Outputs | Validation | Iterations | Status |
|---|---|---|---|---|---|---|---|
| Requirements and design (CYCLE-001) | Decide what and why | Owner's prompt with non-negotiables | Guided brainstorm, mockups, rules research, stack comparison | Design spec | Owner approval per question | Stack changed once on evidence (KTX) | Done |
| Design review (CYCLE-002) | Find flaws early | Draft spec | Independent agent review; AI verified the blocker | Revised spec, ADRs | Owner approved all fixes | One blocker, 7 major, 9 minor | Done |
| Planning (CYCLE-003) | Executable plan | Approved spec | Plan 1; handoff rewrite; cold-read test | Plan 1 with "Start here" | Cold-read subagent | Plan rewritten once, then fixed | Done |
| Implementation of the foundation (CYCLE-004) | Build every gate before game code | Plan 1 | Controller plus Sonnet subagents; task by task | Gradle build, checks, hooks, CI, rulesets, plugin v0.1.x | Bite proofs, reviews, CI, required checks | Two hardening PRs, merge-method reversal, toolchain pivot, Kotlin handling | Done (Plan 1 complete) |
| Tooling for documentation (CYCLE-005) | Reusable ADLC capture | Owner's need | Spike, design, build, hardening in the plugin repo | Plugin 0.2.0 | 88 tests, CI, real-CLI smoke test (Reconstructed) | Many fix commits | Done (Reconstructed) |
| Adoption and first fold-in (CYCLE-006) | Apply it to SuperSnake | Plugin 0.2.0, four staged notes | Adoption commit, reconstruction | `docs/adlc/` | Privacy scan by the main agent | n/a | In progress (Prospective validation) |
| Game engine (CYCLE-007) | Engine in `:domain` and `:data` | Plan 2, design doc | Owner decisions, 8 subagent tasks, whole-range review | PRs #47 to #67, rules C13-C25, ADR-0008 | Gate, coverage 100%, per-task and final review | Plan rewritten after owner redirect; 4 seam defects fixed | Done |
| App plan (CYCLE-008) | Plan the Android app | Plan 2 handoff contract | Parallel research, three owner questions | Plan 3, ADR-0009, ADR-0010 | Owner approval | One recommendation rejected; one colour changed | Planned; execution Prospective |
| App, release (Plans 3 and 4) | The playable game and release | Plan 3 | Not started | Not started | Not started | n/a | Prospective |

## 4. Actual Lifecycle Pattern

Observed: documents first, gates second, product last. About five days of brainstorming, review and planning, then roughly four and a half hours on 2026-10-26 where twenty PRs built the whole enforcement layer, and so far one domain type (`Direction`) of the game. A simplified SDLC puts implementation before tooling. Here the tooling is the first deliverable, because the rules (no comments, 100% coverage, evidence, no direct commits to `main`) have to exist before game code does.

The pattern inside a task is prompt-driven and review-heavy: a controller writes a brief, a subagent implements, a reviewer checks, fixes follow, and a scoped re-review closes it. The plan and git, not the conversation, carry progress (CLAUDE.md "Progress lives in git"). Decisions are corrected when evidence arrives, not held to the original plan (KTX, rebase to squash, Kotlin cap), which is more iterative than the theoretical waterfall the plan's four-wave structure suggests.

## 5. Major Development Cycles

### CYCLE-001 - Brainstorm and design (2026-10-20 to 22)

**Evidence status:** Observed (session note), with Unknown early steps  
**Lifecycle phase(s):** Requirements, design

#### Trigger and Context
Empty Android Studio starter; the owner wanted a Snake game and a teaching project.

#### Purpose and Objective
Settle purpose, rules source, stack, release path and workflow into a spec an agent can plan from.

#### Inputs
PROMPT-001, three neon mockups, searches for the original rules.

#### Significant Prompt / Instruction
PROMPT-001 (non-negotiables) and PROMPT-002 (stack refinement).

#### Human Contribution
Constraints, the look, stack and release choices, repo visibility, review policy, the plugin structure.

#### AI Contribution
One-question-at-a-time facilitation with stated guesses, source research, GitHub community data, version verification by a background agent, a builder-view agent, the draft spec.

#### Observable Rationale and Decisions
DEC-001 to DEC-005, DEC-009 to DEC-012. KTX failed the community rule (ADR-0002).

#### Actions
Choose the look from three mockups; find a rules source (none from Nokia); compare stacks; write `docs/superpowers/specs/2026-10-22-supersnake-design.md` with every decision tagged owner, call or builder.

#### Outputs / Artifacts
Spec, mockup `docs/design/neon-arcade-mockup.html`.

#### Validation
Owner answers at each step, then CYCLE-002.

#### Outcome
Accepted with changes.

#### Failure / Rework
The AI's first stack pick (Compose Canvas) was replaced; the owner's KTX choice was then reversed on data.

#### Learning
Verify community health with data; label rules by source; separate machine-enforced rules from taught practices.

#### Next Lifecycle Transition
The draft spec became the input to an external review.

#### Evidence
[Session record](sessions/2026-10-20-brainstorm-design-and-plan-1.md); `e900708`.

#### Educational Relevance
Requirements as constraints; recommendation tested against evidence.

### CYCLE-002 - External review (2026-10-22)

**Evidence status:** Observed (session note; counts only)  
**Lifecycle phase(s):** Review, design

#### Trigger and Context
PROMPT-003: review the spec as an outsider before planning.

#### Actions and Outputs
An independent agent found 1 blocker, 7 major and 9 minor issues. The AI checked the blocker at the source: CodeQL supported Kotlin only up to 2.4.20 while 2.4.21 existed. The owner approved all fixes.

#### Decisions
DEC-006 (ADR-0006), plus the rest of the fixed list (not captured).

#### Validation and Outcome
Accepted with changes; approved spec at `e900708`.

#### Learning
Author and reviewer should differ, and the reviewer's top finding should be re-checked.

#### Next Lifecycle Transition
The revised spec was the input to Plan 1.

#### Evidence and Educational Relevance
Session record; the failing-build-avoided story fits the presentation guide.

### CYCLE-003 - Plan 1 and the session-independent handoff (2026-10-22 to 23)

**Evidence status:** Observed  
**Lifecycle phase(s):** Planning, validation

#### Trigger and Context
Spec approved. A plan was needed that does not rely on the session.

#### Significant Prompt / Instruction
PROMPT-004, PROMPT-005.

#### Observable Rationale and Decisions
DEC-012, DEC-013.

#### Actions and Outputs
Plan 1 (10 tasks, `7ca0478`); README, CLAUDE.md, "Start here" and handoff memory (`c51cd73`, `730e1e7`); prerequisite installs recorded one commit each (`380cda8`, `b127e67`, `ad75d14`, `c41eca1`, `179a06f`); the decision to review locally (`b32fb27`).

#### Validation
A cold-read subagent. It found gaps (README and CLAUDE.md missing, undefined resume rules, stale commit list, author-line contradiction, local settings not ignored), then fixes.

#### Failure / Rework
`git init` overwrote the starter `.gitignore`; the AI restored it and said it could not prove an exact match. The plan was not session-independent at first.

#### Learning
Always check an existing file before overwriting. Make plans self-contained.

#### Next Lifecycle Transition
Plan 1 started in a fresh session (PROMPT-006).

#### Evidence and Educational Relevance
Commits listed above; the cold-read test is a candidate slide.

### CYCLE-004 - Executing Plan 1 (2026-10-23 to 26)

**Evidence status:** Observed for PRs and commits; Reconstructed for task mapping  
**Lifecycle phase(s):** Implementation, review, validation, release engineering

#### Trigger and Context
PROMPT-006: execute the plan from the first undone task, confirming TDD.

#### Purpose and Objective
A public repo where every pre-game rule of spec §7.2 is machine-enforced and proven, plus the plugin.

#### Inputs
Plan 1, spec, ADRs, the owner's constraints.

#### Human Contribution
Toolchain direction (mise), supply-chain level, zizmor, squash merge, release-please, plugin repo creation; "go" gates; token scope; trade-off calls; context management (PROMPT-013).

#### AI Contribution
A controller wrote briefs and rulings and judged concerns. Sonnet subagents implemented and reviewed each task. Versions and SHAs were verified at the source on the day.

#### Observable Rationale and Decisions
DEC-008, DEC-014 to DEC-022, DEC-028 to DEC-030.

#### Actions
Skeleton, JDK and gh via mise, strict-Gradle allowlist, repo baseline (PR #2), Kover/ktlint/Lint gates on `Direction` (PR #4), squash switch (PR #8), no-comments checker (PR #6), commit/PR/tests-changed checkers (PR #10), workflow and manifest checkers (PR #14), hooks (PR #18), CI, CodeQL, Dependabot and verification (PR #22), required checks (PR #25), onboarding and ADRs 0001 to 0007 (PR #30), release-please docs (PR #32), plugin v0.1.0 to v0.1.1 (plugin PRs #2, #4, #6; PRs #34, #36, #43), closing docs and rules fixes (PRs #39, #40).

#### Outputs / Artifacts
`build-logic/checks`, `.githooks`, `scripts/bootstrap.sh`, `scripts/gradle-strict.sh`, workflows, rulesets, `docs/versions.md`, `mise.lock`, ADRs, the plugin.

#### Validation
Gates shown to bite; implementer-reviewer loops; four required checks on `main`; hook tests.

#### Outcome
Plan 1 complete 2026-10-26 (PR #39); follow-ups after that.

#### Failure / Rework
See section 9.

#### Learning
State in plan and git survives compaction; gates must be shown to fail; irreversible actions need the owner.

#### Next Lifecycle Transition
Plan 2 (engine) is the next planned cycle, after versions and CodeQL's Kotlin support are re-checked. Separately, the plugin work led to CYCLE-005.

#### Evidence
[Session record](sessions/2026-10-23-plan-1-execution.md); PR list; `git log`.

#### Educational Relevance
Controller and subagent pattern; honest limits of verification; the human as gate.

### CYCLE-005 - Building the ADLC documentation feature (2026-10-27)

**Evidence status:** Reconstructed (no session captured; from plugin PR #8 and its commits)  
**Lifecycle phase(s):** Requirements, design, implementation, validation (plugin repo)

#### Trigger and Context
The owner wanted automatic, safe lifecycle documentation for every plugin project, first SuperSnake, as presentation source.

#### Significant Prompt / Instruction
PROMPT-014 (Reconstructed intent).

#### Observable Rationale and Decisions
DEC-023 to DEC-027. The design made two platform facts a precondition and a spike checked them.

#### Actions
Spec and plan, spike, agent and templates, digest, session hooks, capture, backfill, hook registration, redaction and permission hardening, issue-key narrowing, CI shellcheck fix. 16 commits in one PR, merged 15:17 UTC.

#### Validation
88 bash tests, shellcheck, `claude plugin validate`, template drift 0, a real-CLI smoke test, spec and quality review per task, a whole-branch review and a fix wave (all quoted from the PR body, not reproduced).

#### Failure / Rework
Review and the real CLI found issues in nearly every stage: a missing workflow section, a "move" that contradicted "never delete", assertions that could not fail, an `Write` rule the CLI ignores, writes refused inside `.git`, unredacted notes reachable on failure paths, a backfill queue swallowed by stdin, a generic issue-key pattern that over-redacted, shellcheck version drift on CI.

#### Learning
Spike first; layered privacy; make failing tests possible; align local and CI tool versions.

#### Next Lifecycle Transition
The plugin was adopted in SuperSnake (CYCLE-006).

#### Evidence
[Reconstructed record](sessions/2026-10-27-adlc-feature-build-reconstructed.md); plugin PR #8.

#### Educational Relevance
The process that documents the process was itself built with the same loop.

### CYCLE-006 - Adoption and first fold-in (2026-10-27)

**Evidence status:** Observed for the adoption commit and issue; Prospective for validation  
**Lifecycle phase(s):** Documentation, process

#### Trigger and Context
Issue #44; adoption commit `ae6c44d` (templates, CLAUDE.md section, plugin pin at v0.2.0).

#### Actions
First fold-in: four staged notes copied to `sessions/`, one reconstructed record added, all logs populated from git, PR lists, spec, plan, ADRs and the plugin PR.

#### Validation
Pending: the main agent runs the privacy scan and fixes every hit before the fold-in commit.

#### Next Lifecycle Transition
Definition of Done: commit, save durable lessons as memory, delete folded staged notes after the commit, then Plan 2.

#### Evidence
This folder; issue #44.

### CYCLE-007 - Plan 2: the game engine (2026-10-27 to 28)

**Evidence status:** Observed for PRs and owner decisions; Reconstructed for Tasks 5 to 7 (truncated digest)  
**Lifecycle phase(s):** Planning, design, implementation, review

#### Trigger and Context
Plan 2 was written from Plan 1's Start here (PROMPT-015). The owner redirected before any code (PROMPT-016).

#### Actions
Research by two Sonnet researchers (the thin first report was sent back); the agent verified the storage claim from published metadata. Owner rulings (PROMPT-017). Design doc, plan rewrite, PR #47. Eight tasks through subagents (implementer, reviewer, controller re-verification, merge): PRs #49 to #63. Whole-range review: no Critical and four Important seam defects, fixed in PR #65. Task 9 (PR #67) added the rule-to-test table and the Plan 3 contract.

#### Decisions
DEC-031 to DEC-036.

#### Validation
Strict gate re-run by the controller, coverage `:domain` 154 branches and 315 lines and `:data` 18 branches and 51 lines (O, from the note), reviews, four required checks.

#### Failure / Rework
Thin research; `local.properties` missing in a fresh worktree (strict gate failed with "SDK location not found"); a ticked-checkbox claim that was false (Task 4); tests written after code in Tasks 6 and 8; merge-wait retry; contradictory rule C20; four seam defects found only by the final review.

#### Learning
Verify a researcher's key claim against primary artifacts. A whole-range review finds seam defects that per-task reviews cannot. A hard-stop proof turns a constraint into a gate. Copy gitignored files into new worktrees.

#### Next Lifecycle Transition
The "Plan 3 contract" in the handoff is the input to Plan 3.

#### Evidence and Educational Relevance
[record](sessions/2026-10-27-plan-2-game-engine.md); see the presentation guide.

### CYCLE-008 - Plan 3: the app plan (2026-10-28) (Reconstructed)

**Lifecycle phase(s):** Planning, design

#### Trigger and Context
Plan 2 closed; Plan 3 written from the plan's Start here (PROMPT-018).

#### Actions
Three parallel Sonnet research subagents (libGDX, AndroidX and CodeQL's Kotlin range, emulator runner and 16 KB). Three owner questions, one at a time. Wall colour changed after a contrast measurement.

#### Decisions
DEC-037 to DEC-040. The planner's recommendation on the emulator action was rejected by the owner.

#### Validation
Owner approval. Plan execution not started.

#### Next Lifecycle Transition
Execution in a fresh session from Plan 3's Start here.

#### Evidence and Educational Relevance
[record](sessions/2026-10-28-plan-3-planning-reconstructed.md); commit `e11d295`.

## 6. Requirements Evolution

Requirements were stated up front as constraints (clean architecture, evidence, no comments, minimal dependencies, enforced SDLC). They were refined by evidence rather than changed in spirit: "latest stable" became "latest stable that CodeQL supports" (REQ-004); rebase-only became squash-only (REQ-008); a documentation requirement (REQ-013) appeared after Plan 1, derived from the original education purpose. The game requirements (REQ-001, REQ-009) are untouched since the spec. See [Requirements Traceability](REQUIREMENTS_TRACEABILITY.md).

## 7. Key Decisions and Trade-offs

- libGDX alone, no KTX, on community data (DEC-001, DEC-002).
- Kotlin capped by CodeQL, and Lint's newer-version check disabled (DEC-006, DEC-017).
- Squash merge replaced rebase-only (DEC-007, DEC-008).
- Everything through mise, JDK SHA-pinned without signature check (DEC-014).
- Strict Gradle gate with one allow-listed deprecation (DEC-016).
- release-please with an App token, deferred to Plan 4 (DEC-019, DEC-030 open).
- Capture is automatic and private by layers (DEC-023 to DEC-027).
- Storage without okio, Nokia-original game feel, own back stack, synthesized sound, emulator action kept against a recommendation (DEC-031 to DEC-040).

All in [Decision Log](DECISION_LOG.md).

## 8. Testing and Validation Strategy

- Observed: every rule is meant to be enforced by hook, CI check or GitHub setting, and each gate is proven to bite with real output. TDD for logic tasks; `koverVerify` 100% line and branch on `:domain` and `:data`; `test-hooks.sh`; CodeQL, Dependabot, dependency verification; four required checks on `main`.
- Observed: an independent review before planning, and a cold-read test of the handoff.
- Observed (plugin): bash tests with a stub CLI, shellcheck, real-CLI spike and smoke test.
- Not yet: UI tests, accessibility tests, release manifest and 16 KB checks on a real APK, gameplay validation.
- Limit: this fold-in did not re-read PR bodies, so "proof shown in each PR" is Reconstructed (GAP-007).

## 9. Feedback Loops and Rework

| Loop | Trigger | Returned to | Result |
|---|---|---|---|
| KTX | AI checked the owner's choice against the community rule | Design | libGDX alone |
| CodeQL cap | External review | Design | ADR-0006, Kotlin pinned |
| Plan not session-independent | Owner | Planning | "Start here", cold-read test |
| `.gitignore` overwritten | AI self-report | Planning | Restored, limit stated |
| AGP deprecation fails strict builds | First build | Implementation | Single-message allowlist |
| `brew` needs sudo | Install failure | Toolchain | mise, SHA-pinned |
| Rebase-only blocks owner's workflow | PR #6 | Design | Squash (ADR-0007), settings changed together |
| Task 5 review | Reviewer | Implementation | Hardening PR #12; later #20 |
| Token lacked `workflow` scope | Task 8 blocked | Owner | Scope granted |
| Dependabot Kotlin bump fails CI | Gate working | Process | Reminder, then split group (DEC-021, DEC-022) |
| Release dispatch blocked as destructive | Harness classifier | Owner gate | Explicit approval, re-run |
| Plugin: spike, real CLI, review, CI | Several | Plugin design | Fixes listed in CYCLE-005 |

## 10. Human-AI Collaboration

### Human Responsibilities
Set the rules and purpose; choose among options; approve designs; approve every irreversible or public action; grant permissions; manage context; catch stale AI claims (the model-selection session).

### AI Responsibilities
Facilitate; research with evidence; review independently; write specs, plans, code and tests; run the subagent loop; verify versions at source; admit errors; capture and document the process.

### Responsibility Shifts Over Time
Early (2026-10-20 to 23): human-led choices with AI facilitation and research. Execution (10-23 to 26): the AI controller coordinated Sonnet subagents; the human became a gate and a source of direction changes. On 10-27 the AI took over documenting the process, with the human running the fold-in step before PRs.

## 11. Lessons Learned

### Product / Domain
- No official Snake rules exist, so label every rule "from the source" or "our call".

### Engineering
- Prove a gate bites. Check toolchain constraints (CodeQL) before pinning. Pin by SHA and state where signing is not possible. A reversed decision must be propagated to settings, ruleset, spec and plan together. Capture exit codes without a pipe. Match local tool versions to CI.

### Prompting
- Request, evidence check, reversal (KTX). Constraints in the first prompt become gates later. Ask for the review and the cold-read test explicitly.

### Process
- Plans carry their own state; progress lives in git. Plan in waves so versions are fresh. Spike platform facts before designing on them.

### Human-AI Collaboration
- An owner question can reverse a plan; a recommendation can be overruled and should be recorded (DEC-038). Check transitive dependencies in the POM. Run a whole-range review after the per-task reviews. A fresh worktree lacks gitignored files.
- The AI can be confidently stale; verify with the live source. The human owns irreversible actions. A fresh agent is the best validator of documentation.

### Update: CYCLE-009 (third fold-in, 2026-10-28)

AGP 9.5.0 became stable during the Plan 3 execution session and lint's AndroidGradlePluginVersion check failed `:app:lintDebug`. The owner chose a separate bump PR before Task 1 (DEC-041; commits `5d55438`, `c3fe5e5`). Status: pending PR checks. The session has no note yet (GAP-015). Observed notes for the ADLC feature session and the Plan 3 planning session were folded (DEC-042, PROMPT-019, PROMPT-020).

## 12. Current State and Next Step

- Current lifecycle position: foundation and engine complete; Plan 3 (app) planned and approved, not executed
- Current validated state: Plan 1 gates merged on `main`; this documentation awaits the privacy scan
- Open issues: Dependabot Kotlin PR #41 (capped by CodeQL); issue #44 (ADLC adoption); spec design point on the `release` environment (DEC-030); `docs/release.md` not yet written
- Next recommended activity: merge the AGP 9.5.0 PR (issue #71), then Plan 3 Task 1 (#70)

## Supporting Documentation

- [Project Evolution Timeline](PROJECT_EVOLUTION_TIMELINE.md)
- [Prompt and Interaction Log](PROMPT_AND_INTERACTION_LOG.md)
- [Decision Log](DECISION_LOG.md)
- [Requirements Traceability](REQUIREMENTS_TRACEABILITY.md)
- [Source and Uncertainty Register](SOURCE_AND_UNCERTAINTY_REGISTER.md)
- [Presentation Source Guide](PRESENTATION_SOURCE_GUIDE.md)
- [Session records](sessions/README.md)
