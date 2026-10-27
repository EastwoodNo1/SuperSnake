# Requirements Traceability

Maintain evidence-based traceability where possible:

`Requirement -> Acceptance Criteria -> Design -> Implementation -> Test -> Validation -> Outcome`

Do not invent links that cannot be supported.

IDs introduced during reconstruction are documentation identifiers (allocated at the first fold-in, 2026-10-27) and must not be represented as historically existing identifiers. The spec has no requirement IDs; its own tags are `[owner]`, `[call]`, `[builder]`, rule numbers S1 to S5 and C1 to C12, and the rules table in spec §7.2. The ADRs are historical identifiers.

Evidence status: Observed (O), Reconstructed (R), Prospective (P). "Implemented" below means merged to `main` as of 2026-10-27.

| Requirement ID | Requirement | Origin | Evidence Status | Acceptance Criteria | Design | Implementation | Tests | Validation Result | Status | Evidence |
|---|---|---|---|---|---|---|---|---|---|---|
| REQ-001 | A classic Snake game for Android phones and tablets in a neon arcade look, with rules S1 to S5 and C1 to C12 | Owner, PROMPT-001 | O | Spec §1 "Success looks like": a playable, accessible game as a signed APK | Spec §2, §3; `docs/game-rules.md`; mockup | Only `Direction` in `:domain` (commit `802bffe`). No engine, board or app screens | Kover gate proven on `Direction` only | None for gameplay | Not started (Plans 2 and 3) | spec, git log |
| REQ-002 | Clean architecture: four modules (`:app`, `:board`, `:domain`, `:data`), inward dependencies, no DI framework, no branching in ViewModels | Owner, PROMPT-001; module split is a call | O | Module boundaries enforced by Gradle dependencies | Spec §5, ADR-0003 | `:app` and `:domain` exist; `:board`, `:data` not yet | Not applicable yet | None | Partly implemented (2 of 4 modules) | spec §5, Plan 1 Task 1 |
| REQ-003 | Every SDLC rule is machine-enforced, not only written | Owner | O | Spec §7.2 table: each rule has a hook, CI check or GitHub setting | Spec §7; ADR-0004, ADR-0006 | `build-logic/checks` (no-comments, commit message, PR body, tests-changed, workflow safety, manifest hardening), `.githooks`, workflows, rulesets, zizmor | `scripts/test-hooks.sh` (9/9 reported in a session note, not reproduced); checks shown to bite per task | Each gate proven with output in its PR (R: PR bodies not re-read) | Implemented for Plan 1 scope; rows for UI tests, manifest check on a real release APK and 16 KB alignment await later plans | PRs #2 to #43 |
| REQ-004 | Only the latest stable versions, verified with a source on the day and recorded in `docs/versions.md`; Kotlin capped by CodeQL | Owner, PROMPT-001 and PROMPT-003 | O | Version and date and source URL per entry | ADR-0006, DEC-014 | `docs/versions.md`, `mise.lock`, dependency verification metadata | Dependabot, CI | JDK is SHA-pinned only (honest gap) | Implemented, with a known gap | docs/versions.md |
| REQ-005 | Minimal third-party dependencies, each with evidence of a large, active community; libGDX only | Owner, PROMPT-001 | O | Stars, last commit, last stable release and issue activity in the PR; an ADR per runtime dependency | Spec §6, ADR-0001, ADR-0002 | No runtime dependency added yet (libGDX arrives in Plan 2) | n/a | KTX rejected on data | Implemented as a gate (process); dependency itself Prospective | spec §6 |
| REQ-006 | No comments in code or config; the why and what live in commit messages | Owner, PROMPT-001 | O | Syntax-aware checker over listed file types, no exceptions | Spec §7.2 | `:checkNoComments`, pre-commit hook | Checker tests, with bite proof (R) | CI required check | Implemented | PR #6 |
| REQ-007 | Evidence for every claim: command with real output, CI link or screenshot | Owner, PROMPT-001 | O | PR body has a filled `## Evidence` section | Spec §7.5, CONTRIBUTING | PR template; `:checkPullRequest` | PR body check | Required check `pr-rules` | Implemented | PR #10, CONTRIBUTING.md |
| REQ-008 | Enforced solo workflow: issue per change, worktree per branch, conventional commits, squash merge, protected `main`, required checks | Owner | O | Spec §7.1 and §7.2 | DEC-008, DEC-011, DEC-028, ADR-0007 | `.githooks`, rulesets, `scripts/github/apply-repo-settings.sh`; required checks `checks`, `pr-rules`, `analyze (java-kotlin)`, `analyze (actions)` | Hook tests; PRs merged through the gates | Merged PRs #2 to #43 | Implemented | CLAUDE.md, ADR-0007 |
| REQ-009 | Accessibility: TalkBack labels, 48 dp targets, WCAG 2.2 AA contrast, 200% font scale, reduced motion, colour-blind safe, keyboard navigation | Owner (best practice); specifics are calls | O | Spec §3 list, "verified by tests" | Spec §3 | None | None yet | None | Not started | spec §3 |
| REQ-010 | 100% line and branch coverage on `:domain` and `:data` | Owner | O | `koverVerify` in CI and pre-push | Spec §7.2 | Kover gate configured | Proven on `Direction` with TDD red/green (R from session note) | CI | Implemented as a gate; coverage target applies as code arrives | PR #4 |
| REQ-011 | Release: signed APK on GitHub Releases through release-please, App token, CI-only signing, provenance; Play later | Owner | O | Spec §8 | ADR-0005, DEC-019, DEC-030 | Docs and settings only (PR #32); workflow in Plan 4 | None | None | Prospective (Plan 4) | spec §8 |
| REQ-012 | A skill factory: CI, hooks and SDLC rules extracted to a separate public plugin so the next project starts with them | Owner, S1 purpose | O | "A new project can install the plugin and get the same CI, hooks and rules on day one" (spec §1) | DEC-020 | Plugin v0.1.0, v0.1.1, v0.2.0 | Plugin CI; spec calls for proof on a fresh project (Plan 1 Task 10; result not re-verified here) | Plugin pinned and installed in SuperSnake | Implemented | plugin PRs #2, #4, #6, #8 |
| REQ-013 | Lifecycle documentation: an automatic, evidence-based, privacy-safe record of how the project is actually built, usable as presentation source | Owner, 2026-10-27 (R) | R | Capture per session with filters; fold-in before PRs; `scan.sh` hits fixed; Definition of Done in CLAUDE.md | DEC-023 to DEC-027 | Plugin 0.2.0 `adlc-docs` skill, agent and hooks; this folder | 88 plugin tests, shellcheck, CI, real-CLI smoke test (R from plugin PR #8); first fold-in is itself the first real use | Pending validation of the first fold-in (scan result) | Implemented in plugin; adoption in progress | plugin PR #8, issue #44 |
| REQ-014 | A plan must be executable by a fresh session with no conversation context | Owner, PROMPT-004 | O | Cold-read agent can find where to resume | DEC-013 | Plan 1 "Start here"; CLAUDE.md steps; README | Cold-read subagent (S1) | Gaps found and fixed; Plan 1 later executed by a fresh session (PROMPT-006) | Implemented | Plan 1 |

## Requirement Details

### REQ-006 - No comments (evolution)

Initial wording: "no extra comments in the code, why and what comments should always be in commit messages, never in code" (PROMPT-001, 2026-10-20). Current: no comments in any listed code or config file type, with no exceptions, and generated files stripped. The approved spec states it with no exceptions and as syntax-aware (strings and URLs never count, shebang lines are not comments). The prompt said "no extra comments"; when and why the wording became absolute is Unknown, because the intermediate spec text was not captured.

### REQ-004 - Versions (evolution)

Initial: latest stable, verified on the day. Evolved on 2026-10-22 to "latest stable that CodeQL supports" after the external review found Kotlin 2.4.21 ahead of CodeQL 2.4.20 (DEC-006), then on 2026-10-26 to disable Lint's NewerVersion check (DEC-017). Validation: the Dependabot Kotlin PR failing is the gate working.

### REQ-008 - Merge method (evolution)

Rebase only (2026-10-22) to squash only (2026-10-26), see DEC-007 and DEC-008.

### REQ-013 - Added after Plan 1

Not in the original spec; it appears with the plugin spec of 2026-10-27 (Reconstructed from the plugin PR). The original spec's purpose list already included "education in AI-assisted SDLC", which is the origin of the need.

### Known traceability gaps

- Test-level links (named tests per rule) were not reconstructed. Plan 1 holds the per-task commands and expected outputs.
- No acceptance criteria exist for REQ-001 beyond the spec's rules tables, because Plan 2 is unwritten.
