# SuperSnake — Design

Date: 2026-10-22
Status: approved by owner 2026-10-22

Every decision below is tagged with who made it:

- **[owner]**: decided by the project owner in the brainstorming conversation.
- **[call]**: a call made by the designer (Claude). The owner can overturn it.
- **[builder]**: deliberately left to whoever plans and builds.

---

## 1. Intent and why

SuperSnake is a classic Snake game for Android phones and tablets. It exists for four reasons, all equally real **[owner]**:

1. **A fun game.** It has a neon arcade look, an animated icon on launch, and then the main menu.
2. **A reference project.** It shows how the owner builds Android apps: clean architecture, security, static checks, latest stable libraries, minimal third-party dependencies, best practices.
3. **Education in AI-assisted SDLC.** Humans and AI agents work through the full lifecycle (idea → design → plan → build → verify with evidence → review → release) under the same rules.
4. **A skill factory.** The CI pipeline, git hooks and SDLC rules are extracted as Claude Code skills in a **separate plugin repo**, so the next project reuses them instead of rebuilding them.

The project is run **solo** **[owner]**. Nothing in the process may assume a second human reviewer.

Both repos are **public, under the owner's personal GitHub account** **[owner]**. This keeps rulesets with no bypass, secret scanning with push protection, CodeQL and Actions minutes free.

### Success looks like

- A signed APK of a playable, accessible neon Snake on GitHub Releases.
- Every rule in §7.2 is enforced by a machine (hook, CI check, GitHub ruleset or setting), not only written down. The practices in §7.3 are taught, and are honestly labelled as not gated.
- A new project can install the plugin, run its skills, and get the same CI, hooks and rules working on day one.

### Anti-goals (failure even if it "works")

- A rule that exists only in a document and can be skipped. Anything that can't be machine-checked is labelled a practice (§7.3), never a rule.
- A claim that something works without command output, a test result, a CI run or a screenshot to back it.
- Comments in code.
- A third-party dependency without a large, active community.
- A version chosen from memory instead of from a verified source.
- Shipping game behaviour that is presented as "the original Snake" but isn't sourced.

### Non-goals

- iOS, desktop or web builds.
- Online features, accounts, leaderboards, ads, analytics, telemetry.
- Multiplayer.
- Play Store publishing in v1 (it comes later, so the pipeline must make it easy; see §8).

---

## 2. Game rules

### Source **[owner]**

The rules are based on the original Nokia *Snake* (Taneli Armanto, Nokia, 1998, Nokia 6110/5110), as documented by:

