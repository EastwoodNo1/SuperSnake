# Contributing

This is the how-to for humans. [`CLAUDE.md`](CLAUDE.md) gives AI agents the same rules in short form. Both follow the design spec, §7 ([`docs/superpowers/specs/2026-10-22-supersnake-design.md`](docs/superpowers/specs/2026-10-22-supersnake-design.md)). Rules are enforced by a machine. Practices are taught and are not gated.

## Set up

Follow the quick start in [`README.md`](README.md): install mise, point `ANDROID_HOME` (or `sdk.dir` in `local.properties`) at an Android SDK, run `scripts/bootstrap.sh`, then `~/.local/bin/mise exec -- ./gradlew check`.

## Flow for every change

1. **Issue.** Open a GitHub issue that says why.
2. **Worktree.** `git worktree add ../SuperSnake-<name> -b <type>/<name>`. Copy `local.properties` into it if you use one. The main checkout stays on `main` and is only for syncing.
3. **Test first.** Write a failing test before production code (practice).
4. **Small commits.** Conventional Commits. The why and what go in the commit body, never in code.
5. **Stack large work.** Split it into small PRs with `gh stack` (practice).
6. **Checks.** Let the hooks run. Run `scripts/gradle-strict.sh check` before you push.
7. **Pull request.** Title is a Conventional Commit header of at most 64 characters. The body fills the template: `Why`, `What`, `Closes #<issue>` and `Evidence`.
8. **Merge.** Squash merge only, once the required checks are green: `checks`, `pr-rules`, `analyze (java-kotlin)`, `analyze (actions)`. The branch must be up to date with `main`.

Commit format:

```
type(scope): subject

Why: the reason for the change.
What: what changed.

Co-Authored-By: <the model that wrote it, if an agent did>
```

A body is required for `feat`, `fix`, `refactor` and `perf`, and should be added to other types whenever there is a why to say.

## Rules and how each is enforced

Every rule in the table below is machine-enforced; the practices after it are taught, not gated. CI re-runs everything the hooks run, so `--no-verify` changes nothing about what can merge.

| Rule | Local (git hooks) | CI (required check) | GitHub setting |
|------|-------------------|---------------------|----------------|
| No direct commits or pushes to `main` | pre-commit and pre-push refuse on `main` | none | Ruleset on `main`: PR required, no bypass list, conversation resolution required |
| Work happens in a linked worktree | pre-commit refuses commits in the main checkout | none | none |
| Every PR links an issue | none | `pr-rules`: body contains `Closes #n`, or the PR has the `dependencies` label | none |
| Production changes come with tests | none | `pr-rules`: a PR that changes `src/main` in `:domain` or `:data` must change `src/test` in that module | none |
| Conventional Commits | `commit-msg` runs `:checkCommitMessage` | `pr-rules`: `:checkCommitMessages` over all PR commits, and the PR title check | none |
| No comments in source | pre-commit runs `:checkNoComments` | `checks` runs it as part of `check` | none |
| Formatting and static analysis | pre-commit runs `:ktlintCheck` | `checks`: Android Lint and ktlint with zero issues, Kotlin warnings as errors | none |
| Tests pass, 100% line and branch coverage on `:domain`, `:data` and on `build-logic/checks` | pre-push runs strict `check`, which covers `:domain` and `:data` | `checks` runs strict `check`, and `:checks:check :checks:koverVerify` for `build-logic/checks` | none |
| Dependency integrity | none | Gradle dependency verification (`gradle/verification-metadata.xml`) and wrapper validation | none |
| Security scanning | none | `analyze (java-kotlin)` and `analyze (actions)` (CodeQL) | Secret scanning with push protection, Dependabot alerts |
| Manifest hardening | none | `checks` runs `:checkReleaseManifest` on the merged release manifest | none |
| Evidence section present and non-empty | none | `pr-rules`: `:checkPullRequest` | PR template |
| Linear history, squash merge only | none | none | Ruleset: linear history, squash only (ADR-0007) |
| Workflows are safe | pre-push runs zizmor offline | `checks` runs `:checkWorkflows` and zizmor | Fork PR workflows need approval; read-only default token |

