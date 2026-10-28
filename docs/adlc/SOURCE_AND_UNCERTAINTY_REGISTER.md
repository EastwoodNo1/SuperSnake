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
