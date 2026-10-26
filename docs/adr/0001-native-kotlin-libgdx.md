# ADR-0001: Native Kotlin with libGDX

Status: accepted 2026-10-22

## Context

SuperSnake is an Android-only game. The owner wants a neon look with glow, particles and smooth motion on the board, while the code stays Kotlin and fits ktlint and Kover. Options considered: Compose Canvas only, libGDX with KTX, libGDX alone, Godot 4, SurfaceView or OpenGL by hand, Flutter, Kotlin Multiplatform.

- Godot was rejected: GDScript and C# skills do not carry over to the owner's Android work, it is weak on Android accessibility, and it does not fit ktlint and Kover.
- Flutter and Kotlin Multiplatform were rejected: they solve cross-platform problems the project does not have.
- Compose Canvas alone was the designer's first recommendation.

## Decision

Native Kotlin. libGDX renders the game board. Jetpack Compose and the SplashScreen API draw menus, HUD, overlays and the splash. libGDX is used alone, without KTX (ADR-0002).

## Consequences

- libGDX is the only third-party runtime dependency (spec §6).
- The board can get richer visuals than Compose Canvas would give.
- libGDX ships native `.so` files, so the release APK must pass the 16 KB page-size check (spec §7.2).
- Game rules live in a pure Kotlin `:domain` module, so they are testable without libGDX.

Source: design spec §4.
