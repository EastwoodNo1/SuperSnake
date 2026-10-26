# SuperSnake: instructions for AI agents

Full detail is in [`CONTRIBUTING.md`](CONTRIBUTING.md). This file is the short form.

## Start of every session

1. Read the **Start here** section of the active plan: `docs/superpowers/plans/2026-10-22-plan-1-foundation.md`.
2. Find where work stands, following that section's "Progress lives in git" rules:
   - `git log --oneline`
   - `git worktree list`
   - `gh pr list`
3. Continue only from what the plan says. Don't rely on memory of earlier sessions.

## Rules that always apply

The full list is in the plan's Global Constraints, the spec and `CONTRIBUTING.md`.

- No comments in code or config. The why and what go in the commit message. Strip comments that tools write into generated files.
- Never claim something works without the command and its real output, or a CI link.
- Conventional Commits, with a Why: and What: body. A commit written by an agent ends with the `Co-Authored-By:` line of the model that wrote it.
- Only the latest stable versions, verified with a source on the day they're used, and recorded in `docs/versions.md`. Kotlin stops at the newest version CodeQL supports (ADR-0006).
- Minimal third-party dependencies (spec §6). Evidence in the PR is required for each new one.
- Every subagent runs on model Sonnet 5.5 (`model: "sonnet"`).
- Stop and ask the owner before:
  - anything outward-facing the plan doesn't list
  - deleting anything the plan doesn't list
  - changing a decision in the spec

## How the repo enforces them

- Work in a worktree: `git worktree add ../SuperSnake-<name> -b <type>/<name>`. The main checkout and the `main` branch refuse commits.
- Setup is `scripts/bootstrap.sh`. It sets `core.hooksPath=.githooks`. Run Gradle through mise: `~/.local/bin/mise exec -- ./gradlew ...`.
- Hooks: pre-commit (`:checkNoComments`, `:ktlintCheck`), commit-msg (`:checkCommitMessage`), pre-push (refuses `main`, runs zizmor offline, then strict `check`).
- Strict gate: `scripts/gradle-strict.sh <tasks>` fails on any deprecation except one allowlisted AGP 9.4.1 message.
- Invoke root check tasks with a leading colon (`:checkNoComments`, `:checkWorkflows`, `:checkReleaseManifest`, `:checkPullRequest`) because AGP registers same-named tasks in `:app`.
- Merge is squash only. The PR title is a Conventional Commit header of at most 64 characters (GitHub adds ` (#N)`). The PR body has `Closes #<issue>` and a filled `## Evidence` section. See ADR-0007.
- Required checks on `main`: `checks`, `pr-rules`, `analyze (java-kotlin)`, `analyze (actions)`.
- A Dependabot Gradle PR needs `gradle/verification-metadata.xml` refreshed: follow "Dependency verification updates" in `docs/versions.md`.

## Skills

The SDLC skills come from the plugin `android-sdlc`, installed from `EastwoodNo1/android-sdlc-skills` at tag `v0.1.0` through `.claude/settings.json`. Use them:

- `android-sdlc:git-hooks`: hooks, `scripts/bootstrap.sh`, `scripts/test-hooks.sh`.
- `android-sdlc:android-ci`: workflows, mise pins, Dependabot, `build-logic/checks`, repo settings. It re-verifies every pin before use.
- `android-sdlc:sdlc-rules`: the workflow, the evidence rule, dependencies, versions, Dependabot, releases.

The templates in the plugin are copies of this repo's files. After changing a file they copy, update the plugin repo and run its `scripts/verify-templates.sh <path to this repo>`: it must print `0 drift`. `CONTRIBUTING.md` stays the human-readable source.
