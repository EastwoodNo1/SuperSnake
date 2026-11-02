# ADR-0012: Release workflow with an approval-gated signing job

Status: accepted 2026-11-02 (owner)

## Context

Spec section 7.2 left one design point open since Plan 1: the `release` environment was to be limited to `v*` tags, while section 8 ran the release job in the release-please run on `main`, which that limit blocks. The choice was a release job on `main` with the environment allowing `main`, or a second workflow triggered by the tag. Signing needs the keystore, so the question is where the keystore secrets become reachable and who has to agree first.

## Decision

One workflow, `release.yml`, runs on pushes to `main` with five jobs:

1. `release-please`: mints an App token and runs release-please. It outputs `release_created`, `tag_name`, `version` and `sha`. Its credentials are in the `release-please` environment (no reviewer).
2. `build`: no secrets. Checks the tag is on `main` and agrees with `version.txt`, runs the checks and builds the unsigned release APK with R8.
3. `smoke`: runs `scripts/release-smoke.sh` on an emulator against the unsigned build.
4. `sign-publish`: the only job in the `release` environment. Decodes the keystore into `$RUNNER_TEMP`, signs with `apksigner` (passwords by `env:`), checks the certificate against `RELEASE_CERT_SHA256` (failing closed), writes the checksum and attestation, uploads both assets to the draft release and publishes it.
5. `verify-published`: re-checks the published release.

Settings that go with it:

- Environment `release`: deployment branches `main` only, the owner as required reviewer, "prevent self-review" off (the repo is solo), admins cannot bypass.
- Environment `release-please`: `main` only, no reviewer. It holds `RELEASE_APP_PRIVATE_KEY`; the App's client ID is the variable `RELEASE_APP_CLIENT_ID`. This replaces spec section 8's "the App's ID and private key live in the same environment": release-please runs on every push to `main`, so a reviewer on that environment would ask for approval on every merge.
- Tag ruleset `release-tags` on `refs/tags/v*`: creation, update, deletion and non-fast-forward are blocked for everyone except the release App, the only bypass actor.
- Immutable releases are enabled before the first release (existing releases stay mutable). The documented flow of draft, then attach assets, then publish, locks the tag and the assets at publish.
- The App token has Contents and Pull requests write only, and no `repositories` input, so it is limited to this repository.
- The build and the signing are split: Gradle never runs where the key is reachable.

## Alternatives rejected

- **A tag-triggered second workflow** (release-please on `main`, signing on the tag it creates). The App key must be reachable from `main` anyway; when the tag is not created or does not trigger, nothing runs and nothing says so (a silent failure); and the whole path cannot be proven by a PR.
- **The keystore outside GitHub** (the Signal and NewPipe pattern). The strongest key protection, but it breaks ADR-0005's "only CI can produce a signed build" and removes the attestation from the signed APK. It stays the fallback if the owner stops trusting a keystore in GitHub secrets.
- **AGP injected signing** (`android.injected.signing.*`). Undocumented, and the passwords would sit on the Gradle command line. `apksigner` with `--ks-pass env:` keeps them off it.
- **`workflow_dispatch` with a reviewer** (the Thunderbird pattern). With release-please, merging the release PR is already the deliberate act; the environment reviewer is the second click.

## Evidence

Read 2026-10-29 and 2026-11-02; the full notes are in the Plan 4 design record.

- "You can use required reviewers to protect environment secrets. A workflow job cannot access environment secrets until approval is granted by a reviewer." (GitHub docs, "Secure use reference": https://docs.github.com/en/actions/reference/security/secure-use). A job that names `release` gets nothing until the owner approves.
- "Once an immutable release is published, its associated Git tag is locked to a specific commit, cannot be changed, and cannot be deleted while the release exists." (https://docs.github.com/en/code-security/concepts/supply-chain-security/immutable-releases). The same page says that after deleting such a release "you cannot reuse the same tag name", which is why a failure after publish ships as the next patch version. The lock starts at publish, so a draft's tag is protected only by the ruleset.
- "Such secret material MUST NOT be accessible to the environment running the user-defined build steps." (SLSA build requirements, Build L3: https://slsa.dev/spec/v1.2/build-requirements; the lines are about the platform's provenance key, so the transferable idea, not the letter, is what applies to an app-signing key). The split of `build` from `sign-publish` follows this idea.
- "Artifact attestations by itself provides SLSA v1.0 Build Level 2." (GitHub docs, artifact attestations: https://docs.github.com/en/actions/concepts/security/artifact-attestations). Build L3 would need a reusable workflow; this project claims L2.
- Thunderbird for Android builds unsigned in one job and signs in a separate job in a per-product environment (https://github.com/thunderbird/thunderbird-android, `.github/workflows/shippable_builds.yml`). Signal-Android's `build-artifacts.yml` builds unsigned APKs with "No credentials ... available here" (the research's reading: its build workflow has no credentials and signing happens elsewhere; https://github.com/signalapp/Signal-Android). Of the four projects compared (Thunderbird, Tusky, Signal and NewPipe), none signs in the job that runs Gradle.

## Consequences

- The reviewer is a click by the same person: self-review is allowed because the repo is solo. The gate stops an accidental or hijacked run from signing, not the owner.
- The tag ruleset has one bypass actor, so it differs from `main-protection`, whose bypass list is empty.
- Evaluate mode is unavailable for rulesets on a personal repo, so the ruleset is tested by a real tag-creation attempt and a proof workflow.
- A failure before the release is published is re-run in place, even though the tag already exists as a draft. Only a failure after the release is published is fixed forward with the next patch version, because that version number is burned. Tags are never re-pointed.
- `force-tag-creation` creates the tag when the draft is made, before publish, so until publish only the ruleset protects it; release-please ignores an HTTP 422 on the tag, so `check-release-ref.sh` fails the run when the tag is missing.
- A draft left behind by a failed run is visible in `gh release list`; `docs/release.md` is the runbook.
