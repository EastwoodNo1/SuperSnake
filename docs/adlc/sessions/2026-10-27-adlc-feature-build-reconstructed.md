---
session: Unknown (not captured; the session that built the ADLC feature itself)
date: 2026-10-27
harness: claude
branch(es): plugin repo android-sdlc-skills (PR #8); SuperSnake adoption branch (commit ae6c44d)
evidence_status: Reconstructed. No session digest exists. Built from the merged plugin PR #8 (description, 16 commit messages and file list), the SuperSnake adoption commit and issue, and the repo's own documents. Prompts are Unknown and are not quoted.
---

# Session record: building the ADLC documentation feature (Reconstructed)

## Goal
Observed (PR #8 "Why"): give every project that uses the plugin the same evidence-based record of how it was built, covering prompts, decisions, failures, human and AI contribution, and lessons. Capture is automatic per session. The result is safe to commit to a public repo. Codex and Copilot adapters can be added later without redesign. First user: SuperSnake, as source material for a learning presentation.

## What happened (short chronology)
All times are commit times on 2026-10-27 (UTC), Observed from the PR.
1. 14:10 Design spec and implementation plan committed to the plugin repo (`docs/superpowers/specs/2026-10-27-adlc-docs-design.md`, `.../plans/2026-10-27-adlc-docs.md`).
2. 14:13 Spike results recorded. The design made two facts a precondition: a detached child started by a `SessionEnd` hook must survive session exit, and a plugin agent must resolve headless. Both were checked with the real CLI (`-p`, interactive `/exit`, double Ctrl-C; `--agent` with hooks disabled).
3. 14:15 to 14:16 The `adlc-documenter` agent, seven templates, and policy, privacy and harness references. A fix followed one minute later: the agent had dropped the source's invocation workflow, and "move" contradicted leaving staged originals for the main agent to delete.
4. 14:21 to 14:23 `digest.sh` (compact transcript view) and a bash test harness. A fix: negative assertions written as `! grep -q` are exempt from `set -e`, so they could never fail.
5. 14:25 to 14:31 `session-end.sh` (guards, detached launch) and `capture.sh` (lock, headless Sonnet run, redaction). Real-CLI findings: `Write(path)` allow rules are not matched by file checks (use `Edit(//abs/**)`), and notes could reach `staged/` unredacted on failure paths, so the agent now writes to a work folder and `capture.sh` redacts then moves.
6. 14:36 to 14:38 `backfill.sh` and the `SessionStart` catch-up. A fix: a capture reading stdin could swallow the backfill queue, and busy exits counted as captures.
7. 14:48 Hook registration, the `adlc-docs` skill, version 0.2.0. A real end-to-end run showed Claude Code refuses writes inside `.git`, so the raw note moved to a private temp dir outside `.git`.
8. 14:59 to 15:07 A hardening wave: secret-shaped token and PEM redaction, `scan.sh`, path-scoped Read/Edit/Grep/Glob permissions, bounded retries, then a narrowing of the issue-key redaction to owner-listed keys, because a generic pattern over-redacted ADR-0006, SHA-256 and UTF-8.
9. 15:11 PR #8 opened. 15:13 a CI-only failure (shellcheck 0.9.0 on the runner flagged SC2015 and SC2120; the local 0.11.0 did not) was fixed. 15:17 merged.
10. Then SuperSnake adopted the plugin: issue #44 "Adopt ADLC lifecycle documentation" and commit ae6c44d (templates, CLAUDE.md section, plugin pin at v0.2.0). The first fold-in is this documentation.

## Key prompts
Reconstructed prompt intent - not verbatim: the owner asked for portable, automatic lifecycle documentation for every plugin project, first for SuperSnake, as source material for a learning presentation, ready for Codex and Copilot. This is stated in the plan commit message and PR "Why". The actual prompts and any refinements are Unknown.

## Decisions (observable rationale only)
- Capture runs automatically from a `SessionEnd` hook as a detached process (`nohup` + `disown`), within the 1.5 s hook budget, so quitting is never blocked. Gated by the spike.
- Two agent modes, `capture` and `fold-in`, in one agent.
- State lives in `<git-common-dir>/adlc/`, never committed, shared across worktrees. Raw note and digest live in a 0700 temp dir outside `.git`.
- Privacy in three layers: model-side filter, mechanical redaction before staging, and `scan.sh` before the fold-in commit.
- Trivial sessions (fewer than 3 prompts) are skipped. A session that fails twice stops retrying.
- Issue-key redaction only for owner-listed keys, replacing a generic pattern (reversal of the first draft after it damaged legitimate text).
- Bash, git and jq only, with a stub `claude` for tests.

## Failures and rework
Observed from the PR's fix commits and "Real-CLI integration findings": every item in the chronology above marked "fix" or "finding". Pattern: each was found by review, a real CLI run, or CI, not by the first implementation. The spec and plan were then updated to describe what was built, in a "Changes during implementation" section (the plan and spec themselves are in the plugin repo).

## Human contribution
Reconstructed: set the purpose and constraints (public repo, privacy, portability), approved the design, and is the author of record of the commits. Specific interactions are Unknown.

## AI contribution
Observed from commit trailers: commits carry a Co-Authored-By line for Sonnet 5.5 (implementation) and Opus 5.5 (the design, plan, spike and the CI fix). Each task had a spec and a quality review with fix loops, then a whole-branch review and one fix wave (PR "Reviews" section). Real-CLI smoke test on Sonnet piped a real SuperSnake transcript through the capture entry point (12 sections, no email or home path in the note).

## Concepts and principles learned
- Test the platform's behaviour with a spike before designing on it.
- Privacy gates need layers, and a gate that over-redacts damages the record as much as one that under-redacts.
- A test that cannot fail is not a test (`! grep -q` under `set -e`).
- The CI runner's tool versions can differ from local ones.

## Durable lessons (candidates for assistant memory)
- Always check the local tool version against the CI runner's version for linters, because shellcheck 0.9.0 failed where 0.11.0 passed.
- Always show that a negative assertion can fail, because `! cmd` is exempt from `set -e`.

## Presentation candidates
- Headline: "Documenting the AI process with AI, safely." Message: automatic capture, three privacy layers, human-run fold-in.
- Headline: "The spike came first." Message: the design depended on two platform facts and tested them before building.

## Evidence
- Plugin repo `EastwoodNo1/android-sdlc-skills`: PR #8 (merged 2026-10-27 15:17 UTC), 16 commits starting ab2a2c8, spec and plan files named above, CI run linked from the PR.
- SuperSnake: issue #44 (open), commit ae6c44d.
- PR #8 test evidence (88 tests, 0 failed; template drift 0) is quoted from the PR description and was not reproduced here.

## Gaps and uncertainty
- No digest or prompts for this session. Who asked for which design change is Unknown.
- The number of sessions involved and exact timing of the design phase (the spec commit is at 14:10, so earlier work is not visible) are Unknown.
- The spec and plan files in the plugin repo were not read in this fold-in; only the PR description and commits were.
- Whether the adoption commit on this branch has a pull request is Unknown at the time of writing.
