# ADR-0007: Squash merge only, no required commit signatures

Status: accepted 2026-10-26 (owner)

## Context

The original decision on 2026-10-22 was rebase merge only. On 2026-10-26 the owner changed it to squash merge, because squash keeps one commit per PR on `main` with all of the PR's commit messages in the body. The ruleset requires linear history either way.

## Decision

- Squash merge is the only allowed merge method.
- The PR title is a Conventional Commit header of at most 64 characters, because GitHub appends ` (#N)`. It becomes the subject on `main`.
- The commit messages of the PR become the body, keeping their Why, What and trailers.
- Signed commits are not required.

## Consequences

- Release integrity comes from the CI-only build, provenance attestation and immutable releases, not from commit signatures.
- GitHub lowercases `Co-authored-by` trailers and appends ` (#N)` to the subject.
- Stacked PRs merge with the same squash method (spec §11).

Source: design spec §11 (Merge method) and §9.
