# ADR-0011: versionCode derived from version.txt

Status: accepted 2026-11-02

## Context

Google Play needs a `versionCode` that is a positive integer, strictly increasing, with a maximum of 2,100,000,000. Releases are cut from SemVer, so the code must follow the version without a second number to keep in step.

## Decision

`version.txt` at the repo root holds the SemVer (`MAJOR.MINOR.PATCH`, digits, no leading zeros). `versionName` is its trimmed text. `versionCode` is `VersionCode.of(text)` in `build-logic/checks`, under the 100% gate:

`versionCode = major*1_000_000 + minor*1_000 + patch`

- Limits: major at most 2099, minor and patch at most 999. The highest code is 2,099,999,999. A larger field or a malformed version fails the build with a message naming the version and the field (`major`, `minor`, `patch` or `format`).
- `0.0.0` maps to 1, the owner decision of 2026-11-02. The unreleased placeholder keeps the code every earlier build had, so a development build still installs over them. Every real version uses the formula.
- Wiring: the root `build.gradle.kts` declares `buildscript { dependencies { classpath("sdlc:checks") } }`, reads `version.txt` with `providers.fileContents` (so the configuration cache tracks it), computes `VersionCode.of` and exposes `extra["versionName"]` and `extra["versionCode"]`. `:app` reads them from `rootProject.extra`. The included build `build-logic/checks` substitutes `sdlc:checks`, so no second included build and no Gradle plugin were needed. The classpath sits in the root build because Android Lint's `UseTomlInstead` flags it in `:app`, and the check stays enabled.

Options considered:

- `major*10000 + minor*100 + patch`: caps minor and patch at 99, too tight for a long-lived 1.x line.
- Commit count: not derivable from the version, differs between clones and shallow checkouts.
- Date-based: two releases on one day collide, and the code says nothing about the version.

## Consequences

- One edit to `version.txt` changes both fields, and the release tooling only has to bump that file.
- A bump that would lower or overflow the code cannot ship, because the build fails first.
- Reaching minor 1000 needs a new decision.