- Juho Kuorikoski, *Finnish Video Games: A History and Catalog*, McFarland, 2015, pp. 48–50
- as cited by Wikipedia, [Snake (1998 video game)](https://en.wikipedia.org/wiki/Snake_(1998_video_game))

The 1998 Nokia 6110 user guide does not document the rules in detail. No official rules spec exists. The spec below must be committed as `docs/game-rules.md`, with every rule tagged **[source]** or **[call]**.

### Rules from the source

| # | Rule |
|---|------|
| S1 | The snake moves continuously in one of four directions. The player changes its direction. |
| S2 | The play field is a bounded box. |
| S3 | Orbs (food) appear on the field. Eating one increases the score and the snake's length. |
| S4 | Colliding with a wall or with the snake's own body ends the game. |
| S5 | There is a difficulty setting. |

### Rules that are our calls **[call]**

| # | Rule |
|---|------|
| C1 | The board is a fixed 20 × 20 grid, drawn as a square and centred (letterboxed) in any orientation and on any screen size. |
| C2 | The snake starts with 3 segments in the centre, heading right. |
| C3 | One orb is on the board at a time. It spawns on a uniformly random free cell. The random source is injected so tests are deterministic. |
| C4 | Eating an orb adds exactly 1 segment: on the tick that eats it, the tail does not move. |
| C5 | A turn into the opposite of the last queued direction is ignored. If the queue is empty, the current direction is used instead. **Replaced on 2026-10-28 by C15 in `docs/game-rules.md`; see `docs/superpowers/specs/2026-10-28-engine-feel-and-storage-design.md` §4.2.** |
| C6 | Turn inputs are buffered, with up to 2 queued turns, so fast double turns are not lost. A turn is applied at most once per tick. **Replaced on 2026-10-28 by C16 in `docs/game-rules.md`; see `docs/superpowers/specs/2026-10-28-engine-feel-and-storage-design.md` §4.2.** |
| C7 | Moving into the tail's cell is legal only when the tail leaves that cell on the same tick. On a growth tick (C4) the tail stays, so moving into it is a collision. |
| C8 | Difficulty is **Assist** plus levels 1–9.<br>• Levels 1–9: tick interval = `250 − round(22.5 × (level − 1))` ms, where `round` rounds halves up. That gives exactly 250, 227, 205, 182, 160, 137, 115, 92, 70 ms. Points per orb equal the level.<br>• **Assist**: 400 ms per tick and 1 point per orb, for players who need more time. It shows as "A" in high scores. |
| C9 | If no free cell remains after eating, the game ends as a **win**. |
| C10 | The game pauses automatically when the app goes to the background, and resumes only on an explicit user action. |
| C11 | Single player only. |
| C12 | The top 10 high scores (score, level, date) are stored on the device. |

The rules engine is a pure function of state, input and random source, which makes it fully unit-testable.

Rules C13 to C25 (start countdown, turning, timing, pausing, scores, movement, swipes) were added on 2026-10-28 by `docs/superpowers/specs/2026-10-28-engine-feel-and-storage-design.md` §4 and are listed in `docs/game-rules.md`.

---

## 3. Experience, look and accessibility

### Look **[owner]**

Neon arcade style:

- a dark background
- a glowing cyan snake
- pulsing magenta food
- glowing outlined buttons, with the primary action filled

The reference mockup is option A from the brainstorm: `docs/design/neon-arcade-mockup.html`. Open it in a browser to see the animated icon and the main menu.

### Flow

1. **Launch:** an animated neon icon (snake slithering, food pulsing) via the Android SplashScreen API, followed by the main menu **[owner]**.
2. **Main menu** has these items **[call]**:
   - Play
   - High scores
   - Settings
   - Best score, shown small
3. **Game** board, drawn by libGDX:
   - The HUD (score, level, pause button) is drawn by Compose on top of the board, so it stays accessible **[call]**.
4. **Pause** overlay: Resume, Restart, Main menu **[call]**.
5. **Game over / win** screen: score, best score, Play again, Main menu **[call]**.
6. **High scores:** the top 10 list **[call]**.
7. **Settings** **[call]**:
   - difficulty: Assist, or 1–9
   - sound on/off
   - haptics on/off
   - control scheme: swipe, or on-screen D-pad

### Controls **[call]**

- Swipe on the board.
- An optional on-screen D-pad, for users who can't swipe precisely.
- Hardware keyboard, D-pad or gamepad arrows (tablets, Chromebooks).

### Accessibility (must, verified by tests) **[owner: best practice; specifics are calls]**

- TalkBack labels on every interactive element.
- Game events are announced politely, never on every orb, to avoid noise. Announced: game start, pause, every 10th orb, new high score, game over, win.
- Touch targets of at least 48 dp.
- Text contrast meets WCAG 2.2 AA (4.5:1), with the glow ignored when measuring.
- Text scales with the system font size, up to 200%, without clipping.
- When the system "remove animations" setting is on, the splash, menu and food pulse are static, and particle effects are off.
- Snake, food and walls also differ by shape and brightness, not by hue alone, so the game is playable with colour blindness.
- The full menu can be navigated by keyboard or D-pad, with a visible focus indicator.
- Portrait and landscape layouts work on phones and tablets. Phone and tablet screen sizes are tested.

### Assets **[call]**

- All art, sound and fonts are either made for this project or licensed CC0 / SIL OFL.
- Each asset's licence is recorded in `docs/assets.md`.
- Sound effects are synthesized in code, with no audio files (owner, 2026-10-28; ADR-0010).

---

## 4. Stack

### Decisions

| Area | Choice | Decided by |
|------|--------|-----------|
| Platform | Android phones and tablets only | owner |
| Language | Kotlin, at the newest stable version that CodeQL supports (ADR-0006) | owner |
| Game board rendering | **libGDX alone, no KTX** | owner |
| Menus, HUD, overlays, splash | Jetpack Compose + SplashScreen API | call (accepted with libGDX choice) |
| Async | Kotlin Coroutines / Flow | call |
| Persistence | Jetpack DataStore. **Core with our own serializers, not Preferences (owner, 2026-10-28; `docs/superpowers/specs/2026-10-28-engine-feel-and-storage-design.md` §3)** | call |
| Dependency injection | None. Plain constructors and a small manual composition root | call |
| Build | Gradle Kotlin DSL, version catalog `gradle/libs.versions.toml` | call |
| Static analysis | Android Lint, ktlint (standalone), and our own no-comments check. **No detekt** (ADR-0004) | owner |
| Coverage | Kover | call |
| Unit tests | JUnit + `kotlin.test` | call |
| UI tests | Compose UI test + AndroidX Test on emulators | call |
| CI | GitHub Actions | owner (GitHub) |
| Security scanning | GitHub CodeQL, Dependabot, secret scanning with push protection | call |

### Why native Kotlin + libGDX (record as ADR-0001)

- Options considered: Compose Canvas only, libGDX + KTX, libGDX alone, Godot 4, SurfaceView/OpenGL by hand, Flutter, Kotlin Multiplatform.
- **Godot** was rejected: GDScript/C# skills don't carry over to the owner's Android work, it is weak on Android accessibility, and it doesn't fit ktlint/Kover.
- **Flutter and KMP** were rejected: they solve cross-platform problems the project doesn't have.
- **Compose Canvas alone** was the designer's first recommendation. The owner chose libGDX so the board can get richer visuals (glow, particles, smooth motion) while the code stays Kotlin.

### Why no KTX (record as ADR-0002)

Verified on 2026-10-22 via the GitHub API:

| Repo | Stars | Last commit | Latest release | Status |
|------|-------|-------------|----------------|--------|
| `libgdx/libgdx` | 25,465 | 2026-10-14 | 1.14.2 | healthy |
| `libktx/ktx` (original) | 1,470 | 2025-06-28 | 1.13.1-rc1 | stalled |
| `Quillraven/ktx` (community fork) | 3 | — | 1.14.2-rc2 | tiny |

KTX therefore fails the third-party rule (§6). libGDX's Java API is used directly from Kotlin. Any convenience helpers are our own code and are covered by tests.

### Why no DI framework (record as ADR-0003)

- The app has around a dozen objects to wire together. A manual composition root is easy to read and adds zero dependencies.
- Revisit only if wiring grows past what fits on one screen.

### Versions

Every version must be the latest **stable** release, verified with a cited source on the day it is added, and recorded in `docs/versions.md` with the source URL and date. Verified values as of 2026-10-22 are in §11.

---

## 5. Architecture

Clean architecture with dependencies pointing inward. Four Gradle modules **[call]**:

```
:app ──► :board ──► :domain
  │                    ▲
  └──► :data ──────────┘
```

| Module | Kind | Contains | Must not depend on | Coverage gate |
|--------|------|----------|--------------------|---------------|
| `:domain` | Kotlin JVM | Game rules engine (state, tick, input queue, collisions, scoring, spawn, tick timing), **input classification** (swipe vector → direction, using a minimum distance and dominant axis; a platform-neutral key enum → direction), **render math** (interpolating segment positions between ticks, particle timing curves, glow pulse phase), use cases, repository interfaces (`HighScoreRepository`, `SettingsRepository`), and `Random` and `Clock` abstractions | Android, libGDX, DataStore | **100% line + branch** |
| `:data` | Kotlin JVM, or Android library if unavoidable **[builder]** | DataStore-backed implementations of the domain repositories | libGDX, Compose | **100% line + branch** |
| `:board` | Kotlin JVM using libGDX core | libGDX `ApplicationListener` that only draws: it takes positions, phases and particle states computed by `:domain` and issues libGDX draw calls. It contains no rules, no math beyond unit conversion, and no branching on game state. | Android, Compose, `:data` | Excluded from the unit gate; covered by UI tests |
| `:app` | Android application | Activity, SplashScreen, Compose screens, ViewModels with no branching, navigation, libGDX Android backend hosting `:board`, composition root. A clock adapter feeds platform time to the `:domain` tick logic. Platform events (`MotionEvent`, `KeyEvent`) are translated 1:1 into `:domain` input types | — | Excluded from the unit gate; covered by UI tests |

### Rules

- Module boundaries are enforced by Gradle dependencies. A module cannot import what it doesn't depend on.
- **ViewModels contain no branching logic.** They only call `:domain` use cases and expose their results as state. Any decision belongs in `:domain`, where the 100% gate covers it **[call]**.
- **What is outside the unit gate is exactly this:**
  - libGDX draw calls in `:board`
  - Android, Compose and libGDX-backend glue in `:app`
  - composable layout code

  All of these are covered by UI tests instead. Reviewers reject branching logic added to these places.
- The game loop timing (ticks per level) is owned by `:domain`. `:app` only drives the clock. Time is injected so tests are deterministic.
- How the libGDX view is embedded in the Compose screen (`AndroidFragmentApplication` in an `AndroidView`/fragment container, or an alternative) is **[builder]**. It must keep the HUD and overlays in Compose and survive configuration changes and backgrounding without losing the game.

---

## 6. Third-party dependency rule **[owner]**

- Prefer the Kotlin standard library, the Android platform, AndroidX/Jetpack, JetBrains and Google first.
- A third-party dependency is allowed only if it has a large, very active community. **Evidence is required in the PR that adds it**:
  - star count
  - last commit date
  - last stable release date
  - open/closed issue activity
- A brief ADR is required for every new runtime dependency.
- Current third-party runtime dependencies: **libGDX only**.
- Build/CI tools in use: ktlint (`pinterest/ktlint`: 6,767 stars, last commit 2026-10-22, latest stable 1.8.0 from 2025-11-14), zizmor **[owner, 2026-10-23]** (`zizmorcore/zizmor`: MIT, 6,759 stars, last push 2026-10-23, latest stable v1.31.1 from 2026-10-15, 171 open issues, 18 opened and 12 closed in the last 30 days), release-please-action **[owner, 2026-10-26]** (`googleapis/release-please-action`, used from Plan 4; evidence below) and GitHub Actions from verified publishers (the `reactivecircus` emulator runner is the exception and needs the same evidence).
  - `googleapis/release-please-action`: Apache-2.0, 2,559 stars, last commit 2026-05-28, latest stable v5.0.0 from 2026-04-22, 139 open issues. In the 30 days to 2026-10-26, 1 issue was opened and 0 closed (`gh api search/issues`).
  - The engine `googleapis/release-please` is very active: 7,681 stars, last push 2026-10-22, latest stable v17.11.2 from 2026-08-24.
  - Honest note: the action wrapper's own activity is modest, while the engine it runs is very active. The owner decided to adopt it anyway.
  - `actions/create-github-app-token` (MIT, 905 stars, latest stable v3.3.0 from 2026-10-12) is a first-party GitHub action, so it falls under the verified-publisher rule.

---

## 7. SDLC: how work is done **[owner, enforcement mechanisms are calls]**

§7.2 lists **rules**, and every rule is machine-enforced. §7.3 lists **practices**: they are taught by the `sdlc-rules` skill and `CONTRIBUTING.md` but can't be fully checked by a machine, and the docs say so plainly.

### 7.1 Flow for every change

1. **Issue.** A GitHub Issue describes the why. *(rule)*
2. **Worktree.** One git worktree per branch (`git worktree add ../SuperSnake-<branch> -b <type>/<short-name>`). *(partly a rule: commits in the main checkout are refused)*
3. **Test first.** A failing test is written before production code (TDD). *(practice)*
4. **Small commits.** Each commit uses Conventional Commits. **The why and what go in the commit body, never in code.** *(rule)*
5. **Stacked PRs.** Larger work is split into a stack of small PRs using `gh stack` (§11 has its verified current form). *(practice)*
6. **Checks.** All local hooks and CI checks pass.
7. **Evidence.** The PR description contains an Evidence section: commands run with their output, CI links, and screenshots/recordings for UI changes.
8. **AI review (optional).**
   - The owner starts a GitHub Copilot or Claude review by hand, on the PRs where they want one. It is not required and not on every PR **[owner]**.
   - Claude reviews run **locally from Claude Code** (for example `/code-review` or `/review` on the branch or PR). **No Claude GitHub App or Claude Code GitHub Action is installed** on either repo, and no Anthropic API key is stored in GitHub **[owner, 2026-10-23]**.
   - When a review is run, every comment thread must be resolved before merge. This is enforced by the "require conversation resolution" setting **[call]**.
9. **Merge.** Only via the PR, once all required checks are green.
10. **Release.** As in §8.

### 7.2 Rules and how each is enforced

| Rule | Local (git hooks) | CI (required check) | GitHub setting |
|------|-------------------|--------------------|----------------|
| No direct commits or pushes to `main` | pre-commit and pre-push refuse on `main` | — | Ruleset on `main`: PR required, no bypass list (not even the admin), conversation resolution required |
| Work happens in a linked worktree | pre-commit refuses commits made in the main checkout (where `git rev-parse --git-dir` equals `--git-common-dir`). The main checkout stays on `main` and is only used for syncing | — | — |
| Every PR links an issue | — | PR body must contain `Closes #n`, or the PR carries the `dependencies` label (Dependabot) | — |
| Production changes come with tests | — | A PR that changes `src/main` in `:domain` or `:data` must also change `src/test` in that module | — |
| Conventional Commits, with a body required for `feat`, `fix`, `refactor`, `perf` | `commit-msg` hook | Commit-message check over all PR commits; PR title header check | — |
| No comments in source | `pre-commit` | A syntax-aware check (strings and URLs never count as comments) over `*.kt`, `*.kts`, `*.xml`, `*.toml`, `*.yml`/`*.yaml`, `*.sh`, `*.pro`, `*.properties`. Exceptions: none. Shebang lines are not comments | — |
| Formatting and static analysis | `pre-commit` (changed files) | Android Lint + ktlint, zero issues, no baseline; Kotlin warnings as errors | — |
| Tests pass; 100% line + branch coverage on `:domain` and `:data` | `pre-push` | Unit tests + `koverVerify` | — |
| UI tests on emulator profiles (§13.15) | — | Instrumented tests with screenshots as artifacts | — |
| Dependency integrity | — | Gradle dependency verification (checksums). Gradle wrapper validation, which `setup-gradle` does by default | — |
| Security scanning | — | CodeQL (Kotlin + Actions) | Secret scanning + push protection, Dependabot alerts, private vulnerability reporting, `SECURITY.md` |
| Manifest and build hardening | — | Automated check on the merged release manifest and APK:<br>• no `INTERNET` permission<br>• `debuggable` false<br>• R8 on<br>• only the launcher activity exported<br>• `allowBackup` true, with `dataExtractionRules` and `fullBackupContent` that include only the DataStore files for high scores and settings | — |
| 16 KB page-size support | — | `zipalign -c -P 16 -v 4` on the release APK passes (Play requirement from 2027-02-01; libGDX ships native `.so` files) | — |
| Evidence section present and non-empty | — | PR body check | PR template |
| Linear history, squash merge only | — | — | Ruleset: require linear history; the only allowed merge method is squash |
| Workflows are safe | — | A workflow lint check fails on:<br>• any `pull_request_target` or `workflow_run` trigger<br>• `${{ github.event.* }}` interpolated inside a `run:` script<br>• an action not pinned by full commit SHA<br>• a missing top-level `permissions: {}`<br>and zizmor (latest stable, pinned via mise) reports no findings | Fork PR workflows require approval for all outside contributors; default `GITHUB_TOKEN` permission is read-only |
| Release tags are trustworthy | — | Tags are created only by the release-please release job. The release workflow fails unless the tag's commit is an ancestor of `origin/main` | Tag ruleset on `v*`: only via the release process, no deletion or force-update. `release` environment limited to `v*` tags (**open, Plan 4:** §8 runs the release job in the release-please run on `main`, which this limit blocks; resolve by allowing `main` in the environment, or by a separate tag-triggered build job triggered by the tag release-please creates with the App token). Immutable releases on. No "prevent self-review" setting, since solo |

### 7.3 Practices (taught, not gated)

- **Test first (TDD).** A missing test is still caught by the coverage gate and the tests-changed check.
- **Stacking.** Split larger work into a `gh stack` of small PRs.
- **Small PRs, one concern each.**

### 7.4 Hooks

- Plain shell git hooks live in the repo under `.githooks/`.
- `scripts/bootstrap.sh` activates them with `git config core.hooksPath .githooks`.
- No Node, Husky or other hook manager.
- CI re-runs everything, so a skipped hook (`--no-verify`) changes nothing about what can merge.

### 7.5 Evidence rule (applies to humans and AI agents equally)

- Nobody says "works", "passes", "fixed" or "done" without the command and its actual output, a CI run link, or a screenshot.
- A UI change needs a screenshot or recording.
- A dependency change needs the version source.
- This is written into `CONTRIBUTING.md`, into `CLAUDE.md`, and into the SDLC skill.

---

## 8. Release **[owner: GitHub Releases signed APK, Play later; mechanism is call]**

- **Versioning:** SemVer, with `versionCode` derived from it deterministically **[builder]**.
- **Trigger:** release-please (`googleapis/release-please-action`) runs on every push to `main` and keeps one release PR open **[owner, 2026-10-26]**.
  - The release PR holds the next SemVer version and a changelog built from Conventional Commits. With squash merge, every PR title on `main` is a Conventional header.
  - Merging the release PR makes release-please create the tag `vX.Y.Z` and a **draft** GitHub Release.
  - `versionCode` is still derived deterministically from the SemVer version. release-please updates the version through its config (for example a version file or `extra-files`). Plan 4 picks the exact mechanism.
- **Authentication:** release-please uses a GitHub App installation token from `actions/create-github-app-token`, not `GITHUB_TOKEN`.
  - Why: PRs, tags and releases created with `GITHUB_TOKEN` don't trigger other workflows. The release PR would then never get its four required checks and could never merge.
  - The owner creates the App with least privilege: contents write and pull-requests write, installed on this repo only.
  - The App's ID and private key are secrets in the `release` environment.
- **Release workflow** (one run, in the `release` environment). After release-please reports `release_created`, the release job:
  1. Verifies the tag's commit is an ancestor of `origin/main`.
  2. Runs all checks again.
  3. Builds the release APK (R8).
  4. Checks 16 KB alignment.
  5. Signs the APK.
  6. Produces a SHA-256 checksum and a build provenance attestation.
  7. Uploads the APK and checksum to the draft release and publishes it.
- The release is immutable once published, which is why it starts as a draft. Release tags (`v*`) are created only by this process, through the tag ruleset: no deletion or force-update.
- Only CI can produce a signed build. No signing material ever exists in the repo or the working tree.
- **No Android App Bundle (AAB) for now.** It is added when Play work starts **[owner]**.

### Signing key (record as ADR-0005) **[owner]**

- The release key created now **is the app-signing key** for the app's whole life.
- When Play publishing starts, this key is uploaded to Play App Signing, and a **separate upload key** is created for uploading to Play. Players who installed from GitHub can then update from Play, because Android requires updates to be signed with the same key.
- The key is stored in GitHub Actions secrets that only the `release` environment can access. The GitHub App's ID and private key live in the same environment.
- There is an offline encrypted backup held by the owner.
- `docs/release.md` documents creation, backup, recovery, the Play App Signing migration steps, and what losing the key means.

## 9. Onboarding

Files in the SuperSnake repo **[call]**:

| File | For | Content |
|------|-----|---------|
| `README.md` | Everyone | What it is, screenshot, quick start (`scripts/bootstrap.sh`, then `./gradlew check`) |
| `CONTRIBUTING.md` | Humans | The SDLC flow and rules in §7, with how each is enforced |
| `CLAUDE.md` | AI agents | Points to the plugin skills, and restates the evidence rule and the no-comments rule |
| `docs/game-rules.md` | Both | §2, with sources |
| `docs/adr/` | Both | One file per decision, plus one for each later decision:<br>• ADR-0001 stack<br>• ADR-0002 no KTX<br>• ADR-0003 no DI framework<br>• ADR-0004 no detekt<br>• ADR-0005 signing key<br>• ADR-0006 Kotlin version capped by CodeQL support<br>• ADR-0007 squash merge and no required commit signatures |
| `SECURITY.md` | Everyone | How to report a vulnerability privately |
| `docs/versions.md` | Both | Every version, with source URL and verification date |
| `docs/release.md` | Owner | Signing key lifecycle, how to cut a release |
| `docs/assets.md` | Both | Asset licences |

**Onboarding is done** when a fresh clone, `scripts/bootstrap.sh` and `./gradlew check` succeed on the documented toolchain, with the output as evidence. CI proves this on every PR.

---

## 10. The plugin repo (separate) **[owner]**

A separate GitHub repo containing a Claude Code plugin (and marketplace manifest). SuperSnake installs it as its first consumer.

### Skills (names are **[call]**)

| Skill | Does |
|-------|------|
| `android-ci` | Generates the GitHub Actions workflows from §7.2 and §8 for an Android/Kotlin Gradle project, following the workflow-safety rules. Then applies rulesets (branch and tag), environments, and repo security settings with `gh`. |
| `git-hooks` | Installs `.githooks/` and `scripts/bootstrap.sh` with the local checks in §7.2. |
| `sdlc-rules` | Teaches the workflow in §7: rules vs practices, worktrees, `gh stack`, TDD, Conventional Commits with the why and what in the body, no comments in code, the evidence rule, the dependency rule (§6), version verification (including the CodeQL Kotlin cap), handling Dependabot PRs, and the release steps. |

### Rules for the plugin

- A skill is "done" only when it has been applied to SuperSnake and the result was verified with evidence (green CI run, hook rejecting a bad commit, and so on).
- Skills are written as SuperSnake needs them, not upfront.
- The plugin is versioned with SemVer tags. SuperSnake pins a version.
- The plugin repo follows the same SDLC rules (Conventional Commits, PRs, evidence), where they apply to a non-Android repo.

### Left to the builder

The repo name and GitHub owner, and the plugin's internal layout following current Claude Code plugin conventions.

---

## 11. Verified versions and tooling facts (2026-10-22)

### How these were verified

- **Maven artifacts:** stable releases were taken from `maven-metadata.xml`, with alpha, beta, rc, dev and EAP builds removed.
- **GitHub projects:** the release's `published_at` date was used.
- **Source abbreviations:**
  - G = `https://dl.google.com/android/maven2`
  - MC = `https://repo1.maven.org/maven2`
- **Recheck:** every version must be checked again when first added to the build and recorded in `docs/versions.md`.
- **Kotlin cap:** Kotlin may only be raised to a version listed in CodeQL's supported-languages page. A Dependabot Kotlin bump beyond that stays open until CodeQL catches up.

### Build toolchain

| Item | Version | Released | Source |
|------|---------|----------|--------|
| Gradle | 9.8.2 | 2026-10-19 | github.com/gradle/gradle/releases/tag/v9.8.2 |
| Android Gradle Plugin | 9.5.0 | 2026-10-28 | G/com/android/tools/build/gradle (9.5 needs Gradle ≥ 9.6, JDK ≥ 17, build-tools ≥ 36.0.0, supports API ≤ 37) |
| Kotlin | **2.4.20** | 2026-09-07 | github.com/JetBrains/kotlin/releases/tag/v2.4.20. Capped by CodeQL, which supports "Kotlin 1.8.0 to 2.4.20" (codeql.github.com/docs/codeql-overview/supported-languages-and-frameworks/). 2.4.21 (2026-10-08) waits until CodeQL lists it **[owner, ADR-0006]** |
| JDK toolchain | 25 (Temurin 25.0.5+8) **[owner, 2026-10-23]**, installed via mise | 2026-10-22 | github.com/adoptium/temurin25-binaries/releases/tag/jdk-25.0.5%2B8. Latest LTS. CodeQL supports Java 7 to 26 (codeql.github.com/docs/codeql-overview/supported-languages-and-frameworks/, read 2026-10-23) |
| compileSdk / targetSdk | 37 (Android 17) | 2026-06 | android-developers.googleblog.com/2026/06/Android-17.html. Play requires target ≥ 36 for new apps and updates (developer.android.com/google/play/requirements/target-sdk) |
| minSdk | **31** (Android 12) **[owner]** | — | Animated SplashScreen icon on every supported device, so no fallback is needed. Devices on Android 8–11 can't install the game |

### Tool installation [owner, 2026-10-23]

- Dev tools are installed and run through mise (2026.10.18), not system installs.
- Every tool mise installs is SHA-pinned in a committed `mise.lock` and installs only in locked mode (`mise install --locked`). Signature or provenance is verified wherever mise can.
- Java is Eclipse Temurin 25.0.5+8 from `github:adoptium/temurin25-binaries` (tag `jdk-25.0.5+8`). mise's built-in `core:java` catalog lags. Temurin is GPG-signed only and mise can't verify GPG for Java, so it is SHA pin only, cross-checked against the Adoptium API and the release's `.sha256.txt` assets.
- gh 2.103.2 comes from `aqua:cli/cli`, and its lock entries record `provenance = "github-attestations"`.
- zizmor (latest stable, v1.31.1 on 2026-10-23) comes from `aqua:zizmorcore/zizmor`, and its lock entries record per-platform sha256 checksums with `provenance = "github-attestations"`.
- The Android SDK stays outside mise: the only backend, vfox-android-sdk, has 4 stars and skips checksums. Google doesn't sign SDK packages, and sdkmanager checks SHA-1.
- Locked platforms are macos-arm64, linux-x64 and linux-arm64. Intel macOS is unsupported because Adoptium publishes no macOS x64 JDK 25.0.5+8.

### AndroidX and Kotlin libraries

| Item | Version | Released | Source |
|------|---------|----------|--------|
| Compose BOM | 2026.09.00 (ui 1.12.1) | 2026-09-09 | G/androidx/compose/compose-bom |
| activity-compose | 1.13.0 | 2026-03-11 | G/androidx/activity/activity-compose |
| navigation-compose | 2.10.2 | 2026-09-23 | G/androidx/navigation/navigation-compose. **Not used: it pulls kotlinx-serialization-core in at runtime; the app uses its own back stack (owner, 2026-10-28; ADR-0009)** |
| lifecycle viewmodel-compose / runtime-compose | 2.11.0 | 2026-06-17 | G/androidx/lifecycle |
| datastore-preferences | 1.3.0 | 2026-10-21 | G/androidx/datastore/datastore-preferences. **Not used: replaced by `datastore-core` 1.3.0 (owner, 2026-10-28), which needs no okio** |
| core-splashscreen | 1.2.0 | 2025-11-05 | G/androidx/core/core-splashscreen |
| core-ktx | 1.19.1 | 2026-09-23 | G/androidx/core/core-ktx |
| fragment-ktx | 1.9.1 | 2026-09-23 | G/androidx/fragment/fragment-ktx |
| kotlinx-coroutines | 1.11.0 | 2026-05-08 | github.com/Kotlin/kotlinx.coroutines/releases/tag/1.11.0 |

### Game library

| Item | Version | Released | Source |
|------|---------|----------|--------|
| libGDX (`gdx`, `gdx-backend-android`, `gdx-platform`) | 1.14.2 | 2026-06-05 | github.com/libgdx/libgdx/releases/tag/1.14.2 |

### Static analysis and coverage

| Item | Version | Released | Source |
|------|---------|----------|--------|
| ktlint | 1.8.0 | 2025-11-14 | github.com/pinterest/ktlint/releases. Run standalone, through a small Gradle task or the ktlint CLI **[builder]**. No third-party Gradle wrapper plugin unless it passes §6 |
| Kover | 0.9.11 | 2026-09-29 | github.com/Kotlin/kotlinx-kover/releases/tag/v0.9.11 |
| zizmor | 1.31.1 | 2026-10-15 | github.com/zizmorcore/zizmor/releases/tag/v1.31.1. Static checks for GitHub Actions workflows, installed through mise from `aqua:zizmorcore/zizmor`. Recheck the latest stable when it is added |

### Testing

| Item | Version | Released | Source |
|------|---------|----------|--------|
| JUnit 6 (Jupiter, JVM modules) | 6.1.3 | 2026-08-07 | github.com/junit-team/junit-framework/releases/tag/r6.1.3 |
| JUnit 4 (instrumented tests) | 4.13.2 | 2021-02-13 | MC/junit/junit (still the current Android instrumented runner API) |
| androidx.test runner / rules / ext-junit / espresso-core | 1.7.0 / 1.7.0 / 1.3.0 / 3.7.0 | 2025-07-30 | G/androidx/test |
| Robolectric | 4.17 | 2026-09-10 | **Not planned.** Pure-JVM modules don't need it, and UI tests run on emulators |

### GitHub Actions (pin by commit SHA)

| Item | Version | Released | Source |
|------|---------|----------|--------|
| actions/checkout | v7.0.1 | 2026-07-20 | releases/latest |
| actions/setup-java | v6.1.0 | 2026-10-14 | Not used: the JDK comes from mise (R9). Listed only as a record |
| gradle/actions (setup-gradle) | v6.4.0 | 2026-09-28 | releases/latest. Use `cache-provider: basic` (MIT-licensed), not the default proprietary Enhanced Caching. Wrapper validation is built in, so no separate step is needed (github.com/gradle/actions/blob/main/docs/setup-gradle.md) |
| github/codeql-action | v4.39.1 | 2026-10-21 | releases (not the `codeql-bundle` tag) |
| actions/attest-build-provenance | v4.2.2 | 2026-08-06 | releases/latest |
| googleapis/release-please-action | v5.0.0 | 2026-04-22 | releases/latest. Owner-approved third-party action (§6 evidence, 2026-10-26). Engine `googleapis/release-please` v17.11.2 (2026-08-24), releases/latest. Used from Plan 4 |
| actions/create-github-app-token | v3.3.0 | 2026-10-12 | releases/latest. First-party GitHub action. Used from Plan 4 |
| ReactiveCircus/android-emulator-runner | v2.38.0 | 2026-07-05 | 1,307 stars, last commit 2026-08-26. This is the one third-party action, justified by §6 evidence |

### Stacked PRs

- **GitHub stacked pull requests** are generally available on github.com since 2026-10-06 (github.blog/changelog/2026-10-06-stacked-pull-requests-generally-available). Auto-merge for stacks is still rolling out.
- **`gh stack`** is an official GitHub CLI **extension** (`github/gh-stack`), version v0.2.1 released 2026-10-09 (stable, but 0.x).
  - Install: `gh extension install github/gh-stack`.
  - Requires: gh ≥ 2.0 and Git ≥ 2.36.
  - Docs: docs.github.com/en/pull-requests/how-tos/stacked-pull-requests
- **`gh stack` workflow:**
  - `init` / `add` to build the stack
  - `submit` opens one PR per layer
  - `sync` / `rebase` to update
  - `merge` merges the stack up to the selected PR, all or nothing, using the repo's squash method (flags verified when stacked PRs are first used)
  - Branch protection still applies to every PR in the stack.
- **Merge method [owner, 2026-10-26, ADR-0007]:** squash merge only, with linear history required.
  - The PR title, a Conventional Commit header, becomes the subject on `main`.
  - The commit messages of the PR become the body, keeping their why/what and trailers.
  - Signed commits are still **not** required.
  - Release integrity comes from the CI-only build, provenance attestation and immutable releases.

### Why no detekt (record as ADR-0004) **[owner]**

- The latest stable detekt (1.23.8, Feb 2025) predates Kotlin 2.4, and detekt 2.x is alpha only (`dev.detekt` 2.0.0-alpha.6, 2026-08-04).
- Keeping it would mean breaking either "latest stable Kotlin" or "stable only".
- The owner chose to drop detekt:
  - Formatting and style move to ktlint 1.8.0.
  - Correctness checks come from Android Lint and the Kotlin compiler, with warnings treated as errors **[call]**.
  - Our own check enforces the no-comments rule.
- Revisit when detekt 2.x has a stable release.
- The first build task must prove ktlint 1.8.0 runs cleanly on Kotlin 2.4.20 sources, with evidence.

---

## 12. Left to the builder

- The libGDX-in-Compose embedding technique (§5).
- Whether `:data` can be pure JVM. **Answered 2026-10-28: yes, with `datastore-core` (`docs/superpowers/specs/2026-10-28-engine-feel-and-storage-design.md` §3).**
- Exact particle and glow implementation.
- Version code scheme.
- How the no-comments rule is implemented: a small, syntax-aware tokenizer script is preferred over a custom ktlint rule set, for simplicity. It must cover the file types in §7.2 with no false positives on strings or URLs.
- **Unverified risks to prove early, with evidence, in the first task that touches each:**
  - JUnit 6 together with `kotlin-test` in JVM modules under Kover
  - ktlint 1.8.0 on Kotlin 2.4.20
  - Kover reaching 100% branch coverage on Kotlin 2.4 code
  - libGDX rendering on the CI emulator (GLES under SwiftShader)
  - Dependabot behaviour once commits are pushed to its branch
- The order of work. Suggested: repo and hooks, then CI, then the domain engine, then data, then UI and board, then release. Each skill is extracted as its piece lands.

---

## 13. Calls made by the designer (for the owner to check)

1. A 20 × 20 board, a starting length of 3, +1 segment per orb, levels 1–9 using the exact formula in C8, and the Assist speed (§2).
2. Menus, HUD and overlays in Compose. libGDX draws only the board.
3. Four modules: `domain`, `data`, `board`, `app`.
4. No DI framework.
5. Conversation resolution is required before merge, so any AI or human review comment must be dealt with. AI reviews themselves are optional and started by the owner.
6. No comments, enforced by an automated check over the file types listed in §7.2.
7. Controls: swipe, an optional on-screen D-pad, and hardware keys.
8. Settings: difficulty, sound, haptics, control scheme.
9. The top 10 high scores, stored on the device only.
10. Release through release-please: it keeps a release PR open, merging it creates the tag and a draft release, the tag must be on `main`, and releases are immutable once published (owner decision 2026-10-26). Owner decision: no AAB until Play work starts.
11. Skill names: `android-ci`, `git-hooks`, `sdlc-rules`.
12. **Process steps a machine can't fully check.**
    - **Enforced:**
      - Every PR links an issue (a CI check on the PR body for `Closes #n`).
      - Every PR that changes production source in `:domain` or `:data` also changes tests in that module (CI check).
    - **Taught and documented, not enforced:**
      - One worktree per branch.
      - Writing the test first. A missing test is still caught by the coverage gate.
      - Splitting large work into stacks.

      The `sdlc-rules` skill teaches these (§7.3), and `CONTRIBUTING.md` says plainly that they are practices, not gates. Commits in the main checkout are refused by a hook, so worktree use is partly enforced.
13. **Dependabot.**
    - Security alerts and version-update PRs are both on.
    - A Dependabot PR only starts the work. It can't merge until someone (human or agent) has added commits to it that:
      - regenerate the Gradle checksum file
      - update `docs/versions.md`
      - fill in the Evidence section
    - There are no bot exceptions to any gate.
    - `dependabot.yml` settings:
      - `commit-message.prefix: build` with `include: scope`, so its commits pass the Conventional Commits check
      - updates are grouped, to reduce the follow-up work
      - its PRs carry the `dependencies` label, which satisfies the issue-link check
14. **Coverage exclusions.**
    - No exclusion annotations or filters in `:domain` or `:data`.
    - If a branch truly can't be covered, the code is restructured.
    - The first domain task proves the 100% line and branch gate is reachable with Kover on Kotlin 2.4, with evidence.
15. **minSdk 31 [owner].**
    - The animated SplashScreen icon works on every supported device.
    - CI emulator profiles: a phone on API 37, a tablet on API 37, and a phone on API 31.
16. **Accessibility additions.**
    - TalkBack announces milestones only, not every orb.
    - An Assist speed sits below level 1.
