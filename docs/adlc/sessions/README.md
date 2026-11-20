# Session records

One file per working session, named `<YYYY-MM-DD>-<topic>.md`.

Each file is made by fold-in from a staged note that automatic capture wrote when the session ended. Fold-in copies the note here inside a pull request and allocates the stable IDs (`PROMPT-`, `DEC-`, `REQ-` and similar) in the logs. The session record itself carries no IDs.

The front matter and sections are the staged note format:

Front matter: `session`, `date`, `harness`, `branch(es)`, `evidence_status` (a summary).

Sections:

- Goal
- What happened (short chronology)
- Key prompts: verbatim only when they pass the privacy filter, otherwise `Reconstructed prompt intent - not verbatim`. Refinements use the form `Initial -> Output -> Problem -> Refinement -> Result`.
- Decisions (with alternatives and observable rationale)
- Failures and rework
- Human contribution
- AI contribution
- Concepts and principles learned
- Durable lessons (candidates for assistant memory)
- Presentation candidates (headline, key message, example)
- Evidence (commits, PRs, files)
- Gaps and uncertainty

Every statement is classified Observed, Reconstructed, Unknown or Prospective. Session records are history: do not rewrite them after fold-in, add corrections to the logs instead.

- 2026-11-05-plugin-reload-no-op.md: session 3c11e9a7, plugin inspection and reload only; trivial, no project work. The release-smoke resume fix (issue #117) has no staged note of its own; it is recorded in the logs from the main agent's context (SRC-014).
- 2026-11-06-plan-4-task-4-start.md: session 154bc22a, Plan 4 Task 4 start, key exposure and rotation (key content not recorded).
- 2026-11-06-issue-82-api37-fix.md: session 3e97d016, API 37 CI fix (PRs #123, #124). The coordinator's issue #126 work has no staged note; it is recorded from the coordinator's report (SRC-018).
- 2026-10-28-finish-up-orchestrator.md: session 35559d8c, the long orchestrating session (Plan 3 planning on 2026-10-28, Plan 4 review, CI fixes, coordination, autonomous finish-up to 2026-11-20). Re-captured at project end (see 2026-11-19-finish-up-reconstructed.md and GAP-027). Its Plan 3 planning part is already in 2026-10-28-plan-3-app-planning.md and 2026-10-28-plan-3-planning-reconstructed.md.
- 2026-11-06-plan-4-release-executor.md: session 154bc22a, the Plan 4 executor from Task 4 through Task 5, the v1.0.0 release and the start of Task 7 (2026-11-06 to 2026-11-19). Re-captured at project end; the first part overlaps 2026-11-06-plan-4-task-4-start.md, which holds the early digest.
- 2026-11-19-finish-up-reconstructed.md: no staged note. Reconstructed at the final fold-in from git, merged PRs, issues, Plan 4 and Plan 5, to cover what neither staged note shows (PRs #135 to #182 and release 1.0.1).
- 2026-11-20-finishing-session-1-0-2.md: session f877dcd1, the finishing session after the final fold-in (#184): the #181 flake closure (#185, #186, #189), plugin v0.4.2 (#188), AGP 9.5.2 (#192), the Test Orchestrator (#194), the rotation deadlock fix (#193, #195) and release 1.0.2 (#196). Captured before the session ended (plugin issue #13), so later work is not in the note. Verified against git and `gh` at fold-in (eighth fold-in, issue #197).
