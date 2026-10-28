# SuperSnake

A neon, classic Snake game for Android phones and tablets (minSdk 31). The rules are in [`docs/game-rules.md`](docs/game-rules.md).

## Why it exists

1. **A fun game.** Neon arcade look, an animated icon on launch, then the main menu.
2. **A reference project.** Clean architecture, security, static checks, latest stable libraries and minimal third-party dependencies.
3. **Education in AI-assisted SDLC.** Humans and AI agents work through the whole lifecycle under the same rules, with machines enforcing them.
4. **A skill factory.** The CI, git hooks and rules get extracted as Claude Code skills in a separate plugin repo.

## Toolchain

| Tool | Version | How it is installed |
|------|---------|---------------------|
| mise | 2026.10.18 or newer | `curl -fsSL https://mise.run \| sh` |
| JDK | Temurin 25.0.5+8 | `mise install --locked` (SHA-pinned, cross-checked against Adoptium by bootstrap) |
| `gh` | 2.103.2 | `mise install --locked` (SHA-pinned, attestation-checked) |
| zizmor | 1.31.1 | `mise install --locked` (SHA-pinned, attestation-checked) |
| `gh-stack` extension | v0.2.1 | `scripts/bootstrap.sh` |
| Android SDK | platform `android-37.0`, build-tools `37.0.0`, cmdline-tools `23.0` | Android Studio or `sdkmanager`, outside mise |

Supported machines: macOS on Apple silicon, Linux x64 and Linux arm64. Intel macOS is not supported, because Adoptium publishes no Temurin 25.0.5+8 build for it. All versions, sources and verification dates are in [`docs/versions.md`](docs/versions.md).

## Quick start

1. Install mise (see above) and the Android SDK packages listed in the table.
2. Tell Gradle where the SDK is: export `ANDROID_HOME`, or put `sdk.dir=/path/to/sdk` in `local.properties` (untracked). A fresh clone has no `local.properties`.
3. Run:

```sh
git clone https://github.com/EastwoodNo1/SuperSnake
cd SuperSnake
export ANDROID_HOME="$HOME/Library/Android/sdk"
scripts/bootstrap.sh
~/.local/bin/mise exec -- ./gradlew check
```

`scripts/bootstrap.sh` sets `core.hooksPath=.githooks`, checks the mise version, the Temurin checksum and the Android SDK packages, runs `mise install --locked` and installs `gh-stack`. It prints `hooks installed` once the hooks are active.

Use `scripts/gradle-strict.sh <tasks>` when you want the strict gate: it fails on any Gradle deprecation except one allowlisted AGP 9.4.1 message.

### Claude Code plugin (once per machine)

The SDLC skills come from the `android-sdlc` plugin. `.claude/settings.json` declares it, but each contributor installs it once:

```sh
claude plugin marketplace add EastwoodNo1/android-sdlc-skills#v0.2.0 --scope project
claude plugin install android-sdlc@android-sdlc-skills --scope project
claude plugin list
```

Accept the workspace trust prompt when you open the folder in Claude Code. `claude plugin list` must show `android-sdlc@android-sdlc-skills` as `✔ enabled`.

## Contributing

Read [`CONTRIBUTING.md`](CONTRIBUTING.md) for the workflow and rules. AI agents also read [`CLAUDE.md`](CLAUDE.md). Decisions are recorded in [`docs/adr/`](docs/adr/), and the design is in [`docs/superpowers/specs/2026-10-22-supersnake-design.md`](docs/superpowers/specs/2026-10-22-supersnake-design.md). Open [`docs/design/neon-arcade-mockup.html`](docs/design/neon-arcade-mockup.html) in a browser to see the chosen look.

## Status

Plan 1 (foundation) is complete. Plan 2 (game engine) is complete. Plan 3 (app) is written and in progress. The plan files are in [`docs/superpowers/plans/`](docs/superpowers/plans/); the Plan 3 file's **Start here** section has the state.
