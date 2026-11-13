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
