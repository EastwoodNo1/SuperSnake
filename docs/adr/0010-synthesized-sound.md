# ADR-0010: Synthesized sound effects

Status: accepted 2026-10-28

## Context

Main spec §3 has a sound setting and requires every asset to be made for the project or licensed CC0 / SIL OFL, recorded in `docs/assets.md`. The game's look follows the Nokia original, whose sounds were simple beeps.

## Decision

Owner decision, 2026-10-28 (Plan 3, "Decided for Plan 3"): sound effects are short square-wave beeps generated in code. `:domain` computes the PCM samples from a list of notes per sound, under the 100% gate. `:app` plays them with `android.media.AudioTrack`. The project ships no audio files.

Options considered: CC0 sound files played with `SoundPool`, and no sound in v1.

## Consequences

- No audio licences to track. `docs/assets.md` records that the sounds are generated.
- The sounds are deterministic and unit-tested: length, amplitude and frequency.
- The sound is deliberately simple. Richer sound would need a new decision.
