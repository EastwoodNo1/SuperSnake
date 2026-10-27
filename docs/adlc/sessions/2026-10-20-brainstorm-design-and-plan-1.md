---
session: 9666aa57-eabb-4cc6-b528-bb565fb837f3
date: 2026-10-20 to 2026-10-23
harness: claude
branches: main (local, nothing pushed; no GitHub repo yet)
evidence_status: Observed from session digest (conversation, tool calls, subagent briefs). Commit hashes are quoted from the assistant's own messages, not re-verified against git in capture (the fold-in reconstruction later confirmed them in git history). Final design and plan content was not re-read in this capture.
---

# Session note: brainstorm, design spec, external review, Plan 1 and session-independent handoff

## Goal
Brainstorm a classic Snake game for Android phones and tablets. The project doubles as a reference project and as an educational example of AI-assisted SDLC. Produce an approved design spec and a Plan 1 (foundation) that a fresh session can execute without this conversation.

## What happened (short chronology)
1. Owner opened with a brainstorming prompt. It set the requirements: clean architecture, security, static checks, current stable versions, minimal third-party dependencies, an enforced SDLC, evidence for every claim, and no comments in code.
2. The AI asked one question at a time. The owner chose the look, "neon arcade", from three browser mockups. The AI stated its understanding and its guesses after each answer.
3. Purpose settled: fun game, reference project, AI-SDLC teaching ground, and "skill factory" of reusable Claude skills.
4. Solo workflow settled: git worktrees, `gh stack` stacked PRs, 100% coverage on logic, UI tests for screens.
5. Rules source: the AI searched for the original Snake rules. Nokia published none. The owner accepted a 2015 history of Finnish video games (cited by Wikipedia) as the source. Each rule is marked "from the source" or "our call".
6. Stack: the AI recommended native Kotlin + Compose. The owner asked for a game-engine alternative and chose libGDX + KTX. The AI checked GitHub and found KTX stalled or small. The owner then chose libGDX alone.
7. Release: signed APK on GitHub Releases, Play Store later. Reusable skills (CI, git hooks, SDLC rules) go in a separate Claude Code plugin repo.
8. A background agent verified the latest stable versions with sources. A second agent read the draft as a builder and asked questions. The spec was written to `docs/superpowers/specs/2026-10-22-supersnake-design.md`.
9. Owner answers: repos public under a personal account, and AI review (Copilot or Claude) started manually, not on every PR.
10. Owner asked for an external review. An independent agent found 1 blocker, 7 major and 9 minor issues. The AI verified the blocker itself: CodeQL supports Kotlin only up to 2.4.20. The owner approved all fixes, and the spec was revised.
11. The AI initialised git and wrote Plan 1 (`docs/superpowers/plans/2026-10-22-plan-1-foundation.md`). It admitted it had overwritten the starter `.gitignore` without checking, and restored the standard one.
12. Owner asked for the plan to be decoupled from this session. The AI added a "Start here" section, wrote memory handoff files and committed.
13. Machine setup at the owner's request: Android platform 37, cmdline-tools 23.0, `gh-stack` 0.2.1, Copilot CLI, `gh` upgrade. Each was recorded in the plan's prerequisites table.
14. Owner questioned an install the AI never proposed ("Claude Code GitHub app"). The AI said it had not suggested it. The owner decided local Claude Code review is enough, and this was written into the spec and the plan.
15. A cold-read subagent tested the handoff. It found gaps, which were fixed: README and CLAUDE.md added, resume rules defined, stale commit list removed, commit author line aligned with the Sonnet subagents, and the local settings file gitignored.

## Key prompts
- Verbatim prompt (opening, abridged by capture): "/superpowers:brainstorming i want to create a classic snake game for android based devices, like mobile and tables. We need our project to be clean architecture and simple to follow … never guess any verification, allways test with evidence for the project and the application. no extra comments in the code, why and what comments should always be in commit messages, never in code."
- Verbatim prompt: "review all the spec and design that we have created, and try to see it as an external review now, then i will review spec and design before going ahead with creating the implementaiton plan"
- Verbatim prompt: "we need out plan in a way that we can start in a clean session so we don't need to get back to our old session and understand everything again, so the implementationplan should be decoupled with previous sessions"
- Verbatim prompt: "check the plan again that if we start a new session agent or the human will understand what to continue on what to start next in this project"
- Refinement chain (stack):
  - Initial: AI recommended native Kotlin + Compose.
  - Output: a table of the choice with reasons.
  - Problem: the owner wanted a more modern or engine-based option.
  - Refinement: "go with option 1, libGDX + KTX so we see how we can modernize our snake game to another level without adding extra complexity".
  - Result: the AI checked the community evidence and found KTX stalled or tiny (3 stars on the fork, rc versions). That violates the "big, active community" rule, so libGDX was kept alone. The owner confirmed.
