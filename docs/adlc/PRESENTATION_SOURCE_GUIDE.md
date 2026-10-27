# Presentation Source Guide

This file is the bridge between detailed lifecycle evidence and future educational material. It does not replace the evidence-based documentation.

Do not create presentation claims unsupported by the project documentation. No presentation has been created.

## Presentation Purpose

Show an audience what an AI-assisted software lifecycle looked like in practice for one real project: a human owner who set non-negotiable rules, AI agents that did research, review, implementation and documentation, and a set of machine gates that made the rules real. The project is also a teaching reference for how to start such a project, with its history before game code existed.

## Audience Assumptions

Developers and team leads who use or plan to use AI coding agents. They know Git and pull requests. They do not know Claude Code skills, subagents or mise. (Assumed; no audience was specified in the evidence.)

## Learning Outcomes

After the presentation, the audience should be able to understand:

- how the project moved from problem/requirements to validated outcomes
- how AI participated across lifecycle phases
- where human judgement was required
- how prompts functioned as engineering inputs
- how testing, reviews, and failures created feedback loops
- how the actual lifecycle differed from simplified SDLC/ADLC theory
- lessons applicable to future AI-assisted development

## Recommended Narrative

1. Project context and intended outcome
2. Why the development approach matters
3. Initial requirements and assumptions
4. Planning and design
5. Prompting as an engineering activity
6. Implementation progression
7. Testing and validation
8. Failures, rework, and feedback loops
9. Human-AI responsibility split
10. Actual lifecycle compared with theory
11. Lessons learned
12. What to improve next time

## Phase / Cycle Presentation Notes

### CYCLE-001 and CYCLE-002 - Brainstorm, design, external review (2026-10-20 to 22)

#### What happened?
One question at a time, the AI and owner settled purpose, look, rules source, stack, release and review policy. An independent agent then reviewed the spec and found a blocker.

#### Why did it matter?
Every later gate traces to a decision here. The CodeQL blocker would have failed CI later.

#### Inputs / Outputs
Input: PROMPT-001, three mockups. Output: approved spec `e900708`, ADRs 0001 to 0006 content.

#### Human contribution
Non-negotiables, the look, the stack choice, the demand for an external review.

#### AI contribution
Questions, source research, GitHub data, the independent review, verifying the blocker.

#### Validation
External review and owner approval (no running code yet).

#### Feedback loop / learning
KTX recommendation reversed by data; CodeQL cap found by review.

#### Presentation candidates
- **Potential slide headline:** "The reviewer found the bug before there was code."
- **3-5 key messages:** Evidence beats recommendation. Separate author and reviewer. Verify the finding yourself. Label each rule "source" or "our call".
- **Representative example:** KTX table in ADR-0002.
- **Representative prompt:** PROMPT-003.
- **Relevant artifact/evidence:** ADR-0002, ADR-0006.
- **Suggested visualization:** decision flow from "libGDX + KTX" to "libGDX alone".
- **Caveats / evidence limitations:** only the blocker is detailed; the other 16 findings are not captured (GAP-002).

### CYCLE-003 - Plan 1 and the session-independent handoff (2026-10-22 to 23)

#### What happened?
Plan 1 was written, found to depend on the conversation, rewritten with a "Start here" section, and tested by a cold-read subagent.

#### Presentation candidates
- **Potential slide headline:** "Docs are only proven when a stranger uses them."
- **Key messages:** progress lives in git, not in memory; test the handoff; plans in waves keep version facts fresh.
- **Representative prompt:** PROMPT-004.
- **Suggested visualization:** before/after of the handoff, with the cold-read test as a gate.
- **Caveats:** the most serious cold-read gap was not visible in the digest.

### CYCLE-004 - Executing Plan 1 (2026-10-23 to 26)

#### What happened?
A controller agent ran ten tasks through brief, implementer, reviewer, fix and re-review, all on Sonnet subagents, while the owner acted as gate. About twenty PRs merged in about four and a half hours on 2026-10-26, including two reversals (squash merge, Dependabot handling).

