# Prompt and Interaction Log

Record only prompts or instructions that materially influence requirements, planning, design, implementation, testing, debugging, review, validation, documentation, or project direction.

All prompts below come from the staged session notes (Observed unless stated). Wording is exactly as the owner typed it, including typos. Sessions: S1 = `9666aa57` (brainstorm to handoff), S2 = `68b8cc9f` (Plan 1 execution). The early pre-session prompts of the very first project day are Unknown.

## PROMPT-001 - Opening brainstorm and non-negotiables

- **Date / Session / Sequence:** 2026-10-20, S1, first prompt
- **Lifecycle phase:** Requirements
- **Related cycle:** CYCLE-001
- **Evidence status:** Observed (abridged by capture)
- **Trigger / Context:** Empty repo (an Android Studio starter).
- **Objective:** Start a guided brainstorm for a Snake game.
- **Constraints:** Clean architecture, security, static checks, current stable versions, minimal third-party dependencies, enforced SDLC, evidence, no code comments.

### Prompt / Instruction

> Verbatim prompt (abridged by capture)
>
> /superpowers:brainstorming i want to create a classic snake game for android based devices, like mobile and tables. We need our project to be clean architecture and simple to follow … never guess any verification, allways test with evidence for the project and the application. no extra comments in the code, why and what comments should always be in commit messages, never in code.

### Expected Output
A design reached through questions.

### Actual Output
One question at a time, with the AI stating its understanding and guesses after each answer. Three browser mockups for the look.

### Validation / Outcome
Owner approved the design after external review (see PROMPT-003).

### Problem or Limitation
None recorded.

### Related Records
- Requirements: REQ-001 to REQ-008
- Decisions: DEC-001, DEC-009, DEC-010, DEC-011
- Artifacts: `docs/superpowers/specs/2026-10-22-supersnake-design.md`, `docs/design/neon-arcade-mockup.html`

### Educational Relevance
Shows requirements delivered as constraints, not features. The evidence and no-comments rules in this prompt became machine-enforced gates five days later.

## PROMPT-002 - Stack choice and the KTX reversal

- **Date / Session / Sequence:** 2026-10-20 to 22, S1
- **Lifecycle phase:** Design
- **Related cycle:** CYCLE-001
- **Evidence status:** Observed
- **Objective:** Choose a more ambitious rendering stack than the AI's first recommendation.

### Prompt / Instruction

> Verbatim prompt
>
> go with option 1, libGDX + KTX so we see how we can modernize our snake game to another level without adding extra complexity

### Refinement chain
Initial: AI recommended native Kotlin with Compose Canvas. Output: a choice table. Problem: the owner wanted an engine-based option. Refinement: the prompt above. Revised output: the AI checked GitHub data and found KTX stalled (last commit 2025-06-28, rc release) and its community fork tiny (3 stars), which fails the owner's "big, active community" rule. Validation: GitHub API figures recorded in ADR-0002. Next step: libGDX alone, confirmed by the owner (DEC-001, DEC-002).

### Effect of Refinement
The AI's own evidence check overturned the owner's request. The human accepted it.

### Related Records
- Decisions: DEC-001, DEC-002; Requirements: REQ-005; Artifacts: ADR-0001, ADR-0002

### Educational Relevance
A dependency recommendation tested against data before adoption. Neither human nor AI decided from preference alone.

## PROMPT-003 - Ask for an external review

- **Date / Session / Sequence:** 2026-10-22, S1
- **Lifecycle phase:** Review
- **Related cycle:** CYCLE-002
- **Evidence status:** Observed

### Prompt / Instruction

> Verbatim prompt
>
> review all the spec and design that we have created, and try to see it as an external review now, then i will review spec and design before going ahead with creating the implementaiton plan