Rules that later plans add, with their enforcer: UI tests on emulator profiles and the 16 KB page-size check (CI), and release tag protection (tag ruleset and the release workflow). The full table is spec §7.2.

Root check tasks are invoked with a leading colon, for example `:checkNoComments`, because AGP registers same-named tasks in `:app`.

## Practices (not gated)

A machine cannot fully check these. They are taught, and the gates catch only some of the damage if you skip them.

- **Test first (TDD).** A missing test is still caught by the coverage gate and the tests-changed check.
- **Stacking.** Split larger work into a `gh stack` of small PRs.
- **Small PRs, one concern each.**
- **One worktree per branch.** Commits in the main checkout are refused, so this is partly enforced.

## Evidence rule

Nobody, human or agent, says "works", "passes", "fixed" or "done" without the command and its actual output, a CI run link, or a screenshot. A UI change needs a screenshot or recording. A dependency change needs the version source. Never infer a result.

## Dependency rule (spec §6)

- Prefer the Kotlin standard library, the Android platform, AndroidX and Jetpack, JetBrains and Google.
- A third-party dependency is allowed only with a large, very active community. The PR that adds it must show:
  - star count
  - last commit date
  - last stable release date
  - open and closed issue activity
- Every new runtime dependency needs a brief ADR in `docs/adr/`.
- The only third-party runtime dependency is libGDX (ADR-0001, ADR-0002).

## Verify a version before you use it

1. Use only the latest stable release. Take it from a primary source on the day you add it: `maven-metadata.xml` with alpha, beta, rc, dev and EAP builds removed, or the project's release page.
2. Check that it works with the rest of the toolchain (for example AGP's Gradle and JDK requirements).
3. Record it in [`docs/versions.md`](docs/versions.md) with the version, release date, source URL and verification date.
4. **Kotlin cap.** Kotlin may only be raised to a version listed on CodeQL's supported-languages page (https://codeql.github.com/docs/codeql-overview/supported-languages-and-frameworks/). A Dependabot Kotlin bump beyond that stays open until CodeQL lists it (ADR-0006).
5. Lint's `NewerVersionAvailable` check is disabled, so freshness comes from this procedure and Dependabot.
6. Tools come from `mise.toml` and `mise.lock`. Install with `mise install --locked`.

## Dependabot pull requests

A Dependabot PR only starts the work. It cannot merge until someone adds commits that:

- regenerate `gradle/verification-metadata.xml`
- update `docs/versions.md`
- fill in the Evidence section

Kotlin bumps (`org.jetbrains.kotlin:*` and the `org.jetbrains.kotlin.*` plugins) get their own Dependabot group, placed before the catch-all `gradle` group, so a Kotlin PR held by the CodeQL cap does not absorb other Gradle updates. Editing a PR's title, body or labels re-runs the CI workflow, so fixing the Evidence section re-runs `pr-rules` on the new text.

There are no bot exceptions to any gate. Dependabot commits use the `build` or `ci` prefix, and its PRs carry the `dependencies` label, which satisfies the issue-link check. The procedure for refreshing the checksum file, including Linux-only artifacts, is in [`docs/versions.md`](docs/versions.md) under "Dependency verification updates".

## Comments that tools write

The no-comments rule covers generated files too: `*.kt`, `*.kts`, `*.xml`, `*.toml`, `*.yml`, `*.yaml`, `*.sh`, `*.pro` and `*.properties`. A shebang on line 1 is allowed. Markdown is prose and is not checked.

When a tool writes a comment (a header in a generated file, `<!-- -->` in XML, `#` lines in YAML or TOML), delete it before committing. Run `~/.local/bin/mise exec -- ./gradlew :checkNoComments`: it lists the offending files and lines. `gradle/verification-metadata.xml` is checked like any other file. Gradle normally writes `origin` attributes, not comments, so a clean run needs no stripping.

## Decisions

Decisions are recorded in [`docs/adr/`](docs/adr/):

- ADR-0001: native Kotlin with libGDX
- ADR-0002: no KTX
- ADR-0003: no DI framework
- ADR-0004: no detekt
- ADR-0005: signing key
- ADR-0006: Kotlin capped by CodeQL support
- ADR-0007: squash merge
