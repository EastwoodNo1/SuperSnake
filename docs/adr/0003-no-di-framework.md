# ADR-0003: No dependency injection framework

Status: accepted 2026-10-22

## Context

The app has around a dozen objects to wire together. The project keeps third-party dependencies to a minimum (spec §6).

## Decision

No DI framework. Plain constructors and a small manual composition root.

## Consequences

- The wiring is easy to read and adds zero dependencies.
- Revisit only if the wiring grows past what fits on one screen.

Source: design spec §4.
