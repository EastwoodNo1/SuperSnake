# ADR-0008: DataStore core with our own serializers in a pure JVM `:data`

Status: accepted 2026-10-28

## Context

Main spec §12 left the kind of the storage module open. `datastore-preferences` needs okio, and the project keeps third-party dependencies to a minimum (spec §6).

## Decision

Owner decision, 2026-10-28 (engine feel and storage design §3): `:data` is a pure Kotlin JVM module that implements the settings and high-score repositories on `androidx.datastore:datastore-core` 1.3.0 with our own text serializers. No okio and no other third-party code enters the build.

Evidence, read on 2026-10-28:

- `datastore-core-jvm-1.3.0.module` lists only `androidx.annotation:annotation`, `kotlin-stdlib` and `kotlinx-coroutines-core` as runtime dependencies.
- `:data:dependencies --configuration runtimeClasspath` resolves only groups `androidx.*` and `org.jetbrains.*`, and a smoke test writes settings through `Stores.settings` and reads them back from a second store on the same file.

## Consequences

- No third-party runtime code: the added groups are `androidx.datastore` and `androidx.annotation`.
- Our serializers sit under the 100% line and branch gate, and they tolerate any bytes, so no corruption handler is needed.
- The `Difficulty` and `ControlScheme` enum names are now a stored format, so renaming one resets that stored value to its default.
- Plan 3 places the two files, `settings.txt` and `high_scores.txt`, in `filesDir/datastore/` and names them in the backup rules.

Source: engine feel and storage design §3.
