# ADR-0009: Our own back stack instead of a navigation library

Status: accepted 2026-10-28

## Context

The app has four screens: menu, game, high scores and settings. Main spec §11 recorded `navigation-compose` as a verified version, but no decision chose it. The project bans kotlinx-serialization (Plan 2 Global Constraints) and keeps dependencies to a minimum (spec §6).

## Decision

Owner decision, 2026-10-28 (Plan 3, "Decided for Plan 3"): screens are navigated with a small `BackStack` in `:domain` and `BackHandler` from `activity-compose`, which is already a dependency. Neither navigation-compose nor navigation3 is used.

Evidence, read on 2026-10-28:

- `navigation-common` 2.10.2, which `navigation-compose` 2.10.2 depends on, lists `org.jetbrains.kotlinx:kotlinx-serialization-core` 1.7.3 at runtime scope in its POM and `.module` file.
- `navigation3-runtime` 1.2.0 lists the same library at runtime scope, and `navigation3-ui` 1.2.0 depends on it.

## Consequences

- No navigation library and no kotlinx-serialization in the app.
- The back-stack rules (no duplicate top, home, pop at the root) sit under the 100% gate in `:domain`.
- No deep links or transition animations come for free. The app needs neither.
