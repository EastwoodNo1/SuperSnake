# SuperSnake: instructions for AI agents

Full detail is in [`CONTRIBUTING.md`](CONTRIBUTING.md). This file is the short form.

## Start of every session

1. Read the **Start here** section of the active plan: `docs/superpowers/plans/2026-10-28-plan-3-app.md`.
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
- Strict gate: `scripts/gradle-strict.sh <tasks>` fails on any deprecation except one allowlisted Gradle message (Configuration.setVisible, emitted by Kover).
- Invoke root check tasks with a leading colon (`:checkNoComments`, `:checkWorkflows`, `:checkReleaseManifest`, `:checkPullRequest`) because AGP registers same-named tasks in `:app`.
- Merge is squash only. The PR title is a Conventional Commit header of at most 64 characters (GitHub adds ` (#N)`). The PR body has `Closes #<issue>` and a filled `## Evidence` section. See ADR-0007.
- Required checks on `main`: `checks`, `pr-rules`, `analyze (java-kotlin)`, `analyze (actions)`.
- A Dependabot Gradle PR needs `gradle/verification-metadata.xml` refreshed: follow "Dependency verification updates" in `docs/versions.md`.

## Skills

The SDLC skills come from the plugin `android-sdlc`, installed from `EastwoodNo1/android-sdlc-skills` at tag `v0.2.0` through `.claude/settings.json`. Use them:

- `android-sdlc:git-hooks`: hooks, `scripts/bootstrap.sh`, `scripts/test-hooks.sh`.
- `android-sdlc:android-ci`: workflows, mise pins, Dependabot, `build-logic/checks`, repo settings. It re-verifies every pin before use.
- `android-sdlc:sdlc-rules`: the workflow, the evidence rule, dependencies, versions, Dependabot, releases.
- `android-sdlc:adlc-docs`: ADLC lifecycle documentation: adopt, fold-in before a PR, backfill, the off switch. Capture runs automatically through the plugin hooks.

The templates in the plugin are copies of this repo's files. After changing a file they copy, update the plugin repo and run its `scripts/verify-templates.sh <path to this repo>`: it must print `0 drift`. `CONTRIBUTING.md` stays the human-readable source.

### Install the plugin once per machine

Project settings declare the plugin but do not install it. Run these once from the repo root (the commands are pinned to `v0.2.0`):

```sh
claude plugin marketplace add EastwoodNo1/android-sdlc-skills#v0.2.0 --scope project
claude plugin install android-sdlc@android-sdlc-skills --scope project
claude plugin list
```

Accept the workspace trust prompt the first time you open the folder in Claude Code, because `.claude/settings.json` is only honored in a trusted folder. `claude plugin list` must show `android-sdlc@android-sdlc-skills`, `Version: 0.2.0`, `Status: ✔ enabled`. A newer plugin version is adopted by changing the `ref` in `.claude/settings.json` in a PR.

## ADLC/SDLC documentation

### Purpose

Maintain a living, evidence-based record of the project's actual Software Development Lifecycle (SDLC) and AI-assisted/agentic development lifecycle (ADLC).

The documentation covers historical and ongoing work and stays understandable without undocumented conversation context. It is a self-contained source for later educational material and presentations.

### Documentation agent

Delegate lifecycle documentation to the plugin agent:

`android-sdlc:adlc-documenter`

### Capture is automatic

A session-end hook captures each non-trivial session into a staged note, without any action from the person or the agent. Staged notes are filtered for private data and wait in `$(git rev-parse --path-format=absolute --git-common-dir)/adlc/staged/`. Do not write them by hand.

### Fold-in before a pull request

Before opening or updating a pull request, check `$(git rev-parse --path-format=absolute --git-common-dir)/adlc/staged/`. If it has notes:

1. Run `android-sdlc:adlc-documenter` in fold-in mode in the PR's worktree. The prompt starts with `MODE: fold-in`, then `STAGED: <dir>` and `REPO: <worktree path>`.
2. Run `scan.sh docs/adlc "$(git rev-parse --path-format=absolute --git-common-dir)/adlc"`. It prints `file:line: class` for each privacy hit and exits 1. Fix every hit before committing.
3. Commit the result as `docs(adlc): fold in session notes`.
4. Save the returned durable lessons as memories (type `feedback` or `project`), after checking for duplicates. Only the main session has the memory system, so the documenter does not write memory.
5. Delete the folded files from `staged/`, only after the commit.

The documenter copies the staged notes and never deletes them. Pending notes from a trivial PR carry to the next one. At the end of a project, or before building a presentation, run one fold-in now and open a single `docs(adlc)` pull request.

### Historical reconstruction

On the first fold-in, if `docs/adlc/ADLC_PROCESS.md` is unpopulated, the documenter reconstructs history from git, pull requests, plans, specs and ADRs. It determines first what evidence is accessible and never fabricates missing history. A completed reconstruction is not repeated unless gaps remain or new evidence appears.

### Documentation location

Lifecycle documentation lives under `docs/adlc/`. Reuse equivalent existing documentation instead of creating duplicates.

- `ADLC_PROCESS.md` (the human-readable entry point)
- `PROJECT_EVOLUTION_TIMELINE.md`
- `PROMPT_AND_INTERACTION_LOG.md`
- `DECISION_LOG.md`
- `REQUIREMENTS_TRACEABILITY.md`
- `SOURCE_AND_UNCERTAINTY_REGISTER.md`
- `PRESENTATION_SOURCE_GUIDE.md`
- `sessions/` (one record per folded session)

### Integrity

Never fabricate historical information. Classify information as:

- **Observed**: directly supported by evidence
- **Reconstructed**: reasonably inferred from evidence
- **Unknown**: cannot be determined reliably
- **Prospective**: introduced from the current point onward

Preserve failures, iterations, rejected approaches, reversed decisions and feedback loops. Document observable rationale, not hidden reasoning. Never delete superseded decisions.

### Privacy

The repository is public. No email addresses, people's names other than the owner handle, employer or internal system names, absolute home paths, tokens or Jira keys in `docs/adlc/`. The documenter applies the privacy filter, and `scan.sh` checks the result before the fold-in commit.

### Educational purpose

Keep the documentation self-contained enough to explain the actual SDLC/ADLC, requirement evolution, meaningful prompts and refinements, planning and design, implementation progression, testing and validation, decisions and trade-offs, failures and feedback loops, human contribution, AI contribution and lessons learned. A later presentation must be derivable from the documentation without the original sessions.

### Definition of Done

A meaningful development cycle is not complete until:

- staged session notes have been folded into `docs/adlc/` in the pull request
- durable lessons have been saved as memories
- `android-sdlc:adlc-documenter` has reported unresolved gaps, if any

### Off switch

Create `$(git rev-parse --path-format=absolute --git-common-dir)/adlc/disabled` to turn capture off for this repository. Set `ADLC_DISABLE=1` to turn it off for one session.
