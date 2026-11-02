# ADR-0005: One signing key for the app's whole life

Status: accepted 2026-10-22

## Context

Releases are signed APKs on GitHub Releases now, and Play publishing comes later. Android requires updates to be signed with the same key.

## Decision

- The release key created now is the app-signing key for the app's whole life.
- When Play publishing starts, this key is uploaded to Play App Signing, and a separate upload key is created for uploading to Play.
- The key is stored in GitHub Actions secrets that only the `release` environment can access.
- The owner holds an offline encrypted backup.
- The `release` environment requires the owner's approval and allows only `main` (ADR-0012).

## Consequences

- Players who installed from GitHub can update from Play.
- Losing the key means no more updates to installed copies. `docs/release.md` documents creation, backup, recovery, the Play App Signing migration steps and what losing the key means.
- Only CI can produce a signed build. No signing material exists in the repo or the working tree.

Source: design spec §8.
