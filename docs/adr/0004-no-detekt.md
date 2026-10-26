# ADR-0004: No detekt

Status: accepted 2026-10-22

## Context

The latest stable detekt (1.23.8, February 2025) predates Kotlin 2.4. detekt 2.x is alpha only (`dev.detekt` 2.0.0-alpha.6, 2026-08-04). Keeping detekt would mean breaking either "latest stable Kotlin" or "stable only".

## Decision

The owner chose to drop detekt:

- Formatting and style move to ktlint 1.8.0.
- Correctness checks come from Android Lint and the Kotlin compiler, with warnings treated as errors.
- Our own check enforces the no-comments rule.

## Consequences

- Fewer third-party build tools.
- Static analysis is narrower than detekt's rule set.
- Revisit when detekt 2.x has a stable release.
- The first build task had to prove ktlint 1.8.0 runs cleanly on Kotlin 2.4.20 sources, with evidence.

Source: design spec §11.
