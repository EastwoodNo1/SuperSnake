# Source and Uncertainty Register

## Source Inventory

| Source ID | Source / Artifact | Availability | Period / Stage Covered | Information Provided | Reliability | Limitations |
|---|---|---|---|---|---|---|
| SRC-001 | Git history of `EastwoodNo1/SuperSnake` (`git log`, 35 commits on this branch) | Available | 2026-10-22 to 2026-10-27 | Commit subjects, dates, squash-merge PR numbers | High | Earliest commit is the approved design on 2026-10-22. Nothing earlier in git. Commit bodies not all read |
| SRC-002 | Merged and closed PRs and issues in SuperSnake (`gh`, read-only) | Available | 2026-10-26 to 2026-10-27 | Titles, open and merge times, which PRs were closed unmerged | High for titles and times | PR bodies, review comments and CI logs not read |
| SRC-003 | Plugin repo `android-sdlc-skills`: PR list, PR #8 body, commit list and file list | Available | 2026-10-26 to 2026-10-27 | v0.1.0 to v0.2.0 history, detailed rework in PR #8 | High for PR #8 | Plugin spec and plan not read (the API call was refused in this session); PR #2, #4 and #6 bodies not read |
| SRC-004 | Design spec `docs/superpowers/specs/2026-10-22-supersnake-design.md` | Available | 2026-10-22 to 26 | Requirements, owner/call tags, rules | High | Read to §8; §9 onward not read |
| SRC-005 | Plan 1 `docs/superpowers/plans/2026-10-22-plan-1-foundation.md` | Available | 2026-10-22 to 26 | Tasks 1 to 10, "Start here", state at handoff | High | Header and handoff read, task bodies not read |
| SRC-006 | ADRs 0001 to 0007 | Available | 2026-10-22 and 26 | Decisions with context | High | Condensed from spec |
| SRC-007 | `CLAUDE.md`, `CONTRIBUTING.md` (section titles) | Available | Current | Standing rules, how the repo enforces them | High | CONTRIBUTING body not read |
| SRC-008 | Staged session notes: 37cc1e57, b283293d, 9666aa57, 68b8cc9f | Available, folded | 2026-10-20 to 26 | Prompts, chronology, rationale, failures | Medium: model-written from a size-capped digest | Middle parts of digests were omitted; commit SHAs and PR numbers were not verified at capture |
| SRC-009 | Raw Claude Code transcripts | Not read | all | none | n/a | Only digests were available through capture |
| SRC-010 | Session that built the ADLC feature (2026-10-27) | Not captured | 2026-10-27 | none directly | n/a | Reconstructed from SRC-003 |
| SRC-011 | The git-ignored subagent ledger (`.superpowers/sdd/`) | Not available to this fold-in | 2026-10-23 to 26 | Briefs, rulings | n/a | Not committed |

## Evidence Classification

Use:

- **Observed** - directly supported by available evidence
- **Reconstructed** - reasonably inferred from available evidence
- **Unknown** - cannot be reliably determined
- **Prospective** - introduced from the current point onward

When necessary state:

> Not fully reconstructable from the available project history.

## Historical Gaps

| Gap ID | Missing / Uncertain Information | Affected Period / Cycle | Impact | Possible Source | Status |
|---|---|---|---|---|---|
| GAP-001 | The first project day, 2026-10-20, before the first commit: only a model-selection session and an unrelated no-op session are captured | CYCLE-001 | Early requirement steps are known only from the session note | none likely | Open; Not fully reconstructable |
| GAP-002 | Intermediate spec drafts and the full list of 17 external-review findings | CYCLE-002 | Only the blocker and counts are known | git history before `e900708` does not exist | Open |
| GAP-003 | Which PR belonged to which Plan 1 task is inferred from titles; review findings per task are mostly unseen | CYCLE-004 | Timeline rows 9 to 16 are Observed for PR times, Reconstructed for task mapping | Plan 1 task checkboxes, PR bodies | Open |
| GAP-004 | Prompts and design conversation for the ADLC feature | CYCLE-005 | Cycle is Reconstructed only | None (session not captured) | Open |
| GAP-005 | Why Kotlin bumps were split (DEC-022) | CYCLE-004 | Rationale Unknown | PR #40 and #38 bodies | Open |
| GAP-006 | Plugin spec's "Changes during implementation" section and plan spike results | CYCLE-005 | Rework list comes from the PR body and commits only | plugin repo docs | Open; read in the next fold-in |
| GAP-007 | CI and review evidence per PR (not re-read) | CYCLE-004 | Validation rows say "merged", not "proof shown" | PR bodies, Actions runs | Open |
| GAP-008 | Whether the rebuilt starter skeleton commit and the early prerequisite commits were reviewed | CYCLE-003 and 004 | Direct commits to `main` before rulesets existed (10-23) had no PR | none | Open |

