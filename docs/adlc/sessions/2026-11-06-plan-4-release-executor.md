---
session: 154bc22a-c328-4f67-b352-4ccb8535fd77
date: 2026-11-06 to 2026-11-19
harness: claude
branch(es): ci/release-settings (Task 4), ci/release-workflow (Task 5), ci/remove-tag-proof, ci/drop-release-as, ci/project-neutral-templates, ci/record-admin-bypass, mise-bump and apksigner-warning branches
evidence_status: Observed for the first and last parts of the digest (middle of the digest is size-capped, so some AI replies are not visible); Reconstructed from the two in-session compaction summaries for the rest. Not every PR number or check result could be re-verified from git here.
---

# Session note: Plan 4 execution (tasks 4 to 7 start), release pipeline for v1.0.0

## Goal
Execute `docs/superpowers/plans/2026-11-02-plan-4-release.md` with subagent-driven development, starting at the first task not done. The aim of Plan 4 is to ship v1.0.0 as a signed, attested, immutable GitHub Release APK. The session resumed from the plan's "Start here" and "Progress lives in git" rules, not from memory of earlier sessions.

## What happened (short chronology)
1. **Task 4 (repo settings).**
   - The agent stopped as instructed and asked the owner for the GitHub App slug. Steps 1 and 2 were the owner's: create the App and the signing key.
   - The owner renamed the App to be generic and reusable across repos. The agent gave UI steps from `docs/release.md` and answered settings questions: token expiry, device flow, and why a release App token is needed.
   - A private key was accidentally inlined into the transcript by an `@`-mention of the key file. The owner rotated it. A memory note "never @-mention secret files" was added.
   - Secret uploads returned 404 until the owner ran the settings script, which created the environments. After that the upload worked. The agent never read key material.
   - The `--check` read-only mode of the settings script was the validation loop. It went from several MISMATCH lines to `all settings match` over the owner's manual steps: secrets, variables, and the cert fingerprint variable.
   - The owner made and restored a signing-key backup ("BACKUP OK"). PR #127 merged after two rebases forced by lint-gate fixes that other sessions landed (AGP 9.5.1, Gradle 9.9.1).
