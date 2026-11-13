---
session: 154bc22a-c328-4f67-b352-4ccb8535fd77
date: 2026-11-06
harness: claude
branches: ci/release-settings (local, unpushed); main at e633d7b then d692407
evidence_status: Observed from the session digest. No commit content, PR or CI output was inspected. Subagent results are the agents' own reports.
---

## Goal
Run Plan 4 (release) with subagent-driven development from the first task that is not done. That was Task 4: extend the repo-settings script (`--check` mode, environments, tag ruleset, labels, immutable releases) and register a GitHub App that release-please uses for its token.

## What happened
1. The agent read the plan's "Start here" section and git state. Task 4 was next, with no worktree, branch or PR. The owner had reserved Steps 1 and 2 (create the App and the signing key), so the agent stopped and asked for the App slug. It also told the coordinator session that it was waiting.
2. The owner renamed the App to a generic name because it will serve other repos later. They asked for the UI steps to create it. The agent pointed to `docs/release.md` and gave the field values.
3. The owner asked three permission questions: the user-token settings, Device Flow, and whether the App needs Workflows write. The agent said no to all three. It explained that the App token exists so that release PRs trigger CI, because `GITHUB_TOKEN` events start no workflows, and that this needs only Contents and Pull requests write.
4. The owner @-mentioned the private key file. The harness attached its contents to the agent's context. The agent flagged the key as exposed and asked for rotation and for the old key to be deleted. It said the upload must go through `gh secret set < file` and that it would never open the file. The owner rotated the key.
5. The App slug lookup via `gh api` returned 404, because the App is private. The agent asked for the numeric App ID instead, since the tag ruleset's bypass actor needs it.
6. The owner ran the secret upload. It failed with 404 because the `release-please` environment did not exist yet. The agent explained that the plan creates environments in the settings script (Step 5). The owner chose the plan order and gave the App ID.
7. The agent created the issue and a worktree, then dispatched a Sonnet implementer. The implementer made two commits: a RED commit and a writes commit. It verified with shellcheck 0.9.0, `bash -n`, `:checkNoComments` and a fake-`gh` harness. A separate Sonnet reviewer found no Critical or Important issues.
8. The strict gate failed on `AndroidGradlePluginVersion` lint, because AGP 9.5.1 had been released. The agent proposed a separate bump PR, following an earlier owner preference. The coordinator said the parallel #82 session already had a bump PR, so the agent held back. After that PR (#123) merged, the agent rebased onto main, and the strict gate passed.
9. The coordinator asked for the PR to be pushed. The agent declined for now, because the Evidence section needs the owner's live run outputs. It listed the owner steps: the RED `--check` run, confirming the App ID, the apply run and the secrets upload.

## Key prompts
- Verbatim prompt (project-related): "Execute docs/superpowers/plans/2026-11-02-plan-4-release.md with superpowers:subagent-driven-development, starting at the first task that is not done. Follow the plan's "Start here" and "Progress lives in git" sections; don't rely on earlier sessions."
- Reconstructed prompt intent - not verbatim: don't touch the parallel #82 scope, rebase onto main if it moves, re-read ci.yml when Task 5 starts, and stop and ask for the App slug before Task 4 Step 3.
- Reconstructed prompt intent - not verbatim: create the repo variables and secrets in the protected environment, upload the key with the `gh` secret command without reading it, and set up the environment if missing.
  - Refinement chain: Initial (the @-mention of the key file) -> Output (the harness attached the key contents to the context) -> Problem (a secret is now in the transcript) -> Refinement (rotate the key, delete the old one, pass paths as plain text, run the upload via `!`) -> Result (new key, upload command run by the owner; it 404s until the environment exists).

## Decisions
- App named generically because it will be reused across repos. The alternative was a project-specific name. Docs are renamed in the Task 4 PR. The name is not a spec decision.
- Workflows permission stays No access. The alternative was to grant it so that workflows would start. Rationale: the App token only exists to trigger CI, and Workflows write would let a leaked key rewrite the release gate.
- Environments are created by `apply-repo-settings.sh` (plan order, option 1) rather than by hand, so that every setting lives in one reviewed file.
- The AGP bump goes in a separate PR that the other session owns. The alternatives were a bundled bump or suppressing the lint. Rationale: it matches an earlier owner preference, and a second PR would collide.
- The Task 4 PR is not opened until the owner's run outputs exist, because the Evidence rule requires them and `pr-rules` would fail an empty section.

## Failures and rework
- The key was exposed through an @-mention. It was rotated, and the old key deleted. Root cause: the harness inlines the file contents of @-mentioned paths.
- The `gh secret set` upload returned 404 because it was run before the environment existed. The fix was to follow the plan order.
- The App slug lookup returned 404 for the private App, so the numeric ID had to come from the owner.
- The strict gate failed on the new AGP release lint. The bump merged from the other session, then the branch was rebased.

## Human contribution
The owner made the manual App and key steps, naming and security choices (permissions, rename) and the key rotation. They also ran the upload and the ID lookup, and arbitrated between the coordinator's requests and the evidence rules.

## AI contribution
The agent assessed plan state and stopped at the owner-only steps. It flagged and handled the key exposure. It explained the permission model and ran implementer and reviewer subagents. It diagnosed the gate failure and respected cross-session coordination. It declined to open a PR with empty evidence.

## Concepts and principles learned
- Events from `GITHUB_TOKEN` do not start workflows. An App token is needed for release PRs to get CI.
- Least privilege for App permissions: Workflows write is a high-risk permission.
- A private GitHub App is not readable through the API by a normal token.
- A parallel session needs a coordinator so that work does not duplicate (the AGP bump).

## Durable lessons
- Never @-mention a secret file path in a prompt, because the harness attaches its contents to the context. Give a plain-text path, or run the command with `!`.
- Check the environment exists before `gh secret set --env`, because it returns 404 and does not create the environment.
- Ask the owner for the numeric App ID, because a private App returns 404 from `gh api apps/<slug>`.
- Before opening a PR, confirm the owner-run evidence is in hand, because an empty Evidence section fails `pr-rules`.

## Presentation candidates
- Headline: "A secret leaked by an @-mention". Key message: convenience features of the harness can break secret handling, so rotate and change the workflow. Example: the key rotation steps in this session.
- Headline: "Least privilege answered by the agent". Key message: ask why a permission is needed and trace the actual flow. Example: the Workflows write discussion.

## Evidence
- Issue #121 and worktree `../SuperSnake-release-settings` on branch `ci/release-settings`. Commits `a2edf5e` (RED) and `5333dd7` (writes), later rebased onto d692407. Not pushed at the end of the session.
- PR #123 (AGP 9.5.1 bump, from the other session).
- Subagent artifacts under `.superpowers/sdd/2026-11-02-plan-4-release/` (brief, report, review).
- The docs mention the old App name in `docs/release.md` and the plan.

## Gaps and uncertainty
- The Task 4 outcome is unknown at session end: the RED and apply runs, the secrets and the PR are pending the owner.
- The review and the implementer outputs are only known from their summaries.
- The reviewer's findings text and the full strict-gate output were not seen.