## Conflicting Evidence

| Conflict ID | Topic | Evidence A | Evidence B | Current Interpretation | Confidence | Resolution Needed |
|---|---|---|---|---|---|---|
| CONF-001 | Merge method | Session note S1 and spec history: rebase only | CLAUDE.md and ADR-0007: squash only | Changed on 2026-10-26 (DEC-007, DEC-008); session records stay as history | High | None |
| CONF-002 | Plugin "PR 43" | S2 note says "PR 43" without a repo, next to v0.1.1 | SuperSnake PR #43 pins v0.1.1; plugin PR #6 released v0.1.1 | Both exist: plugin PR #6 made the release, SuperSnake PR #43 pinned it | High | None |
| CONF-003 | Dependabot #28 "left open" | S2 note: left open as a reminder | GitHub: closed unmerged 2026-10-26 15:06 | Closed later in the same session after the split in PR #40 (DEC-021, DEC-022) | Medium | Check PR #40 body |
| CONF-004 | Plan 1 "complete" (PR #39, 14:56) | Plan "State at handoff" lists PRs #2 to #36 | PRs #39 to #43 followed the same day | Plan 1 was declared complete, then small follow-ups landed | High | None |
| CONF-005 | Plan "State at handoff" says Tasks 1 to 10 are merged as PRs #2 to #36 | PRs #23 ("update stuff") and #28 (Dependabot) in that range were closed unmerged | Task PRs in the range did merge | "PRs #2 to #36" is a range, not a list of merged PRs. Purpose of #23 is Unknown | Medium | Check PR #23 |

## Reconstruction Status

- Overall status: **Complete for what the accessible history supports; one gap family remains (GAP-003, 004, 006, 007)**
- Earliest reliably reconstructed point: 2026-10-20 (session S1 note); first commit 2026-10-22 (`e900708`)
- Latest reconstructed point: 2026-10-27, plugin PR #8 merged 15:17 UTC and the SuperSnake adoption commit `ae6c44d`
- Major unresolved gaps: GAP-001 (day one), GAP-004 (ADLC session prompts), GAP-006, GAP-007
- New historical evidence to inspect: plugin spec and plan (read in the next fold-in, with `gh` access to file contents), SuperSnake PR bodies for #8, #32, #40, plugin PR #6

### Notes on this fold-in

- The four staged notes were captured automatically; the fifth record (2026-10-27) was reconstructed by the documenter.
- Privacy edits applied to copied notes: the name of the cited book's author was replaced by a description of the book. The session records otherwise keep the staged wording.
- The commit hashes quoted in the S1 note (c51cd73, 380cda8, b127e67, ad75d14, c41eca1, 179a06f, b32fb27, 730e1e7) were checked against `git log` and exist, so that gap in the note is closed (Observed).
- The `git log` count and PR list were read in this fold-in. Evidence from the plugin repo was limited to the PR list and PR #8.

## Second fold-in additions (2026-10-28)

| Gap ID | Gap | Cycle | Impact | Source | Status |
|---|---|---|---|---|---|
| GAP-009 | The digest of session dfda0bef was truncated in the middle (Plan 2 Tasks 5 to 7); the third storage option and several agent calls are cut off | CYCLE-007 | Individual findings of those tasks unknown | PR bodies, git history | Open |
| GAP-010 | Subagent reports and the ledger were deleted at the end of the Plan 2 session | CYCLE-007 | Only git and PR bodies remain | none | Open |
| GAP-011 | Root cause of the merge-wait failures on Tasks 6 and 7 | CYCLE-007 | Unknown | none | Open |
| GAP-012 | The Plan 3 planning session is a reconstruction from the main agent's report and the committed plan; no digest read | CYCLE-008 | No verbatim prompts; research reports unread | Plan, ADR-0009, ADR-0010 | Open until its staged note is folded |
| GAP-013 | Nothing reliable found on Google or Nokia Snake tail-cell and grace behaviour | CYCLE-007 | Rules rest on owner rulings | none | Open |

Sources added: SRC-007 Plan 2, its design doc and ADR-0008; SRC-008 Plan 3 and ADR-0009, ADR-0010; SRC-009 the staged note of session dfda0bef.

## Third fold-in additions (2026-10-28)

| Gap ID | Gap | Cycle | Impact | Source | Status |
|---|---|---|---|---|---|
| GAP-012 update | The observed note of the Plan 3 planning session is now folded (SRC-010); verbatim prompts exist. Research report bodies remain unread | CYCLE-008 | Numbers (libGDX 1.14.2, 16 KB, activity counts) still come from main-agent summaries | session record | Partly closed |
| GAP-014 | The ADLC feature session now has an observed note next to the earlier reconstructed record; the two were not reconciled line by line. Commit SHAs and counts in the note come from AI summaries | CYCLE-005, CYCLE-006 | Minor | both records | Open |
| GAP-015 | The AGP 9.5.0 session has no staged note yet; the trigger and the owner's choice are taken from the main agent's context and git | CYCLE-009 | Rationale and alternatives thin | `5d55438`, `c3fe5e5` | Open until its note is folded |

Sources added: SRC-010 staged note of session 35559d8c; SRC-011 staged note of session ea322ed1.

### Added in the fourth fold-in (2026-11-04)

| Gap ID | Gap | Cycle | Impact | Source | Status |
|---|---|---|---|---|---|
| GAP-015 update | The AGP session is covered in the observed note of session c5ff6184 (SRC-012), but at summary level only | CYCLE-009 | Rationale still thin | SRC-012 | Partly closed |
| GAP-016 | The digest of session c5ff6184 was size-capped; Tasks 6 to 9 and many subagent prompts are Reconstructed from compaction summaries; merge hashes not verified | CYCLE-010 | Moderate | SRC-012 | Open |
| GAP-017 | The Plan 4 review report and the second re-review were not read by the documenter; findings and verdicts come from the main agent's context (re-review verdict: ready for Task 4). The reviewer's tag-object SHA was corrected to commit 8da46b53 by the main session | CYCLE-011 | Counts (4 critical, 8 important) unverified here | SRC-013 | Open |
| GAP-018 | Task 4 of Plan 4 and the actual effect of the fixes are Prospective | CYCLE-011 | Unknown until Task 4 runs | plan | Open |

Sources added: SRC-012 staged note of session c5ff6184 (Observed for human messages and two compaction summaries); SRC-013 main-agent context for the Plan 4 review (issue #115), Observed as reported.

## Update (fifth fold-in, 2026-11-05)

| Gap ID | Gap | Cycle | Impact | Source | Status |
|---|---|---|---|---|---|
| GAP-019 | The release-smoke resume fix (issue #117) has no staged note; it rests on the main agent's context. Run IDs, the RED and GREEN outputs and the exact quote "PR 116 isn't green" were not read from primary evidence | CYCLE-012 | Moderate | SRC-014 | Open |
| GAP-020 | Whether the merge block was a safety classifier decision or a permission setting is as reported by the main agent; not verified | CYCLE-012 | Low | SRC-014 | Open |
| GAP-021 | CI result of the #117 fix is Prospective at fold-in time | CYCLE-012 | Unknown until the PR runs | issue #117 | Open |

Sources added: SRC-014 main-agent context for issue #117 (Observed as reported); SRC-015 staged note of session 3c11e9a7 (trivial, plugin commands only; purpose Unknown).

## Update (sixth fold-in, 2026-11-13)

| Gap ID | Gap | Cycle | Impact | Source | Status |
|---|---|---|---|---|---|
| GAP-022 | Task 4 outcome unknown: `--check` and apply runs, secret upload and the PR depend on the owner; the implementer and reviewer outputs are known only from summaries | CYCLE-013 | High | SRC-016 | Open |
| GAP-023 | Whether a newer cmdline-tools fixes the `target` was never isolated; Unknown | CYCLE-014 | Low | SRC-017 | Open |
| GAP-024 | PR 2 for #82 (required checks, docs) is Prospective; the final report of the session is not in the digest | CYCLE-014 | Moderate | SRC-017 | Open |
| GAP-025 | The #126 fix (runs, RED 2 failed, GREEN 19 passed, the coordinator's cmux watcher and cross-session messages) rests on the coordinator's report, not primary evidence; CI result Pending | CYCLE-012 | Moderate | SRC-018 | Open |
| GAP-026 | `release-smoke` flakiness root cause beyond the SIM dialog is Unknown; GAP-021 is superseded by the second timeout (issue #126) | CYCLE-012 | Moderate | SRC-017, SRC-018 | Open |

Sources added: SRC-016 staged note of session 154bc22a (digest-based; no PR or CI output inspected); SRC-017 staged note of session 3e97d016 (Observed run IDs, PR numbers); SRC-018 coordinator report for issue #126 (Observed as reported). Privacy: the exposed key content and App identifiers are deliberately not recorded.

## Update (final fold-in, 2026-11-20)

| Source ID | Source | Availability | Covers | Reliability | Limitations |
|---|---|---|---|---|---|
| SRC-019 | Re-captured note of session 35559d8c (orchestrator) | Available | 2026-10-28 to 2026-11-20 | Observed for named PRs and issues; middle Reconstructed | Digest omits most AI text; one session resumed several times |
| SRC-020 | Re-captured note of session 154bc22a (Plan 4 executor) | Available | 2026-11-06 to 2026-11-19 | Observed at start and end; middle from compaction summaries | Digest size-capped |
| SRC-021 | `git log` and `gh pr list --state merged` on 2026-11-20 | Available | PRs #123 to #182 | High for titles, dates, numbers | Review comments not read |
| SRC-022 | PR bodies #156, #162 to #167, #169, #173, #175, #176, #179, #180, #182 | Available | Finish-up decisions and evidence | High as statements; evidence blocks not re-run | Quoted partly |
| SRC-023 | Issues #151 to #153, #168, #170, #181, #97 | Available | Playtest defects and CI flakes | High | Issue state is as of 2026-11-20 |
| SRC-024 | Plan 5 and Plan 4 | Available | State at handoff, owner-veto list, Task 8 | High | Written by the main session |
| SRC-025 | Release list and run 44709700143 (`gh`) | Available | v1.0.0 (2026-11-18), v1.0.1 (2026-11-20) | High for the five job conclusions | Attestation and approval are from the main session's report |

| Gap ID | Gap | Cycle | Impact | Source | Status |
|---|---|---|---|---|---|
| GAP-027 | Capture gap: a session id already in `captured` is never captured again (android-sdlc-skills#13), so nothing after 2026-11-09 was captured until this re-capture. The remedy (remove the id, re-capture) recovers digests, not the full conversation | CYCLE-019 | High | SRC-019 | Open until the plugin fix; recorded |
| GAP-028 | The owner's crash in #152 was never reproduced. #167 fixed the likeliest causes | CYCLE-016 | Moderate | SRC-022, SRC-023 | Open |
| GAP-029 | Owner outputs for the settings script and the play check on 1.0.1 are not recorded (Prospective) | CYCLE-019 | Moderate | SRC-024 | Open |
| GAP-030 | Two machine restarts and the lost local state: only one power-down is evidenced in the note, plus the unpushed Plan 4 close-out commit in Plan 5. The count and the full loss are Unknown | CYCLE-017 | Low | SRC-019, SRC-024 | Open |
| GAP-031 | Subagent outputs, review rounds and the exact sequence of the finish-up are not in the digests; only PR bodies and the plan | CYCLE-017 | Moderate | SRC-019 | Open |
| GAP-032 | Whether the key exposed in the Plan 4 executor session was rotated: the executor note says the owner rotated it; the orchestrator note says it is unknown. No key material was recorded. Treated as rotated, not verified | CYCLE-013 | Moderate | SRC-019, SRC-020 | Open |
| GAP-033 | #181: emulator flakes are not root-caused; the `codeql.yml` cache lead is untested | CYCLE-018 | Moderate | SRC-023 | Open at project end |
| GAP-034 | Which model wrote which commit: both Opus and Sonnet attribution lines appear in the digests | all | Low | SRC-019 | Open |
| GAP-035 | Plugin 0.4.1 does not install locally until the owner aligns the user-settings marketplace entry (Prospective, reported by the main session) | CYCLE-019 | Low | SRC-024 | Open |
| GAP-036 | The executor note names #136 as another session's work; git shows #136 as the 1.0.0 release PR. Git is taken as correct. PR #143 (closed unmerged) was not re-read | CYCLE-015 | Low | SRC-020, SRC-021 | Open |

Privacy: the key, the GitHub App identifiers and local paths are deliberately not recorded.

## Update (eighth fold-in, 2026-11-20)

| Source ID | Source | Availability | Covers | Reliability | Limitations |
|---|---|---|---|---|---|
| SRC-026 | Staged note of session f877dcd1 (`sessions/2026-11-20-finishing-session-1-0-2.md`) | Available | 2026-11-20, #184 to #196 | Reconstructed from a digest; PR numbers, titles and merge times confirmed in git and `gh` | Command output not re-verified; partly truncated; captured before the session ended |
| SRC-027 | `git log`, `gh pr view` and `gh issue view` for #181, #183 to #197 on 2026-11-20 | Available | States and merge times | High | #187, #191, #193 and #197 are issues, not PRs (the PR lookup fails by design) |
| SRC-028 | Release list and plugin tag v0.4.2 (`gh`) | Available | v1.0.2 Latest (19:54 UTC); v0.4.2 at commit `04ca3ae` | High | Attestation, shasum and `versionCode` come from the session note |
| SRC-029 | Body of PR #195 | Available | The 20 runs (4 failed) and the chance estimate | High | The 30 passing runs are quoted from the PR body, not re-run |

| Gap ID | Gap | Cycle | Impact | Source | Status |
|---|---|---|---|---|---|
| GAP-037 | The #186 DataStore race was never reproduced locally (0 of 25); the fix follows the `StoresTest` pattern | CYCLE-020 | Low | SRC-026 | Open |
| GAP-038 | Root cause of the #196 phone-31 `LayoutScreenshotTest` failure (app never idle after rotating on the menu) is Unknown. The failed `checks` and `release-smoke` entries in the #196 check rollup are explained (Observed): they come from CI run 44804068027 at 19:38 UTC on `b9f9f0e`, a release-please re-push of its branch after #196 had merged at 19:38:21 UTC. Both failed resolving the Kotlin Gradle plugin (`Could not parse POM https://plugins.gradle.org/...kotlin-gradle-plugin-2.4.20.pom`), see GAP-043 | CYCLE-024 | Moderate | SRC-026, SRC-027 | Open |
| GAP-043 | `main` CI was red after the session's last merges, which the session did not check at the time (it checked each PR's CI only). Runs 44772006023 (`4c62c80`, #192) and 44774492909 (`0fb0056`, #194) failed in `checks` resolving `org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.20`; 44784171262 (`28df360`, #195) failed in `ui-tests (tablet-37)` configuring `:checks`; 44804016440 (`3ce133a`, release 1.0.2) was cancelled about 30 s in. Cause (Observed in `gh cache list`): #192 changed the Gradle cache key; a failing `checks` run saved an incomplete 129 MiB entry (10857033823) that the basic cache provider never overwrites, so runs restore it and fetch the Kotlin plugin from the network. The same pattern was cleared by hand earlier on 2026-11-20 (Plan 5 Task 1 Step 2). The release artifact itself was built, smoke-tested, signed and verified by the Release run 44804016463 | CYCLE-024 | High | SRC-026 | Open |
| GAP-039 | The #195 fix has not been tested on a real device | CYCLE-023 | Moderate | SRC-026 | Open |
| GAP-040 | The owner's local `phone37` emulator lost the installed app after a headless boot (snapshot missing); the cause is Unknown and may be this session's boot | CYCLE-024 | Low | SRC-026 | Open |
| GAP-041 | Work after the capture is not recorded because the session id is in `captured` (plugin issue #13, which is CLOSED in the plugin repo as of 2026-11-20, O; whether the fix is released is Unknown) | CYCLE-024 | Moderate | SRC-026, SRC-028 | Open |
| GAP-042 | The playtest of 1.0.2 is reported as "works well", not as command output; the eight-item plugin handover is not committed anywhere, so it cannot be inspected | CYCLE-024 | Low | SRC-026 | Open |

Updates to earlier gaps: GAP-033 (#181 root cause) is resolved in part: four separate causes were found and fixed (DEC-087, DEC-088), and #181 is CLOSED (O). GAP-032 (key rotation) remains unanswered in this session.
