# ADR-0006: Kotlin version capped by CodeQL support

Status: accepted 2026-10-22

## Context

The project uses the latest stable Kotlin, but CodeQL must be able to analyse it. On 2026-10-22 CodeQL supported "Kotlin 1.8.0 to 2.4.20" (https://codeql.github.com/docs/codeql-overview/supported-languages-and-frameworks/). Kotlin 2.4.21 (2026-10-08) was already released.

## Decision

Kotlin may only be raised to a version that CodeQL's supported-languages page lists. The project stays on Kotlin 2.4.20 until CodeQL lists 2.4.21 or later. A Dependabot Kotlin bump beyond the cap stays open until CodeQL catches up.

## Consequences

- "Latest stable" for Kotlin means the latest stable that CodeQL supports. `docs/versions.md` records the deliberate gap.
- Lint's `NewerVersionAvailable` check is disabled (owner decision 2026-10-26). It flagged the deliberate Kotlin cap and depends on the network. Freshness comes from per-task version verification and Dependabot.

Source: design spec §11.
