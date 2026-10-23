# SuperSnake: instructions for AI agents

This is a temporary bootstrap file. Task 9 of Plan 1 replaces it with the full version.

## Start of every session

1. Read the **Start here** section of the active plan: `docs/superpowers/plans/2026-10-22-plan-1-foundation.md`.
2. Find where work stands, following that section's "Progress lives in git" rules:
   - `git log --oneline`
   - `git worktree list`
   - `gh pr list` (once the repo is on GitHub)
3. Continue only from what the plan says. Don't rely on memory of earlier sessions.

## Rules that always apply

The full list is in the plan's Global Constraints and in the spec.

- No comments in code or config. The why and what go in the commit message.
- Never claim something works without the command and its real output, or a CI link.
- Conventional Commits.
- Only the latest stable versions, verified with a source on the day they're used.
- Minimal third-party dependencies.
- Every subagent runs on model Sonnet 5.5 (`model: "sonnet"`).
- Stop and ask the owner before:
  - anything outward-facing the plan doesn't list
  - deleting anything the plan doesn't list
  - changing a decision in the spec