- Refinement chain (handoff):
  - Initial: Plan 1 was written.
  - Problem: it depended on this conversation.
  - Refinement: the owner's prompt above.
  - Result: a "Start here" section, then a cold-read agent test, then fixes.

## Decisions (alternatives and rationale)
- Neon arcade look. Alternatives: classic LCD, modern soft.
- Rules source: a 2015 history of Finnish video games via Wikipedia, with our own rules labelled. Alternative: Nokia manual. It was searched, and no rules were found there.
- libGDX alone, no KTX. Alternatives: Compose Canvas only (the AI's first pick), Godot, KTX. Rationale: the owner's community rule, checked against GitHub data.
- Menus in Compose, board in libGDX. Rationale: accessibility.
- Four modules, no DI library.
- Rebase merge only (AI's call, in the spec). Note: the CLAUDE.md read at capture time says squash only (ADR-0007). This was later changed, and the change is not in this session's digest.
- detekt dropped (ADR-0004). Kotlin 2.4.20 (ADR-0006), because of CodeQL support. Signing key lasts for good (ADR-0005). minSdk 31.
- Public repos, personal account. AI review manual. Claude review local only.
- Four plans: foundation, game engine, app, release. Each is written when the previous one merges, so versions are re-verified.
- Spec §7 split into machine-enforced rules and taught practices.

## Failures and rework
- The AI overwrote `.gitignore` during git init. It restored the 225-byte standard file, and it said it could not prove an exact match.
- The blocker in the first design (Kotlin 2.4.21 vs CodeQL 2.4.20) was found by the external review and fixed by ADR-0006.
- The first plan was not session-independent. The owner flagged it, and a cold-read test found more gaps. The most serious one was not shown in the digest.
- Stale commit list in the plan, and a contradiction about the AI author line in commits, were both fixed.

## Human contribution
Set requirements and non-negotiables. Chose the look, stack, release path, repo visibility, review policy and plugin structure. Demanded an external review. Insisted on a session-independent plan. Challenged an unclear suggestion.

## AI contribution
Facilitated the brainstorm one question at a time, with explicit guesses. Researched sources and versions with evidence. Ran independent reviewer and cold-read subagents. Wrote the spec, the plan and the handoff memory. Did the machine setup. Admitted its own mistake.

## Concepts and principles learned
- Verify community health with data before adopting a dependency.
- Separate machine-enforced rules from taught practices.
- Test a handoff with a cold-read agent.
- Label each rule by source: "from the source" or "our call".
- Write plans in waves so version facts stay current.

## Durable lessons (candidates for assistant memory)
- Always check the existing file before running `git init` or overwriting anything, because the starter `.gitignore` was overwritten here.
- Always make plans self-contained, with a "Start here" section and resume rules, because the next session starts without context.
- Always verify a dependency's stars, last commit and release stability before recommending it, because KTX failed the owner's rule.
- Always check a toolchain constraint (CodeQL's Kotlin support) before pinning versions.

## Presentation candidates
- "The cold-read test": a fresh agent validates the handoff. Key message: documentation is only proven when someone with no context uses it. Example: the gaps it found, such as no README or CLAUDE.md.
- "Evidence beats recommendation": the AI's KTX recommendation was reversed by GitHub API data.
- "An external reviewer found the blocker": an independent review plus self-verification against the official source.

## Evidence
- Files: `docs/superpowers/specs/2026-10-22-supersnake-design.md`, `docs/superpowers/plans/2026-10-22-plan-1-foundation.md`, `docs/design/neon-arcade-mockup.html`, README.md, CLAUDE.md.
- Commits, as quoted by the assistant: c51cd73, 380cda8, b127e67, ad75d14, c41eca1, 179a06f, b32fb27, 730e1e7.
- Subagent tasks: version verification, builder check, external review, cold-read test.

## Gaps and uncertainty
- Commit hashes were not re-verified against git at capture time.
- Parts of the AI messages are truncated in the digest. The full list of external-review findings and the cold-read gaps are only partly visible.
- The rebase-vs-squash merge decision changed after this session. The reason is not in this evidence.
- The tool output behind the version table was not captured.