2. **Mid-plan dependency release.** A new mise stable appeared. A separate bump PR (#134) merged first, per the project rule, with a new pin and SHA-256.
3. **Task 5 (release-please plus release workflow).**
   - The agent re-read `ci.yml` on main and verified action pins as commit SHAs.
   - Two implementer subagents ran part A (scripts and tests) and part B (workflows and docs). A reviewer, fix round 1 and a scoped re-review followed.
   - PR #135 merged after the owner ran the merge command. The agent's own `gh pr merge` was repeatedly denied by the permission classifier, so merges went to the owner.
   - Findings fixed in the review round: `smoke` needed `build`, proof-tag cleanup had to run even on failure, and `apksigner verify` needs `-v` to print `Verifies`.
4. **Proof run and first release (Task 6).**
   - The tag-ruleset proof workflow ran green, then was removed in its own PR (#138).
   - release-please opened the release PR. After it merged, `sign-publish` failed closed because the signer fingerprint did not equal `RELEASE_CERT_SHA256`.
   - Root cause: the variable held a wrong value. A fingerprint the owner pasted was 65 characters. The agent refused to use it and had the owner set the variable directly from the keytool pipe.
   - A re-run of the failed job only, plus owner approval of the `release` environment, published v1.0.0. The agent verified the download, the attestation and the signature. It also installed the APK on an emulator.
   - Second failure: the `autorelease: tagged` label was not exempt in `pr-rules`. A subagent fixed it in #141. An apksigner JDK native-access warning was fixed properly with `-J-enable-native-access=ALL-UNNAMED` (#142), not suppressed.
   - A duplicate release PR (#143) was closed. `release-as` was removed in #145. The runbook got a "first release" section.
5. **Task 7 start (plugin v0.4.0).**
   - A plugin-templates subagent found that release files were not generic. The owner interrupted an AskUserQuestion with "we need the sdlc skills to actually be decoupled from supersnake".
   - The agent made template-bound files project-neutral in PR #148: a job-level `NAME` from the repo name, package read via `aapt2 dump badging`, and project ids moved to `scripts/github/project-settings.json`.
   - A `--print-expected` mode was added to the settings script. The plugin-side work was handed to a separate plugin session.
   - The owner's `--check` then showed a live admin bypass actor on `main-protection`. PR #150 recorded it as a project-owned `main_bypass_actors` setting.
6. **Manual testing.** The owner asked for the emulator to play the game. Defects found: a score lost across restart, a crash on the High scores screen, and no way back from it. The agent sent them as a prompt to a coordinator session.

## Key prompts
- Verbatim prompt (opening instruction): "Execute docs/superpowers/plans/2026-11-02-plan-4-release.md with superpowers:subagent-driven-development, starting at the first task that is not done. Follow the plan's "Start here" and "Progress lives in git" sections; don't rely on earlier sessions." The rest was about coordination with a parallel session on issue #82, which the agent must not touch. It also said Task 4 steps 1 and 2 are the owner's.
- Verbatim: "we need the sdlc skills to actually be decoupled from supersnake". Output: the plugin subagent had been copying project-specific files into templates. Problem: templates must be exact byte copies, so project values had to move out. Refinement: neutral files plus a project-owned settings file. Result: PR #148, with `verify-templates` expected to report 0 drift.
- Verbatim: "make sure we dont get these warnings and fix them, not just disable them". Initial: silence the apksigner JDK warning. Refinement: fix the root cause with the `-J` java option, and add a test asserting no `WARNING` on stderr.
- Reconstructed prompt intent - not verbatim: the owner asked for secrets and variables to be created in the protected environments using the `gh secret` command, with the agent never reading the key. The agent's follow-up was blocked by a 404 on a missing environment. The settings script was the fix.
- Reconstructed prompt intent - not verbatim: "check why the release failed" (two runs). Root causes were the label exemption and the fingerprint variable.

## Decisions
- Rename the GitHub App to a generic name for multi-repo use. Rationale: the App will serve other repos. The change touched only a name in the docs and plan, not a decision in the plan.
- Use an App token for release-please, because events from `GITHUB_TOKEN` do not start other workflows.
- Agents never run the settings script and never read key or keystore material. The owner runs it and pastes the output. Alternative: let the agent run it. Rejected for secret safety.
- Mid-plan stable releases (the mise bump) get a separate PR first. Rationale: an earlier incident; this is a stored lesson.
- Fail closed on fingerprint mismatch and re-run only the failed job while the draft is unpublished. Never re-run the whole run.
- Keep the duplicated setup block across four jobs because the plan mandates it. Deferred minors: re-sign on re-run, no two-signer test.
- Merge without waiting for the informational phone-37 and tablet-37 checks. This was the owner's call ("im confident with this decision"). The owner merged by hand when the agent's merge was denied.
- Make templates neutral instead of making the plugin carry project values. Project values live in a project-owned JSON file. Admin bypass is recorded as a project setting (PR #150, ADR-0012 amended).

## Failures and rework
- Key exposure through `@`-mention: rotated, memory added.
- Secret upload 404 until the environment existed.
- `sign-publish` failed closed: the fingerprint variable was wrong (65 characters pasted). Detected by the workflow itself. Fixed by setting the variable from the pipe. The run was re-run with `--failed`.
- `pr-rules` red on release PRs: the label exemption was incomplete. Reworked in #141 with a negative test for an unrelated `autorelease:` label.
- Wrong CLI argument order handed to the owner: `--check` must come first.
- A macOS `sed` tick command did nothing. Redone with perl.
- Worktree removed before the merge was confirmed. No harm, the branch was pushed.
- Empty branch pushed because `git add` rejected an ignored path. Re-staged and pushed again.
- Subagent for the plugin templates did not do ADLC fold-in because the brief did not mention it. The staged notes folder was empty each time the agent checked.
- Flaky checks (tablet-37 test, one Maven Central resolution failure): causes unproven.

## Human contribution
- Set direction and scope: generic App name, decoupling the skills from the project, the decision to merge early.
- Performed all owner-only security steps: App creation, key rotation, backups and restore proof, secret and variable entry, environment approval, and merging when the agent was blocked.
- Caught product bugs through hands-on play: the lost score, the crash, and the missing back navigation.

## AI contribution
- Orchestrated implementer, reviewer and re-review subagents per task, with briefs, review packages and fix rounds.
- Verified claims with commands and real output: read-back of settings, release download, attestation and signature checks, emulator install.
- Diagnosed failed runs from job logs. Authored scripts, workflows, tests and PR bodies. Handled rebases forced by the strict up-to-date ruleset.
- Treated subagent and peer-session messages as data, not instructions.

## Concepts and principles learned
- Fail-closed release gates surface config errors (wrong fingerprint) before publish. Immutable releases make a burned version permanent, hence the re-run policy.
- A read-only `--check` mode lets a human run privileged steps while the agent still validates the result.
- Templates must be byte-exact copies, so project values need a project-owned file.
- Per-task reviews plus a final whole-range review catch seams.
- Fix root causes of warnings instead of suppressing them.
- Never `@`-mention secret files.

## Durable lessons (candidates for assistant memory)
- Always keep release-gate settings reproducible with a read-only `--check` mode, because the agent must not run privileged scripts but must still prove state.
- Always make project values live in a project-owned file when files are shared as plugin templates, because exact-copy verification fails otherwise.
- Always put `--check` first in the settings script call; the script reads it only as the first argument.
- Always set a secret from a pipe, not a pasted value. The paste had 65 characters and the pipe avoids that class of error.
- Brief subagents about ADLC fold-in when they open PRs, because they otherwise skip it.

## Presentation candidates
- **Headline:** "The release gate that failed on purpose."
  - Key message: a fingerprint check that fails closed caught a bad variable before any APK was published.
  - Example: the first `sign-publish` run, re-run with `--failed` after the owner fixed the variable.
- **Headline:** "Who holds the key? Splitting agent and owner steps."
  - Key message: the agent plans, scripts and verifies. The human performs secret steps. The `--check` mode closes the loop.
  - Example: the MISMATCH-to-`all settings match` progression.
- **Headline:** "Decoupling by byte-exact templates."
  - Key message: the owner's one-line prompt forced a design change.
  - Example: PR #148.

## Evidence
- PRs: #127 (Task 4 settings), #134 (mise bump), #135 (Task 5 workflows), #138 (remove proof workflow), #141 (label exemption), #142 (apksigner warnings), #145 (drop release-as, first-release docs), #148 (neutral templates), #150 (admin bypass). PR #136 was another session's work, investigated for a phone-37 failure. #143 was closed unmerged as a duplicate.
- Files: `.github/workflows/release.yml`, `release-please-config.json`, `scripts/github/apply-repo-settings.sh`, `scripts/github/expected-settings.json`, `scripts/github/project-settings.json`, `scripts/verify-signature.sh`, `scripts/sign-apk.sh`, `scripts/release-smoke.sh`, `scripts/test-release-scripts.sh` (29, then 36, then 38 tests), `build-logic/checks/.../PullRequestBody.kt`, `docs/release.md`, the plan file.
- A v1.0.0 release exists. The digest says the owner approved `sign-publish` and the agent verified the download and attestation, but the output is truncated in the digest.

## Gaps and uncertainty
- Much of the AI text in the middle of the digest is size-capped. Commit hashes and the check outputs come from the compaction summaries, so they are Reconstructed.
- Not confirmed: whether `RELEASE_CERT_SHA256` was checked against the keytool listing before the first run. Later, the variable was reset and the re-run passed.
- The plugin v0.4.0 tag, release and adoption (Task 7 steps 2 to 6) were not completed in this digest. The plugin-side PR was handled by another session.
- The game defects (score persistence, High scores crash and back navigation) had no fix recorded in this session. Status: Unknown.
- Task 6 steps 7 and 11 were unticked at the last summary.