#### Why did it matter?
It built every machine gate the later game code will pass through, and the plugin that exports them.

#### Human contribution
Direction changes (mise, zizmor, squash, release-please), irreversible-action gates, trade-off calls (allowlist, Lint check, signing level), context management.

#### AI contribution
Briefs, implementation, TDD, bite proofs, review, CI debugging, version and SHA verification.

#### Validation
Each gate was shown to fail on bad input (Observed in session notes); CI with four required checks; hook tests.

#### Feedback loop / learning
Hardening PRs after review (PRs #12, #20); brew failure to mise; merge-method reversal propagated across settings, ruleset, spec, plan.

#### Presentation candidates
- **Potential slide headline:** "A foundation in a day: controller, subagents and a human who says go."
- **Key messages:** state in plan and git, not in the context window; a gate must be shown to bite; humans own irreversible actions; be honest about limits (JDK has a hash but no signature).
- **Representative example:** PROMPT-007 chain from failed `brew` to pinned mise.
- **Representative prompt:** PROMPT-007, PROMPT-012.
- **Relevant artifact:** `mise.lock`, `scripts/gradle-strict.sh`, ADR-0007.
- **Suggested visualization:** swim lane of owner, controller, implementer, reviewer per task; timeline of PRs on 10-26.
- **Caveats:** per-task review findings are partly unseen (GAP-003, GAP-007).

### CYCLE-005 and CYCLE-006 - Documenting the process itself (2026-10-27) (Reconstructed)

#### What happened?
A plugin feature was designed, spiked, built and hardened in one PR, then adopted by SuperSnake. This documentation is its first fold-in.

#### Presentation candidates
- **Potential slide headline:** "Automatic capture, three privacy layers, human-run fold-in."
- **Key messages:** spike the platform before designing on it; a filter can over-redact; a test that cannot fail is not a test; local and CI tool versions can differ.
- **Representative example:** the generic issue-key pattern that redacted ADR-0006 and SHA-256 (DEC-026).
- **Caveats:** no prompts were captured for this cycle (GAP-004).

## Visual Candidates

Selected, with what each must communicate and what must not be invented:

| Visual | Communicates | Source | Do not invent |
|---|---|---|---|
| PR timeline for 2026-10-26 | How compressed the foundation work was | SRC-002 | Task mapping beyond PR titles |
| Decision flow: KTX, rebase to squash, Dependabot reminder to split | Decisions changed on evidence | DECISION_LOG | Reasons for DEC-022 |
| Controller and subagent loop | Brief, implement, review, fix, re-review | S2 note | Findings per task |
| Three-layer privacy diagram | Model filter, mechanical redaction, scan | DEC-024 | Real note content |
| Actual vs theoretical lifecycle | Design, review, plan and handoff rework before code; code is Direction only | ADLC_PROCESS | Game progress |

## Presentation Readiness Check

Confirm that the documentation can answer:

- What was being built and why? Yes (ADLC_PROCESS §1).
- How did requirements evolve? Partly: REQ-004, REQ-006, REQ-008, REQ-013 have evolution; others do not.
- Which lifecycle phases actually occurred? Yes; implementation of the game itself has not.
- How were prompts used? Yes (PROMPT-001 to 014).
- How did implementation progress? Plan 1 only.
- How were outputs validated? Partly: gates and reviews are known; per-PR evidence was not re-read (GAP-007).
- What failed or caused rework? Yes (ADLC_PROCESS §9).
- Which decisions changed? DEC-007/008, DEC-021/022, DEC-026.
- How did outputs become inputs to later work? Yes, per cycle.
- What did the human contribute? Yes.
- What did AI contribute? Yes.
- What was learned? Yes.
- How did the real process differ from theory? Yes.
- Which examples and visuals best demonstrate the process? Listed above.

Open: gameplay implementation and testing (not started), and the gaps in the register.
