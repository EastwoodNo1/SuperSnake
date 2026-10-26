# ADR-0002: No KTX

Status: accepted 2026-10-22

## Context

KTX is a set of Kotlin extensions for libGDX. The third-party dependency rule (spec §6) requires a large, very active community. Verified on 2026-10-22 via the GitHub API:

| Repo | Stars | Last commit | Latest release | Status |
|------|-------|-------------|----------------|--------|
| `libgdx/libgdx` | 25,465 | 2026-10-14 | 1.14.2 | healthy |
| `libktx/ktx` (original) | 1,470 | 2025-06-28 | 1.13.1-rc1 | stalled |
| `Quillraven/ktx` (community fork) | 3 | none | 1.14.2-rc2 | tiny |

## Decision

KTX is not used. libGDX's Java API is called directly from Kotlin.

## Consequences

- KTX fails the third-party rule, so it stays out of the build.
- Any convenience helpers are our own code and are covered by tests.

Source: design spec §4.
