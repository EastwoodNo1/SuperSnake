---
session: ea322ed1-6e1c-4b6f-a10c-b553a417707d
date: 2026-10-27
harness: claude
branches: feat/adlc-docs (plugin repo android-sdlc-skills), docs/adlc-docs (SuperSnake)
evidence_status: Observed from the session digest (prompts, tool calls, subagent briefs, reported CI and PR results). The main-agent summaries were truncated in places; PR and commit identifiers come from those summaries and were not re-verified against git.
---

# Session note: building and adopting automatic ADLC documentation

## Goal
Integrate a generic ADLC documentation template into the owner's `android-sdlc-skills` plugin. The aims were a portable way of working across projects, and automatic per-session capture for SuperSnake so a learning presentation can be built at the end. The plugin was then released as v0.2.0 and SuperSnake adopted it.

## What happened (short chronology)
1. The owner asked to integrate the template into the plugin repo. The AI asked about intent. The owner answered: document their ways of working, keep projects consistent, and later present how SuperSnake was built (process, reasoning, pros and cons, lessons).
2. The owner wanted the agent to document automatically but in an optimized way. The AI proposed milestone-only triggers, a Sonnet background agent and appending to logs.
3. The owner agreed that the agent may read transcripts. The AI flagged that both repos are public and proposed a privacy filter. The owner accepted the filter.
4. The owner asked how Claude's memory works, since it seemed similar. The AI compared memory (forward-looking steering, local, rewritten), transcripts (raw, deleted after 30 days by default) and ADLC docs (a curated, public, permanent story).
5. The owner proposed capturing per session instead of scanning at the end. The AI agreed, but pushed back on using memory as the only store. It split the design: a SessionEnd hook writes a staged note, the note is folded into `docs/adlc/` at the next PR, and only durable lessons go to memory.
6. The owner asked for it to be mainly automatic, and for the design to extend to other agents such as Copilot and Codex. The AI wrote a harness-neutral core with a Claude Code adapter. Two parallel research subagents gathered hook and agent facts for each tool. A design spec was written and reviewed.
7. The owner asked whether the plugin auto-updates. The AI explained that it is pinned to a tag by design, so a PR bumps the ref.
8. The owner approved the design. The AI wrote a plan and ran subagent-driven development: 7 tasks, each with an implementer and a reviewer on Sonnet, then a final whole-branch review and fix wave. This produced 88 passing tests.
9. PR #8 on the plugin repo failed CI on shellcheck (runner 0.9.0 against local 0.11.0). The AI fixed it and CI went green.
10. The owner approved the merge. The AI merged PR #8, tagged v0.2.0 and opened SuperSnake issue #44. It bumped the pin, ran the backfill (4 sessions captured, 2 skipped), and ran a documenter fold-in with historical reconstruction. The privacy scan was clean. It saved 5 memories.
11. SuperSnake PR #45 passed the 4 required checks and was squash-merged. The owner then asked whether to hand off to a fresh session. The AI said yes, since quitting triggers the first real end-of-session capture.

## Key prompts
- Verbatim: "use superpowers and integrate this: <downloaded template folder> into my plugin repo" (the path is omitted).
- Verbatim: "i want the agent to document automatically but in an optimized way".
- Verbatim: "rather of just scanning the who transcript history, when the project is done, wouldn't it be better to capture key learnings, concepts and principles per session, it should not rely on me asking, but capture these learnings per session i a memory which claude's handle it".
  - Initial: scan transcripts at the end.
  - Output: the AI agreed on per-session capture but proposed staging, a committed session record and memory only for durable lessons.
  - Problem: memory alone bloats the index, gets rewritten and is not in git.
  - Refinement: the owner said "mainly automatic so i never have to thing about it". This led to the SessionEnd hook, staging and fold-in at the next PR.
  - Result: the design was accepted.
- Verbatim: "no further questions, make sure we can even extend this to other agents souch as copilot, codex etc. then implement the documentation feature".
- Verbatim: "yes, merge and start supersnake adoption".
- Reconstructed prompt intent - not verbatim: the owner asked whether it was time for a handoff and a clean session.