### Actual Output
An independent agent reported 1 blocker, 7 major and 9 minor issues. The AI verified the blocker (Kotlin 2.4.21 vs CodeQL's supported 2.4.20) against the official page before reporting it.

### Validation / Outcome
Owner approved all fixes; spec revised; ADR-0006 added. Accepted with changes.

### Related Records
- Decisions: DEC-006; Requirements: REQ-004; Artifacts: spec, ADR-0006

### Educational Relevance
Separating the author from the reviewer, plus self-verification of the headline finding. The worst design flaw would otherwise have surfaced as a failing build on first CI.

## PROMPT-004 - Plans must stand alone

- **Date / Session / Sequence:** 2026-10-22 to 23, S1
- **Lifecycle phase:** Planning
- **Related cycle:** CYCLE-003
- **Evidence status:** Observed

### Prompt / Instruction

> Verbatim prompt
>
> we need out plan in a way that we can start in a clean session so we don't need to get back to our old session and understand everything again, so the implementationplan should be decoupled with previous sessions

### Refinement chain
Initial: Plan 1 written. Output: a plan that relied on conversation context. Problem: a fresh agent could not resume. Refinement: this prompt, then PROMPT-005. Revised output: a "Start here" section with resume rules, README and CLAUDE.md, handoff memory. Validation: a cold-read subagent. Next step: gaps fixed (stale commit list, author-line contradiction, ignored local settings file), then execution.

### Related Records
- Decisions: DEC-013; Requirements: REQ-014; Artifacts: Plan 1 "Start here"

### Educational Relevance
A prompt that changes how every later prompt is written. The CLAUDE.md rule "Don't rely on memory of earlier sessions" comes from here.

## PROMPT-005 - Verify the handoff

- **Date / Session / Sequence:** 2026-10-23, S1
- **Lifecycle phase:** Validation
- **Related cycle:** CYCLE-003
- **Evidence status:** Observed

### Prompt / Instruction

> Verbatim prompt
>
> check the plan again that if we start a new session agent or the human will understand what to continue on what to start next in this project

### Actual Output
The AI ran a cold-read subagent that found gaps (README and CLAUDE.md missing, resume rules undefined). Full list is only partly visible (see SRC register).

### Validation / Outcome
Gaps fixed in commits. Accepted with changes.

### Educational Relevance
Documentation is proven only when someone with no context uses it.

## PROMPT-006 - Execute the plan, with a skills confirmation

- **Date / Session / Sequence:** 2026-10-23, S2, first prompt
- **Lifecycle phase:** Implementation
- **Related cycle:** CYCLE-004
- **Evidence status:** Observed

### Prompt / Instruction

> Verbatim prompt
>
> Execute the Plan 1 file starting at the first task that is not done and continue the plan, but before comencing the development, please confirm that you will use all superpower skills like tdd?

### Actual Output
Confirmation that TDD applies to logic tasks 3 to 7, while tasks 1, 2, 8, 9 and 10 are verified by build, readback and CI output.

### Educational Relevance
The AI scoped TDD honestly instead of claiming it everywhere. A fresh session needed only this prompt, as PROMPT-004 intended.

## PROMPT-007 - No system installs: use mise

- **Date / Session / Sequence:** 2026-10-23, S2
- **Lifecycle phase:** Implementation (toolchain)
- **Related cycle:** CYCLE-004
- **Evidence status:** Observed

### Prompt / Instruction

> Verbatim prompt
>
> instead of installing any libraries and runtoolin in my system, let use mise and use the latest version of mise as well, but i think we need to install mise on my machine first

### Refinement chain
Initial: owner asked for the latest Java, then Temurin 21, then 25. Output: `brew install --cask temurin@25`. Problem: it failed, sudo needs a TTY. Refinement: this prompt. Revised output: mise installed to `~/.local/bin`; JDK 25.0.5 SHA-pinned and gh pinned with attestations in `mise.lock`. Validation: the lock file, and a later CI run proving the linux-x64 lock (DEC-014 area). Next step: all Gradle runs go through `mise exec`.

### Related Records
- Decisions: DEC-014, DEC-015; Requirements: REQ-004, REQ-008

### Educational Relevance
A failed install changed the approach, not the workaround. It also produced a documented limit: the JDK is SHA-pinned but not signature-checked.

## PROMPT-008 - SHA-pinned and signed (paraphrase)

- **Date / Session / Sequence:** 2026-10-23, S2
- **Evidence status:** Reconstructed

### Prompt / Instruction

> Reconstructed prompt intent - not verbatim.
>
> Everything installed through mise should be SHA-pinned and signed.

### Actual Output
gh got SHA plus GitHub attestation. The JDK got SHA only, because mise has no OpenPGP tool. The owner accepted that after being told.

### Educational Relevance
A requirement that could not be fully met, stated honestly and accepted by the human.

## PROMPT-009 - Where does zizmor belong?

- **Date / Session / Sequence:** 2026-10-23, S2
- **Lifecycle phase:** Planning change
- **Evidence status:** Observed

### Prompt / Instruction

> Verbatim prompt
>
> if i want this to be included in the plan "use zizmor(latest stable version pinned) for static checks on github actions and workflows", in which task should this be included to?

### Actual Output
The AI located Tasks 7, 8 and 10 and the spec; the owner approved a separate docs commit (`26d66b6`).

### Educational Relevance
A mid-execution scope change handled as a plan edit, in its own commit, not as drive-by work.

## PROMPT-010 - Why does the Dependabot PR fail?

- **Date / Session / Sequence:** 2026-10-26, S2
- **Lifecycle phase:** Debugging
- **Evidence status:** Observed

### Prompt / Instruction

> Verbatim prompt
>
> ok keep going, but lets check why depandabot pr's failes on our check; https://github.com/EastwoodNo1/SuperSnake/pull/28

### Actual Output
Root cause: Kotlin was bumped past the version CodeQL supports (ADR-0006). The gate worked as designed.

### Validation / Outcome
Owner: "leave #28 open as the reminder" (DEC-021), later superseded (DEC-022).

### Educational Relevance
A failing check was a correct result of an earlier decision, not a defect.

## PROMPT-011 - Release automation with release-please

- **Date / Session / Sequence:** 2026-10-26, S2
- **Lifecycle phase:** Design
- **Evidence status:** Observed

### Prompt / Instruction

> Verbatim prompt
>
> leave #28 open as the reminder, and check if we already have in our release process described to use google's api's: release-please action for automating pr creation of our releases and even create github releases

Follow-up answer: "yes, adopt release-please with a GitHub App token".

### Actual Output
The AI found that the release process did not use it, explained why `GITHUB_TOKEN` would stop release PRs from triggering checks, and the owner chose the App token. Spec §8 and the third-party evidence table were updated (PR #32). Implementation deferred to Plan 4.

### Related Records
- Decisions: DEC-019; Requirements: REQ-011

## PROMPT-012 - Approval gates for outward actions

- **Date / Session / Sequence:** 2026-10-23 to 26, S2
- **Evidence status:** Observed

### Prompt / Instruction

> Verbatim prompts
>
> go
>
> yes, create the public repo EastwoodNo1/android-sdlc-skills
>
> yes, release plugin v0.1.1 with the tag ruleset

### Actual Output
Each action ran only after the answer. One dispatch (the first v0.1.1 release) had been blocked by the harness classifier as git-destructive and was re-run after the explicit approval.

### Educational Relevance
The human as the gate for irreversible, public actions.

## PROMPT-013 - Hand off or keep going?

- **Date / Session / Sequence:** 2026-10-26, S2 (asked twice)
- **Evidence status:** Observed

### Prompt / Instruction

> Verbatim prompt
>
> should we keep going here or handoff the session if the context is getting to big, but, without missingout work that is in progress?

### Actual Output
Decision to continue, with the git-ignored ledger and plan checkboxes carrying state.

### Educational Relevance
Context management as a project-management decision.

## PROMPT-014 - Portable automatic lifecycle documentation

- **Date / Session / Sequence:** 2026-10-27, not captured
- **Lifecycle phase:** Requirements, design
- **Related cycle:** CYCLE-005
- **Evidence status:** Reconstructed

### Prompt / Instruction

> Reconstructed prompt intent - not verbatim.
>
> Give every project that uses the plugin the same automatic, privacy-safe record of how it was built, starting with SuperSnake, to serve as source material for a learning presentation, with room for Codex and Copilot later.

### Actual Output
Plugin v0.2.0 (PR #8).

### Related Records
- Decisions: DEC-023 to DEC-027; Requirements: REQ-013

## Prompt Evolution Patterns

1. **Request, evidence check, reversal** (PROMPT-002): the AI tested the owner's chosen dependency against the owner's own rule and recommended dropping it. Result: KTX removed.
2. **Plan, handoff complaint, cold-read test, fix** (PROMPT-004, 005): the plan was good but session-bound. A fresh agent was the validator.
3. **Install fails, owner changes method, reproducible toolchain** (PROMPT-007, 008).
4. **Failing check explained as design working** (PROMPT-010): the answer was to keep the failure visible.

## PROMPT-015 - Write Plan 2

- **Date / Session / Sequence:** 2026-10-27, session dfda0bef, start
- **Lifecycle phase:** Planning
- **Related cycle:** CYCLE-007
- **Evidence status:** Observed

### Prompt / Instruction
Verbatim prompt:
> Write Plan 2 (game engine) following the active plan's Start here.

### Actual Output
A nine-task plan with DataStore Preferences and an okio checkpoint.

### Related Records
- Decisions: DEC-031 to DEC-036

## PROMPT-016 - Owner redirect: question okio, brainstorm the rules

- **Date / Session / Sequence:** 2026-10-27, session dfda0bef, after the first draft
- **Lifecycle phase:** Requirements, design
- **Related cycle:** CYCLE-007
- **Evidence status:** Reconstructed

### Prompt / Instruction
Reconstructed prompt intent - not verbatim: judge okio on community size and safety; if it fails, brainstorm alternatives. Do a complete brainstorm of game rules and edge cases, including cases solved in other Snake apps. Only after both decisions move on to subagents.

### Observed Problem and Refinement
Initial -> Output (plan on DataStore Preferences, which needs okio) -> Problem (an unexamined second runtime library, and rules not researched) -> Refinement (this prompt) -> Result: okio removed (DEC-031), rules C13-C25 added, plan rewritten, hard-stop proof in Task 8.

### Related Records
- Decisions: DEC-031 to DEC-036; Requirements: REQ-001, REQ-005

## PROMPT-017 - Short owner rulings and the go signal

- **Date / Session / Sequence:** 2026-10-27 to 28, session dfda0bef
- **Lifecycle phase:** Design, implementation
- **Related cycle:** CYCLE-007
- **Evidence status:** Observed

### Prompt / Instruction
Verbatim prompts, each a short ruling that defaults to the Nokia original: "hit the count as in the original nokia game", "countdown 3-2-1, then move", "keep speed fixed like the original nokia", "yes, go with option 1". The go signal: "ok, start task 1 once it's merged".

### Actual Output
Rules C13-C25; autonomous subagent execution of Tasks 1-8.

### Related Records
- Decisions: DEC-032 to DEC-034

## PROMPT-018 - Write Plan 3, with owner questions

- **Date / Session / Sequence:** 2026-10-28, planning session (not captured)
- **Lifecycle phase:** Planning
- **Related cycle:** CYCLE-008
- **Evidence status:** Reconstructed

### Prompt / Instruction
Reconstructed prompt intent - not verbatim: write Plan 3 (the app) from the plan's Start here, and settle dependency choices with the owner one question at a time.

### Observed Problem and Refinement
Initial -> Output (a draft with open dependency points) -> Problem (navigation libraries pull a banned runtime library; the emulator action looked quiet; sound source open) -> Refinement (three owner rulings) -> Result: ADR-0009, ADR-0010, the action kept with its evidence.

### Related Records
- Decisions: DEC-037 to DEC-040

Prompt patterns added: **Owner doubts a dependency, research follows, plan changes** (PROMPT-016, PROMPT-018); **short rulings that default to the Nokia original** (PROMPT-017).