## Decisions (alternatives and observable rationale)
- Per-session capture with staging, instead of an end-of-project scan or memory-only storage. Reasons: the 30-day transcript cleanup, memory bloat, and portability.
- Staged notes live in the shared git common dir, never committed raw, and are folded into `docs/adlc/` in the next PR. The main checkout refuses commits, and this keeps them out of public history until filtered.
- Harness-neutral core plus a Claude adapter. Copilot and Codex are documented as future adapters. Copilot's session-end hook has no transcript path.
- The plugin is pinned to a tag and adopted by PR. Rationale: an update cannot silently change a project's behaviour.
- Spike result: the detach that survives every exit path is `nohup` plus `disown`, because SessionEnd allows only 1.5 s.
- Changed during implementation (found with the real CLI):
  - Claude Code refuses writes inside `.git`, so the agent writes to a private temp dir and only the redacted note is staged.
  - A `Write(path)` permission rule is ignored. `Edit(//path/**)` is needed.
  - Issue-key redaction uses the owner's private key list instead of a generic pattern, because the generic one would redact `ADR-0006`, `SHA-256` and `UTF-8`.
- The owner is to set `cleanupPeriodDays` themselves. The skill only suggests it.

## Failures and rework
- Task reviews caught several real defects:
  - Test assertions of the form `! grep` never fail under `set -e`.
  - The documenter agent had dropped the Invocation workflow section.
  - A capture failure could leave an unredacted note in staging.
  - Skipped sessions were counted as captured.
  - The first capture could swallow the rest of the backfill queue.
- The AI's own permission ruling was wrong and was corrected by checking the real CLI.
- CI shellcheck failed because of a version mismatch with the runner.
- The plugin was only installed for three removed worktree paths, so it had never been active in the main checkout. It was installed there during adoption.
- A scripted edit did not match the file wording, so manual edits were used.

## Human contribution
- Set the goals: portable across projects, a presentation at the end, fully automatic.
- Proposed per-session capture.
- Asked the AI to check how memory works, which grounded the design.
- Required extensibility to other agents.
- Gated the publishing steps: merge, tag and adopt.
- Chose to accept the privacy filter.

## AI contribution
- Intent questions and design proposals, including the pushback on memory-only storage and the privacy risk for a public repo.
- Parallel research on harnesses.
- The design spec, the plan, and a subagent-driven implementation with reviews.
- Verification against the real CLI.
- The CI fix, release, adoption, backfill and fold-in.

## Concepts and principles learned
- Memory steers the next session. The ADLC record tells the story. Different stores fit different purposes.
- Hook time budgets force detached workers.
- Redaction patterns need testing against the project's own vocabulary.
- Negative assertions must be proven able to fail.
- Per-task reviews plus a final whole-range review caught different defects.
- Pinned plugins trade a small amount of friction for predictability.

## Durable lessons (candidates for assistant memory)
- Always run shellcheck at the CI runner's version, because versions flag different things.
- Never use a bare `! cmd` mid-test. Use an assertion helper that fails.
- Always verify an assumption about CLI permissions against the real CLI, because docs and guesses were wrong.
- Always check that the plugin is installed for the actual working directory, because installs are per project path.

## Presentation candidates
- Headline: "I never have to think about it: documentation that writes itself."
  - Message: per-session capture, a privacy filter and fold-in at the next PR.
  - Example: the memory-versus-ADLC pushback exchange.
- Headline: "Reviewers found what the implementers missed."
  - Example: the staging privacy gap, and the tests that could never fail.
- Headline: "Test against the real tool."
  - Example: `.git` write refusal and the permission rule syntax.

## Evidence
- Plugin PR #8 (closes issue #7), merged. Release v0.2.0. Spec and plan in the plugin repo's `docs/superpowers/`.
- SuperSnake issue #44, PR #45 (merged). `docs/adlc/` and the 5 session records.
- CI: 88 tests on Ubuntu, green after the shellcheck fix.

## Gaps and uncertainty
- The first real end-of-session capture via the hook was untested at the end of this session.
- The commit SHAs and counts (30 decisions, 14 requirements) come from the AI's summaries.
- The middle of the session, including subagent internals, was only partly read.
