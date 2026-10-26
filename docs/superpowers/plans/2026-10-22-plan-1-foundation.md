# SuperSnake Plan 1: Foundation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development to implement this plan task-by-task (owner's choice). Every subagent runs on model Sonnet 5.5: pass `model: "sonnet"` on each Agent call. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** A public GitHub repo where every SDLC rule in spec §7.2 that applies before game code exists is enforced by a machine and proven with evidence. The CI, hooks and SDLC rules are extracted as a Claude Code plugin in a second repo.

**Architecture:**
- The Android Studio starter is rebuilt into a Gradle composite:
  - `:app`: Android application
  - `:domain`: pure Kotlin JVM
  - `build-logic/checks`: an included Kotlin JVM build holding our own rule checkers, run as Gradle `JavaExec` tasks
- Git hooks and GitHub Actions call the same Gradle tasks, so local and CI agree.
- GitHub rulesets and repo settings are applied with `gh api`.

**Tech Stack:** Gradle 9.8.2, AGP 9.4.1, Kotlin 2.4.20, JDK 25, mise, Compose BOM 2026.09.00, JUnit 6.1.3 + kotlin-test, Kover 0.9.11, ktlint 1.8.0 (CLI artifact), zizmor 1.31.1, Android Lint, CodeQL, GitHub Actions, plain shell git hooks.

**Spec:** `docs/superpowers/specs/2026-10-22-supersnake-design.md`. Approved by the owner on 2026-10-22 at commit `e900708`. Later commits only add owner decisions confirmed on 2026-10-23 (the mockup location, and that Claude reviews run locally); `git log -- docs/superpowers/specs` lists them.

## Start here (for a fresh session)

Together with the spec and `docs/design/neon-arcade-mockup.html`, this plan is self-contained. A new session needs nothing from the brainstorming or planning conversations: every decision made there is written in one of those three files. Statements marked "confirmed by the owner" record decisions the owner made in those conversations. Treat them as settled.

`README.md` (for humans) and `CLAUDE.md` (for agents) at the repo root point here. Both are temporary bootstrap versions, and Task 9 replaces them.

### To start or resume

**AI agent (Claude Code):** open Claude Code in `/Users/r03418/AndroidStudioProjects/SuperSnake` and say:

> Execute `docs/superpowers/plans/2026-10-22-plan-1-foundation.md` with superpowers:subagent-driven-development, starting at the first task that is not done.

**Human:** work through the tasks in order, by hand. Every step gives the exact command and the output that means it passed. The only parts that need Claude Code are the subagent instructions and Task 10 Step 4.

### Read first, in this order

1. This file, all of it.
2. The spec.
3. The mockup, in a browser: the chosen neon look.

### Progress lives in git

- **A task is done** when all of its checkboxes are ticked in this file on `main`. The task's own commit (Task 1) or PR (Tasks 2–10) ticks them, so the ticks land on `main` together with the work.
- **A task is in progress** if any of these exists for it:
  - a worktree (`git worktree list`)
  - a branch named in its last step
  - an open PR (`gh pr list`)

  Continue in that worktree. Ticked steps there are done; re-run the first unticked step's verification before moving on.
- **A task is not started** if none of those exist.
- **Before Task 2**, the repo is local only and `main` is the record. **From Task 2 on**, run `git pull` in the main checkout first, and use `origin/main` as the record.
- **Start from the first task that is not done.** Never skip a task.

### State at handoff (2026-10-23)

- **Repo:** local git repo at `/Users/r03418/AndroidStudioProjects/SuperSnake`, branch `main`, no remote yet.
- **Commits:** documentation only (spec, this plan, the mockup, the bootstrap `README.md`/`CLAUDE.md`, and prerequisite updates). Run `git log --oneline` to see them. No build or code commit exists yet.
- **Next action:** Task 1, Step 1.
- **Untracked Android Studio starter files from 2026-10-20**, which Task 1 consumes:
  - `app/`
  - `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`
  - `gradle/`, `gradlew`, `gradlew.bat`
- **Never commit** `local.properties`, `.idea/`, `.gradle/`, `.superpowers/` or `.claude/settings.local.json`. All five are in `.gitignore`.
- **Local leftovers you can ignore:** `.superpowers/` holds brainstorming mockups, and `.claude/settings.local.json` holds local Claude Code permissions.

### Machine prerequisites

Each was checked on 2026-10-22/23. Re-run the check command and fix anything that differs before Task 1.

| Need | Check | Last seen |
|------|-------|-----------|
| mise | `~/.local/bin/mise --version` | 2026.10.18 ✅ (latest stable, 2026-10-23). Install with `curl -fsSL https://mise.run \| sh` |
| JDK 25 (provided by mise) | `mise exec -- java -version` | 25.0.5, Temurin-25.0.5+8 ✅ |
| Android SDK platform 37 | `ls ~/Library/Android/sdk/platforms` | `android-37.0` revision 2 ✅, installed 2026-10-23. The SDK also offers 37.1 and 37.2, which are not installed and not needed: `compileSdk = 37` needs only 37.0 |
| Android cmdline-tools | `~/Library/Android/sdk/cmdline-tools/latest/bin/sdkmanager --list_installed \| grep cmdline` shows `23.0.0`; `android --version` prints `1.0.16625370` | 23.0 (Android CLI 1.0.16625370) ✅, updated from 20.0 on 2026-10-23. In 23.0, `sdkmanager` is deprecated; use `android sdk install|list|update` instead, for example `android sdk install platforms/android-37.0` |
| Android build-tools 37 | `ls ~/Library/Android/sdk/build-tools` | `37.0.0` ✅ |
| Official GitHub CLI `gh`, logged in as `EastwoodNo1` | `gh --version && gh auth status` | gh 2.103.0 (latest stable, 2026-10-21), provided by mise (`mise exec -- gh --version`). The Homebrew `gh` may remain, but the project uses mise's; account `EastwoodNo1` ✅ |
| `gh-stack` extension | `gh stack --version` | 0.2.1 ✅ (latest stable, released 2026-10-09), installed 2026-10-23. Task 7's `bootstrap.sh` still installs it when missing, for other machines |
| git ≥ 2.36 | `git --version` | 2.56.0 ✅ |
| GitHub Copilot CLI (optional; used only for the owner's own optional reviews, not required by any task) | `copilot --version` | 1.0.103 ✅ (latest stable, 2026-10-22), installed 2026-10-23 with `brew install --cask copilot-cli`, one of the methods on docs.github.com/en/copilot/how-tos/set-up/install-copilot-cli. Needs a Copilot subscription and a one-time `/login` |

### How the owner works

Carry these into every task. They are also in the spec.

- The owner works **solo**.
- **Evidence for every claim:** the command and its real output, or a CI link. Never "should work".
- **No comments in code.** Why and what go in commit messages.
- **Only the latest stable versions**, re-verified with a source on the day they're used.
- **Minimal third-party dependencies.**
- **Plan approval covers creating the two public repos** in Tasks 2 and 10, and the GitHub settings in this plan.
- **Claude reviews are run locally from Claude Code.** Do not install the Claude GitHub App or the Claude Code GitHub Action, and don't add an Anthropic API key secret.
- **Stop and ask the owner before:**
  - anything outward-facing that this plan doesn't list
  - deleting anything that isn't in the plan
  - any change to a decision in the spec

### Confirmed at plan review

All three were confirmed by the owner on 2026-10-23.

- **App ID:** `applicationId`/`namespace` = `io.github.eastwoodno1.supersnake`. It is permanent from the first signed release.
- **Repos:** `EastwoodNo1/SuperSnake` and `EastwoodNo1/android-sdlc-skills`, both public.
- **Execution:** superpowers:subagent-driven-development.
  - **Every subagent** (implementer, reviewer, or any other dispatched agent) **must run on model Sonnet 5.5.** In practice, pass `model: "sonnet"` on every Agent tool call; that alias resolves to `claude-sonnet-5-5`.
  - The orchestrating session may run on any model.

### After Plan 1 is merged

- Start a fresh session and use superpowers:writing-plans to write Plan 2 (Game engine) from the spec and the Roadmap below.
- Re-verify versions as part of that.
- Give Plan 2 the same "Start here" section, updated with the state at that point.

## Roadmap (this plan is 1 of 4)

The spec covers four separable deliverables. Each gets its own plan, written when the previous one is merged, so versions are re-verified at that time.

1. **Foundation (this plan).** Repo, build skeleton, quality gates, rule checkers, hooks, CI, GitHub rules, onboarding docs, plugin repo v0.1.0.
2. **Game engine.**
   - `:domain`: rules S1–S5 and C1–C12, input classification, render maths, use cases.
   - `:data`: DataStore repositories.
   - All at 100% coverage.
3. **App.**
   - Neon Compose UI, splash, libGDX `:board` embedded in Compose, controls, accessibility.
   - UI tests on 3 emulator profiles, the 16 KB alignment check, final manifest hardening, assets.
   - `android-ci` skill gains the UI-test job.
4. **Release.**
   - Signing key (ADR-0005), tag ruleset, `release` environment, immutable releases, release workflow with attestation, `docs/release.md`, first `v1.0.0`.
   - `android-ci` skill gains the release workflow.

## Global Constraints

These are copied from the spec and apply to every task.

**Code and commits**
- **No comments** in `*.kt`, `*.kts`, `*.xml`, `*.toml`, `*.yml`, `*.yaml`, `*.sh`, `*.pro`, `*.properties`. A shebang on line 1 is allowed. Generated files are included: strip comments that tools write.
- **Commits:**
  - Conventional Commits.
  - A body is required for `feat`, `fix`, `refactor` and `perf`, and should be added to the others whenever there is a why to say.
  - The body states **Why:** and **What:**.
  - When an agent wrote a commit, it ends with a `Co-Authored-By:` trailer naming the model that actually wrote it, using the attribution line that Claude Code's system reminder gives in that session. Subagents run on Sonnet 5.5, so their commits use `Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>`. Never copy another model's line.
- **Evidence:**
  - Every "passes / works / done" claim in a PR or a report includes the exact command and its real output, or a CI run link.
  - Never infer a result.

**Versions and dependencies**
- **Versions** (verified 2026-10-22, spec §11):

  | Item | Version |
  |------|---------|
  | Gradle | 9.8.2 (distribution SHA-256 `21bd5da2de49b642904077f38c0efdc7778e92b3ed0889392dca61b97e389607`) |
  | AGP | 9.4.1 |
  | Kotlin | 2.4.20 (capped by CodeQL) |
  | Compose BOM | 2026.09.00 |
  | activity-compose | 1.13.0 |
  | core-ktx | 1.19.1 |
  | lifecycle | 2.11.0 |
  | Kover | 0.9.11 |
  | JUnit BOM | 6.1.3 |
  | JUnit 4 | 4.13.2 |
  | androidx.test ext-junit | 1.3.0 |
  | espresso | 3.7.0 |
  | ktlint | 1.8.0 |
  | zizmor | 1.31.1 |

  If a newer **stable** version exists on the day a task adds a dependency, re-verify, use it (Kotlin only up to the CodeQL-supported maximum), and record it in `docs/versions.md` with source URL and date.
- **Third-party:** nothing beyond ktlint, zizmor (owner-approved 2026-10-23, §6 evidence in the spec) and `ReactiveCircus/android-emulator-runner` (Plan 3). No Gradle plugins other than AGP, Kotlin and Kover. The scaffold's `org.gradle.toolchains.foojay-resolver-convention` is removed.

**Android**
- **Android:**
  - `applicationId` and `namespace` are `io.github.eastwoodno1.supersnake` **[owner-confirmed 2026-10-23; permanent once released]**
  - minSdk 31, compileSdk and targetSdk 37
  - JVM toolchain 25
  - Kotlin `allWarningsAsErrors = true`
  - no `INTERNET` permission
- **Packages:**
  - `io.github.eastwoodno1.supersnake.domain` for `:domain`
  - `io.github.eastwoodno1.supersnake` for `:app`
  - `sdlc.checks` for `build-logic/checks` (kept project-neutral so the plugin can ship it unchanged)

**Process**
- **Coverage:** 100% line and branch on `:domain` and `build-logic/checks`, with no exclusions. Any Kotlin code that holds a decision belongs in a gated module.
- **From Task 2 on, every task gets its own GitHub issue and is done in its own worktree on a branch `<type>/<name>`.** It merges through a PR with squash merge only; the PR title is a Conventional Commit header and becomes the subject on main. The PR body contains `Closes #<issue>` and a filled `## Evidence` section.
- **GitHub:**
  - owner `EastwoodNo1` (the authenticated `gh` account)
  - repos `EastwoodNo1/SuperSnake` and `EastwoodNo1/android-sdlc-skills` **[owner-confirmed 2026-10-23]**
  - both public
  - both names were verified free on 2026-10-22
- **Gradle deprecations [owner-decided 2026-10-23]:** wherever this plan says `--warning-mode=fail`, run the command through `scripts/gradle-strict.sh` instead. AGP 9.4.1, the latest stable, emits one deprecation (`Configuration.setVisible(boolean)`, from `BasePlugin.createAndroidJdkImageConfiguration`), so `--warning-mode=fail` fails every cold build. The script allows exactly that message and fails on any other deprecation. Remove the allowlist when a stable AGP no longer emits it.
- **Tools via mise [owner-decided 2026-10-23]:**
  - Dev tools (JDK, gh) are installed and run through mise, not system installs.
  - Every tool is SHA-pinned in a committed `mise.lock` and installs only with `mise install --locked`. Provenance is verified where mise can: gh via GitHub attestations. Temurin 25.0.5+8 is SHA pin only (GPG-signed, which mise can't verify for Java), cross-checked against the Adoptium API and the `.sha256.txt` assets.
  - The Android SDK stays outside mise (vfox-android-sdk has 4 stars and skips checksums; sdkmanager checks SHA-1).
  - Every Gradle and gh command runs under mise: an activated shell or `mise exec --`.
  - Ruling R9: CI installs the same pinned mise, with its release archive SHA-256 pinned, and runs `mise install --locked` instead of `actions/setup-java`.
  - Locked platforms are macos-arm64, linux-x64 and linux-arm64. Intel macOS is unsupported because Adoptium publishes no macOS x64 JDK 25.0.5+8.

## Review Focus

1. **No-comments false positives.** Comment markers inside strings or URLs must not be flagged, for example:
   - `"https://x"` and `"/* not */"` in Kotlin
   - `url: a#b` and `key: '#fff'` in YAML
   - `$#` and `${#v}` in shell

   Otherwise a legitimate commit is blocked. Pinned in Task 4.
2. **Comments written by tools.**
   - `./gradlew wrapper` and `updateDaemonJvm` write a `#` timestamp line into `.properties` files, and Android Studio writes XML comments.
   - Expected: the check flags them, with the file and line, and CONTRIBUTING says to strip them.
   - Pinned in Task 1 (strip) and Task 4 (properties test).
3. **Git's own commit messages.**
   - `Revert "feat: x"` (from `git revert`), `fixup!`/`squash!` commits, and a body made only of trailers.
   - Expected:
     - the default revert message is rejected with a hint to use `revert:`
     - `fixup!` is allowed by the local hook but rejected in CI
     - a trailers-only body does not count as a body
   - Pinned in Task 5.
4. **Hooks during rebase in a worktree.** `git rebase` in a linked worktree runs pre-commit on a detached HEAD. That must not be treated as "on main" or as "the main checkout". Pinned in Task 7.
5. **A PR body that only repeats the template.** An `## Evidence` heading followed only by the template's placeholder text must fail. Pinned in Task 5.

---

### Task 1: Rebuild the scaffold into the SuperSnake skeleton

**Files:**
- Delete:
  - `app/src/main/java/MainActivity.kt`
  - `app/src/main/java/com/example/**`
  - `app/src/test/java/com/example/**`
  - `app/src/androidTest/java/com/example/**`
- Rewrite with no comments: `app/src/main/res/xml/backup_rules.xml`, `app/src/main/res/xml/data_extraction_rules.xml`
- Modify:
  - `settings.gradle.kts`
  - `build.gradle.kts`
  - `app/build.gradle.kts`
  - `app/proguard-rules.pro`
  - `app/src/main/AndroidManifest.xml`
  - `app/src/main/res/values/*.xml`
  - `gradle.properties`
  - `gradle/libs.versions.toml`
  - `gradle/wrapper/gradle-wrapper.properties`
  - `gradle/gradle-daemon-jvm.properties`
- Create:
  - `domain/build.gradle.kts`
  - `app/src/main/kotlin/io/github/eastwoodno1/supersnake/MainActivity.kt`
  - `docs/versions.md`
- Do not commit: `.idea/`, `local.properties`, `.gradle/` (already ignored, or add `/.idea/` to `.gitignore`).

**Interfaces:**
- Produces:
  - Gradle projects `:app` and `:domain`
  - version catalog aliases `libs.plugins.android.application`, `libs.plugins.kotlin.jvm`, `libs.plugins.kotlin.compose`, `libs.plugins.kover`
  - the class `io.github.eastwoodno1.supersnake.MainActivity : ComponentActivity`

- [x] **Step 1: Upgrade the wrapper**

  Run: `./gradlew wrapper --gradle-version 9.8.2 --gradle-distribution-sha256-sum 21bd5da2de49b642904077f38c0efdc7778e92b3ed0889392dca61b97e389607`, then run it again so the wrapper jar is regenerated by 9.8.2.

  Expected: `gradle/wrapper/gradle-wrapper.properties` contains `gradle-9.8.2-bin.zip`.

  Delete any `#` timestamp line that the tool writes.

- [x] **Step 2: Rewrite the catalog and the build files**
  - `libs.versions.toml` holds exactly the Global Constraints versions that Plan 1 uses.
  - `settings.gradle.kts` includes `:app` and `:domain`, has no foojay plugin, and uses `FAIL_ON_PROJECT_REPOS`.
  - `:domain` uses `kotlin("jvm")` with `jvmToolchain(21)`.
  - `:app` uses AGP:
    - `namespace`/`applicationId` = `io.github.eastwoodno1.supersnake`
    - `minSdk = 31`, `compileSdk = 37`, `targetSdk = 37`
    - `isMinifyEnabled = true` and `isShrinkResources = true` for release
    - Java 21
    - depends on `:domain`
  - Every module sets `compilerOptions { allWarningsAsErrors = true }`.
  - `gradle.properties` keeps only these keys, with no comment lines:
    - `org.gradle.jvmargs`
    - `kotlin.code.style=official`
    - `org.gradle.configuration-cache=true`
    - `org.gradle.parallel=true`

- [x] **Step 3: Write a minimal `MainActivity`**
  - It calls `setContent { }` with a single `Text(stringResource(R.string.app_name))`.
  - `AndroidManifest.xml` has:
    - one exported launcher activity
    - `android:allowBackup="true"`
    - `android:dataExtractionRules="@xml/data_extraction_rules"` and `android:fullBackupContent="@xml/backup_rules"`; both files include only the `file` domain path `datastore/` (concrete DataStore files come in Plan 2), so Lint's backup checks pass from the start
    - no permissions
  - Remove every XML comment from `res/` and the comment lines from `proguard-rules.pro`. The file may be empty.
  - Delete the template themes, colours and Example tests.

- [x] **Step 4: Verify the build**

  Run: `scripts/gradle-strict.sh build`

  Expected: `BUILD SUCCESSFUL`. Paste the last 20 lines into the task report as evidence.

- [x] **Step 5: Verify that nothing in the tracked files contains a comment marker yet**

  Run: `git ls-files -co --exclude-standard | grep -E '\.(kt|kts|xml|toml|properties|pro)$' | xargs grep -nE '^\s*(#|//|<!--)'`

  Expected: no output (exit 1).

- [x] **Step 6: Write `docs/versions.md`**

  One table row per version: item, version, release date, source URL, verified on.

- [x] **Step 7: Commit (the bootstrap commit, directly on `main`, because the remote and its rules don't exist yet)**

  ```bash
  git add -A && git status --short
  git commit
  ```

  Subject: `build: rebuild starter into SuperSnake skeleton`. The body says why: the starter used outdated versions, a placeholder package, and a third-party toolchain plugin.

---

### Task 2: Create the GitHub repo and the baseline rules

**Files:**
- Create:
  - `SECURITY.md`
  - `.github/pull_request_template.md`
  - `scripts/github/apply-repo-settings.sh`
  - `scripts/github/expected-settings.json`

**Interfaces:**
- Produces:
  - remote `origin` = `https://github.com/EastwoodNo1/SuperSnake`
  - the ruleset named `main-protection`
  - the PR template sections `## Why`, `## What`, `Closes #`, `## Evidence`
  - the label `dependencies`

- [ ] **Step 1: Write `scripts/github/apply-repo-settings.sh`**

  It takes `<owner/repo>` and idempotently applies the settings below using `gh api`. Before writing each call, look up the endpoint in the GitHub REST docs and record the doc URL in the PR evidence.
  - **Repo settings:**
    - `allow_squash_merge=true`, `allow_rebase_merge=false`, `allow_merge_commit=false`
    - `squash_merge_commit_title="PR_TITLE"`, `squash_merge_commit_message="COMMIT_MESSAGES"`
    - `delete_branch_on_merge=true`
    - secret scanning and push protection enabled
  - **Security features:**
    - Dependabot alerts on
    - private vulnerability reporting on
  - **Actions:**
    - default workflow token permissions `read`
    - Actions can't approve PRs
    - fork PR workflows need approval for all external contributors
  - **Ruleset `main-protection`** on `~DEFAULT_BRANCH`, with an empty bypass list:
    - `deletion`
    - `non_fast_forward`
    - `required_linear_history`
    - `pull_request` with 0 required approvals, `required_review_thread_resolution: true`, `allowed_merge_methods: ["squash"]`
    - required status checks are added in Task 8
  - **Label:** `dependencies`

- [ ] **Step 2: Write `scripts/github/expected-settings.json`**

  It holds the expected values. The script ends by reading every setting back with GET and exits non-zero if anything differs.

- [ ] **Step 3: Create the repo and push the bootstrap commit**

  ```bash
  gh repo create EastwoodNo1/SuperSnake --public --source . --remote origin --push
  ```

  Then run `scripts/github/apply-repo-settings.sh EastwoodNo1/SuperSnake`.

  Expected: exit 0, and the readback prints `all settings match`.

- [ ] **Step 4: Prove that a direct push is refused**

  From a scratch commit on `main`: `git push origin main`.

  Expected: rejected with a message citing the ruleset. Paste the output, then run `git reset --hard origin/main`.

- [ ] **Step 5: Branch, commit, open the issue and the PR, merge**
  - Create a worktree `../SuperSnake-chore-repo-rules` with branch `chore/repo-rules`, and commit `SECURITY.md`, the PR template and the scripts there.
  - Subject: `chore: add repo security policy, PR template and settings script`.
  - Open an issue, "Repo rules, security policy and PR template".
  - `gh pr create`, with a body containing `Closes #<that issue>` and the Evidence from Steps 3–4.
  - `gh pr merge --squash`. The PR title equals the commit subject.

  Expected: merged. `git log origin/main --oneline` shows one commit with the PR title as its subject, and its body holds the commit message.

---

### Task 3: Quality gates (ktlint, Lint, Kover) proven on the first domain type

**Files:**
- Create:
  - `.editorconfig`
  - `domain/src/main/kotlin/io/github/eastwoodno1/supersnake/domain/Direction.kt`
  - `domain/src/test/kotlin/io/github/eastwoodno1/supersnake/domain/DirectionTest.kt`
  - `gradle/ktlint.gradle.kts`
- Modify:
  - `build.gradle.kts`
  - `domain/build.gradle.kts`
  - `app/build.gradle.kts`
  - `gradle/libs.versions.toml`
  - `docs/versions.md`

**Interfaces:**
- Produces:
  - `enum class Direction(val dx: Int, val dy: Int) { UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0); val opposite: Direction }`. The y axis grows downward.
  - root tasks `ktlintCheck`, `ktlintFormat`, `koverVerify`
  - `./gradlew check` runs ktlint, Lint, unit tests and `koverVerify`

- [ ] **Step 1: Write the failing test `DirectionTest`**

  It uses JUnit 6 `@Test` and `kotlin.test` assertions:

  ```kotlin
  @Test fun oppositeOfEachDirection() {
      assertEquals(Direction.DOWN, Direction.UP.opposite)
      assertEquals(Direction.UP, Direction.DOWN.opposite)
      assertEquals(Direction.RIGHT, Direction.LEFT.opposite)
      assertEquals(Direction.LEFT, Direction.RIGHT.opposite)
  }
  @Test fun oppositeIsInvolutive() { Direction.entries.forEach { assertEquals(it, it.opposite.opposite) } }
  @Test fun vectorsAreUnitSteps() { assertEquals(listOf(0 to -1, 0 to 1, -1 to 0, 1 to 0), Direction.entries.map { it.dx to it.dy }) }
  ```

- [ ] **Step 2: Run the test and confirm it fails**

  Run: `./gradlew :domain:test`

  Expected: compilation fails, with `Unresolved reference 'Direction'`.

- [ ] **Step 3: Implement `Direction`**

  Use `useJUnitPlatform()`, with `testImplementation(platform(junit-bom 6.1.3))`, `junit-jupiter`, `kotlin("test-junit5")`, and `testRuntimeOnly junit-platform-launcher`.

- [ ] **Step 4: Run the test and confirm it passes**

  Run: `./gradlew :domain:test`

  Expected: `BUILD SUCCESSFUL`, with 3 tests in `domain/build/test-results/test/*.xml` and `failures="0"`.

  **Evidence for spec §12 risk "JUnit 6 + kotlin-test":** include that XML line in the report.

- [ ] **Step 5: Add Kover**
  - Apply Kover to `:domain`, and aggregate it at the root.
  - Add a verify rule on `:domain`: `minBound(100, LINE)` and `minBound(100, BRANCH)`, with no filters.
  - Run `./gradlew koverVerify koverLog`.
  - Expected: success, and the log shows 100% line and branch for `:domain`.
  - Then prove the gate bites: temporarily add an untested branch to `Direction`, run `koverVerify`, and paste the FAILED output. Revert the branch.

- [ ] **Step 6: Add ktlint without a third-party Gradle plugin**
  - In `gradle/ktlint.gradle.kts`, add a `ktlint` configuration that depends on `libs.versions.toml`'s `ktlint-cli` library (version `ktlint`, 1.8.0).
  - `ktlintCheck` is a `JavaExec` with `mainClass = "com.pinterest.ktlint.Main"` and, as args, the `.kt` and `.kts` files that exist in the `git ls-files -z -co --exclude-standard` list shared with `checkNoComments` (explicit relative paths, so ktlint never walks `build/` directories). The `--sun-misc-unsafe-memory-access=allow` jvmArg is defined once.
  - `ktlintFormat` is the same task plus `-F`.
  - `.editorconfig`: `root = true`, then `[*.{kt,kts}]` with `ktlint_code_style = ktlint_official`.
  - Wire `ktlintCheck` into `check`.
  - Run `./gradlew ktlintCheck`. Expected: success.
  - Prove it bites: add a bad indent, run it, paste the failure, revert.
  - **Evidence for spec §12 risk "ktlint 1.8.0 on Kotlin 2.4.20".**

- [ ] **Step 7: Configure Android Lint**
  - `lint { warningsAsErrors = true; abortOnError = true; checkDependencies = true }`, with no baseline file.
  - Run: `./gradlew :app:lintDebug`. Expected: `0 errors, 0 warnings`.
  - If Lint flags Kotlin 2.4.21 as newer, that is the deliberate CodeQL cap (ADR-0006). Report it with the exact issue id and stop for a decision. Do not disable checks on your own.

- [ ] **Step 8: Run the whole gate**

  Run: `./gradlew check --warning-mode=fail`

  Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 9: Commit, open the issue and the PR, merge**

  Worktree with branch `build/quality-gates`. Subjects:
  - `feat(domain): add Direction with opposite`
  - `build: add Kover 100% gate, ktlint CLI and strict Lint`

  Merge via PR with `--squash`, with Evidence (Steps 4, 5, 6 and 8). The PR title is the first subject above.

---

### Task 4: `build-logic/checks` and the no-comments checker

**Files:**
- Create:
  - `build-logic/checks/settings.gradle.kts`
  - `build-logic/checks/build.gradle.kts`
  - `build-logic/checks/src/main/kotlin/sdlc/checks/Finding.kt`
  - `build-logic/checks/src/main/kotlin/sdlc/checks/NoComments.kt`
  - `build-logic/checks/src/main/kotlin/sdlc/checks/Cli.kt`
  - `build-logic/checks/src/test/kotlin/sdlc/checks/NoCommentsTest.kt`
  - `build-logic/checks/src/test/kotlin/sdlc/checks/CliTest.kt`
- Modify:
  - `settings.gradle.kts`, adding `includeBuild("build-logic/checks")`
  - `build.gradle.kts`, adding a `checks` configuration and a `checkNoComments` task wired into `check`

**Interfaces:**
- Produces:
  - `data class Finding(val path: String, val line: Int, val message: String)`. Printed as `path:line: message`.
  - `object NoComments { fun scan(path: String, content: String): List<Finding> }`. Returns an empty list for file types outside the scope.
  - `object Cli { fun run(args: List<String>, readFile: (String) -> String, out: (String) -> Unit): Int }`:
    - returns exit 0 when no findings, 1 when findings, 2 on usage error
    - subcommand `no-comments <path...>`
    - `fun main(args: Array<String>)` calls `exitProcess(Cli.run(...))`
  - root task `checkNoComments`: a `JavaExec` over `git ls-files -co --exclude-standard` filtered to the in-scope extensions

- [ ] **Step 1: Write the failing tests `NoCommentsTest`**

  Each case asserts the exact list of `Finding` lines.

  **Kotlin and `.kts`:**

  | Case | Input | Expected |
  |------|-------|----------|
  | Line comment flagged | `val a = 1 // x` | `[1]` |
  | Block comment flagged | `/* x */` | `[1]` |
  | KDoc flagged | `/** x */` | `[1]` |
  | Nested block | `/* a /* b */ c */ val x = 1` | `[1]` (one finding, the code after it is clean) |
  | Strings ignored | `val u = "https://x/*y*/"` | `[]` |
  | Raw string ignored | `val r = """ // not """` | `[]` |
  | Template ignored | `val t = "${ "a//b" }"` | `[]` |
  | Char literal ignored | `val c = '/'` | `[]` |

  **XML:**

  | Case | Input | Expected |
  |------|-------|----------|
  | Comment flagged | `<a/><!-- x -->` | `[1]` |
  | CDATA ignored | `<a><![CDATA[<!-- -->]]></a>` | `[]` |
  | Attribute ignored | `<a b="<!--"/>` | `[]` |

  **Hash languages** (`.toml`, `.yml`, `.yaml`, `.sh`, `.pro`):

  | Case | Input | Expected |
  |------|-------|----------|
  | Line start flagged | `# x` | `[1]` |
  | After whitespace flagged | `a = 1 # x` | `[1]` |
  | Inside a word ignored | `url: a#b` | `[]` |
  | Single quotes ignored | `c: '#fff'` | `[]` |
  | Double quotes ignored | `c: "#fff"` | `[]` |
  | Shell `$#` ignored | `echo $#` | `[]` |
  | Shell `${#v}` ignored | `echo ${#v}` | `[]` |
  | Shebang on line 1 allowed | `#!/bin/sh` | `[]` |
  | Shebang on line 2 flagged | `#!/bin/sh` on line 2 | `[2]` |

  **`.properties`:**

  | Case | Input | Expected |
  |------|-------|----------|
  | `#` at line start flagged | `#Tue Oct 20` | `[1]` |
  | `!` at line start flagged | `! x` | `[1]` |
  | `#` inside a value ignored | `k=v#1` | `[]` |

  **Out of scope:** `scan("README.md", "<!-- x -->")` gives `[]`.

  **Multi-line:** a finding's line number is the line where the comment starts.

- [ ] **Step 2: Run the tests and confirm they fail**

  Run: `./gradlew -p build-logic/checks test`

  Expected: compilation fails, with `Unresolved reference 'NoComments'`.

- [ ] **Step 3: Implement `NoComments.scan`**

  It's a single-pass character state machine per language family:
  - **Kotlin:** states CODE, LINE_COMMENT, BLOCK(depth), STRING, RAW_STRING, CHAR, plus a template-brace depth stack for `${`.
  - **XML:** CODE, ATTR_VALUE(quote), CDATA, COMMENT.
  - **Hash:** a `#` is a comment only outside quotes and when it is at line start or after whitespace.
  - **Properties:** only the first non-blank character counts.

  Emit one finding per comment, at its start line. Message: `comment not allowed (why/what belong in the commit message)`.

- [ ] **Step 4: Run the tests and confirm they pass**

  Run: `./gradlew -p build-logic/checks test`

  Expected: all pass.

- [ ] **Step 5: Write `CliTest` and implement `Cli`**
  - Exit codes: 0 / 1 / 2.
  - Unknown subcommand: prints usage and exits 2.
  - Findings are printed in `path:line: message` form.

  Run the tests. Expected: pass.

- [ ] **Step 6: Add Kover 100% line and branch to `build-logic/checks`**

  Run `./gradlew -p build-logic/checks koverVerify`. Expected: success.

- [ ] **Step 7: Wire it in and prove it**
  - Run `./gradlew checkNoComments`. Expected: success on the repo.
  - Add `// x` to `Direction.kt` and run again. Expected: exit 1, printing `domain/src/main/kotlin/.../Direction.kt:<n>: comment not allowed ...`.
  - Revert.
  - Paste both outputs as evidence.

- [ ] **Step 8: Commit and PR**

  Branch `feat/no-comments-check`.

  Subject: `feat(checks): add syntax-aware no-comments checker`

---

### Task 5: Commit message, PR body and tests-changed checkers

**Files:**
- Create:
  - `build-logic/checks/src/main/kotlin/sdlc/checks/CommitMessage.kt`
  - `build-logic/checks/src/main/kotlin/sdlc/checks/PullRequestBody.kt`
  - `build-logic/checks/src/main/kotlin/sdlc/checks/TestsChanged.kt`
  - a matching `*Test.kt` for each
- Modify:
  - `Cli.kt`, `CliTest.kt`
  - `build.gradle.kts`, adding tasks `checkCommitMessage`, `checkCommitMessages`, `checkPullRequest`

**Interfaces:**
- Consumes: `Finding`, `Cli` from Task 4.
- Produces:
  - `enum class CommitContext { LOCAL, CI }`
  - `object CommitMessage { fun validate(message: String, context: CommitContext): List<String>; fun validateTitle(title: String): List<String> }`
  - `object PullRequestBody { fun validate(body: String, labels: Set<String>): List<String> }`
  - `object TestsChanged { fun validate(changedPaths: List<String>, gatedModules: Set<String>): List<String> }`
  - CLI subcommands:
    - `commit-message <file>` (LOCAL)
    - `commit-messages <file>` (CI; messages separated by `\u0000`)
    - `pr-body <bodyFile> <labelsFile>`
    - `pr-title <titleFile>`
    - `tests-changed <pathsFile> <module...>`
  - Gradle tasks:
    - `checkCommitMessage -PmessageFile=`
    - `checkCommitMessages -PmessagesFile=`
    - `checkPullRequest -PtitleFile= -PbodyFile= -PlabelsFile= -PpathsFile=`, which runs `pr-title`, `pr-body` and `tests-changed` with modules `domain`, `data`

- [ ] **Step 1: Write the failing tests**

  `CommitMessageTest` (each case asserts empty vs non-empty, plus a key error substring):

  | Case | Message | Context | Expected |
  |------|---------|---------|----------|
  | Valid with body | `feat(domain): add x` + blank line + `Why: ...` | — | `[]` |
  | Header format | `Feat: x` | — | error contains `type must be one of build, chore, ci, docs, feat, fix, perf, refactor, revert, style, test` |
  | Body required | `fix: x` with no body | — | error contains `body required` |
  | Trailers-only body doesn't count | `feat: x` + blank + `Co-Authored-By: A <a@b>` | — | error contains `body required` |
  | `chore` needs no body | `chore: bump` | — | `[]` |
  | Header length | header longer than 72 characters | — | error contains `72` |
  | Default revert | `Revert "feat: x"` | — | error contains `use "revert:"` |
  | `fixup!` | `fixup! feat: x` | LOCAL | `[]` |
  | `fixup!` | `fixup! feat: x` | CI | error contains `autosquash` |
  | Breaking change | `feat!: x` + body | — | `[]` |

  `CommitMessageTest` title rows (`validateTitle` applies the header rules only: type list, 64 characters so that GitHub's squash suffix ` (#NNNNN)` keeps the subject within 72, no `Revert "`, no `fixup!`):

  | Case | Title | Expected |
  |------|-------|----------|
  | Valid title | `feat(domain): add x` | `[]` |
  | Invalid title | `Feat: x` | error contains `type must be one of` |
  | 64-character title | `feat: ` + 58 × `x` | `[]` |
  | 65-character title | `feat: ` + 59 × `x` | error contains `64` |

  `PullRequestBodyTest`:

  | Case | Body | Labels | Expected |
  |------|------|--------|----------|
  | Valid | contains `Closes #12` and `## Evidence` followed by a fenced block | — | `[]` |
  | No issue link | no `Closes #n` | none | error `link an issue` |
  | Dependabot | no `Closes #n` | `dependencies` | no link error |
  | Heading inside a fence | Evidence fenced block contains the line `## main...origin/main` | — | `[]` |
  | Template placeholder | Evidence section contains only the placeholder text from `.github/pull_request_template.md` | — | error `Evidence` |
  | Missing Evidence | no Evidence heading | — | error `Evidence` |

  The placeholder string is a constant shared with the template. `Fixes #n` and `Resolves #n` (case-insensitive) also count as issue links.

  `TestsChangedTest`:

  | Case | Changed paths | Expected |
  |------|---------------|----------|
  | Missing test change | `domain/src/main/kotlin/A.kt` | error naming `domain` |
  | With test change | `domain/src/main/kotlin/A.kt` + `domain/src/test/kotlin/ATest.kt` | `[]` |
  | Ungated module | `app/src/main/kotlin/X.kt` | `[]` |
  | Docs only | `docs/x.md` | `[]` |

- [ ] **Step 2: Run the tests and confirm they fail**

  Run: `./gradlew -p build-logic/checks test`

  Expected: unresolved references.

- [ ] **Step 3: Implement the three validators and the CLI subcommands**

  The header grammar is `^(build|chore|ci|docs|feat|fix|perf|refactor|revert|style|test)(\([a-z0-9-]+\))?!?: \S.*$`.

  The body is everything after the first blank line, minus trailing `Token: value` trailer lines.

- [ ] **Step 4: Run the tests and coverage**

  Run: `./gradlew -p build-logic/checks test koverVerify`

  Expected: pass, 100%.

- [ ] **Step 5: Commit and PR**

  Branch `feat/process-checks`.

  Subject: `feat(checks): add commit message, PR body and tests-changed checks`

---

### Task 6: Workflow-safety and manifest-hardening checkers

**Files:**
- Create:
  - `build-logic/checks/src/main/kotlin/sdlc/checks/WorkflowSafety.kt`
  - `build-logic/checks/src/main/kotlin/sdlc/checks/ManifestHardening.kt`
  - a matching `*Test.kt` for each
- Modify:
  - `Cli.kt`, `CliTest.kt`
  - `build.gradle.kts`, adding tasks `checkWorkflows` (all `.github/workflows/*.yml`) and `checkReleaseManifest`. `checkReleaseManifest` depends on `:app:processReleaseMainManifest` and reads the merged manifest that the task outputs.

**Interfaces:**
- Produces:
  - `object WorkflowSafety { fun scan(path: String, content: String): List<Finding> }`
  - `object ManifestHardening { fun validate(mergedManifestXml: String): List<String> }`. Uses the JDK `javax.xml.parsers`, with no new dependency; external entities are disabled.
  - CLI subcommands `workflows <path...>` and `manifest <path>`

- [ ] **Step 1: Write the failing tests**

  `WorkflowSafetyTest`, one finding each:

  | Case | Input | Expected finding message |
  |------|-------|--------------------------|
  | Unsafe trigger | `on: pull_request_target` | `forbidden trigger` |
  | Unsafe trigger | `workflow_run:` | `forbidden trigger` |
  | Interpolation in `run:` | `run: echo "${{ github.event.pull_request.body }}"` | `pass event data through env` |
  | Interpolation in a `run: \|` block line | `${{ github.event.issue.title }}` on a line of the block | `pass event data through env` |
  | Unpinned action | `uses: actions/checkout@v7` | `pin to a full commit SHA` |
  | Missing permissions | no top-level `permissions: {}` | `top-level permissions: {}` |

  Also assert:
  - `uses: actions/checkout@<40 hex> # v7.0.1` is not flagged for pinning. The no-comments check still flags the `#` comment, so pinned versions are recorded in `docs/versions.md` instead.
  - `env:` mapping `BODY: ${{ github.event.pull_request.body }}` is not flagged.

  `ManifestHardeningTest`:
  - A valid minimal manifest (one launcher activity with `exported=true`, `allowBackup=true`, `dataExtractionRules` and `fullBackupContent` set) gives `[]`.
  - Each violation gives exactly one error:
    - `uses-permission android.permission.INTERNET` → `INTERNET`
    - `android:debuggable="true"` → `debuggable`
    - a second activity with `exported="true"` and no launcher filter → `exported`
    - `allowBackup="false"` → `allowBackup`
    - missing `dataExtractionRules` → `dataExtractionRules`
    - an XML with a DOCTYPE external entity → an error, with no file read

- [ ] **Step 2: Run the tests and confirm they fail**

  Run: `./gradlew -p build-logic/checks test`

  Expected: unresolved references.

- [ ] **Step 3: Implement both checkers**
  - **WorkflowSafety** is line-based:
    - track whether the current line is inside a `run:` scalar or block, by indentation
    - flag `${{ github.event.` inside it
    - `uses:` values must match `@[0-9a-f]{40}$`, ignoring trailing whitespace; local `./` actions are allowed
  - **ManifestHardening** parses with `FEATURE_SECURE_PROCESSING` and `disallow-doctype-decl`.

- [ ] **Step 4: Run the tests and coverage**

  Run: `./gradlew -p build-logic/checks test koverVerify`

  Expected: pass, 100%.

- [ ] **Step 5: Run the check on the real manifest**

  The backup rule files already exist from Task 1.

  Run: `./gradlew checkReleaseManifest`

  Expected: success.

  Prove it bites: temporarily add the INTERNET permission, run, paste the failure, revert.

- [ ] **Step 6: Commit and PR**

  Branch `feat/workflow-manifest-checks`.

  Subject: `feat(checks): add workflow safety and manifest hardening checks`

---

### Task 7: Git hooks and bootstrap

**Files:**
- Create:
  - `.githooks/pre-commit`
  - `.githooks/commit-msg`
  - `.githooks/pre-push`
  - `.githooks/lib.sh`
  - `scripts/bootstrap.sh`
  - `scripts/test-hooks.sh`

**Interfaces:**
- Consumes: Gradle tasks `checkNoComments`, `ktlintCheck`, `checkCommitMessage`, `check`.
- Produces:
  - `scripts/bootstrap.sh`: sets `core.hooksPath=.githooks`, checks mise (at least 2026.10.18), runs `mise install --locked`, re-runs the Temurin SHA cross-check, checks the pinned Android SDK packages are installed, and installs the `gh-stack` extension through mise's `gh` if missing
  - `lib.sh` functions `is_main_checkout` and `on_main_branch`

- [ ] **Step 1: Write `scripts/test-hooks.sh`**

  It creates a temporary clone with a linked worktree, then asserts each case's exit code and that stderr contains the stated text.

  | Case | Action | Expected |
  |------|--------|----------|
  | (a) | commit in the main checkout | refused, `use a worktree` |
  | (b) | commit on branch `main` in a worktree | refused, `main` |
  | (c) | commit with message `bad` in a worktree on `feat/x` | refused, the `checkCommitMessage` error |
  | (d) | commit a `.kt` file containing `// x` | refused, `comment not allowed` |
  | (e) | valid commit in the worktree | accepted |
  | (f) | `git rebase` in the worktree (detached HEAD) replaying a valid commit | accepted, not treated as main |
  | (g) | worktree path containing a space | accepted |
  | (h) | `pre-push` with a failing unit test | refused |
  | (i) | `pre-push` with a workflow zizmor flags (e.g. an unpinned action) | refused |

- [ ] **Step 2: Run it and confirm it fails**

  Run: `scripts/test-hooks.sh`

  Expected: case (a) fails first, because no hooks exist yet.

- [ ] **Step 3: Implement the hooks**
  - `is_main_checkout`: `[ "$(git rev-parse --git-dir)" = "$(git rev-parse --git-common-dir)" ]`
  - `on_main_branch`: `[ "$(git symbolic-ref -q --short HEAD)" = main ]`. A detached HEAD is not main.
  - **pre-commit:** runs `checkNoComments` and `ktlintCheck` on the whole repo, quietly, with the configuration cache.
  - **commit-msg:** runs `checkCommitMessage -PmessageFile="$1"`.
  - **pre-push:** refuses pushes to `refs/heads/main`, then runs `zizmor` on `.github/workflows/` through mise, then `./gradlew check`.
  - The scripts are POSIX sh, start with `#!/bin/sh`, and have no other comments.

- [ ] **Step 4: Run the tests and confirm they pass**

  Run: `scripts/test-hooks.sh`

  Expected: `9/9 passed`.

- [ ] **Step 5: Commit and PR**

  Branch `feat/git-hooks`.

  Subject: `feat: add git hooks enforcing worktrees, commits and checks`

  Paste the `test-hooks.sh` output as evidence.

---

### Task 8: CI workflows, CodeQL, dependency verification, Dependabot, required checks

**Files:**
- Create:
  - `.github/workflows/ci.yml`
  - `.github/workflows/codeql.yml`
  - `.github/dependabot.yml`
  - `gradle/verification-metadata.xml`
- Modify:
  - `scripts/github/apply-repo-settings.sh`
  - `scripts/github/expected-settings.json`
  - `mise.toml`
  - `mise.lock`
  - `docs/versions.md`, adding action SHAs and versions and the zizmor version

**Interfaces:**
- Consumes: all Gradle check tasks from Tasks 3–6.
- Produces the required status check names `checks`, `pr-rules`, `analyze (java-kotlin)`, `analyze (actions)`.

- [ ] **Step 1: Resolve the SHAs for the action versions in spec §11**

  For each action, resolve the SHA of its version tag:

  ```bash
  gh api repos/actions/checkout/git/ref/tags/v7.0.1 --jq .object.sha
  ```

  Dereference annotated tags with `gh api repos/<o>/<r>/git/tags/<sha> --jq .object.sha`. Record each SHA and version in `docs/versions.md`.

  Also re-verify zizmor's latest stable release. Add it to `mise.toml` as `aqua:zizmorcore/zizmor`, run `mise lock`, and confirm the lock entry has a sha256 per locked platform and `provenance = "github-attestations"`. Record version, source and date in `docs/versions.md`.

- [ ] **Step 2: Write `ci.yml`**
  - **Triggers:** `pull_request` and `push` to `main`. Top-level `permissions: {}`.
  - **Job `checks`** (`contents: read`), steps:
    1. checkout
    2. install the pinned mise per Ruling R9 (release archive SHA-256 pinned), then `mise install --locked`
    3. `zizmor` on `.github/workflows/` through mise, with zero findings
    4. `gradle/actions/setup-gradle` with `cache-provider: basic`
    5. `./gradlew check checkNoComments checkWorkflows checkReleaseManifest --warning-mode=fail`
    6. `./gradlew -p build-logic/checks check koverVerify`
  - **Job `pr-rules`** (PRs only, `contents: read`, `pull-requests: read`):
    - checkout with `fetch-depth: 0`
    - write the inputs through `env:`, never inline:
      - `PR_TITLE: ${{ github.event.pull_request.title }}` → `title.txt`
      - `PR_BODY: ${{ github.event.pull_request.body }}` → `body.txt`
      - labels via `gh pr view --json labels` → `labels.txt`
      - `git log -z --format=%B origin/$BASE..HEAD` → `messages.bin`
      - `git diff --name-only origin/$BASE...HEAD` → `paths.txt`
    - run `checkCommitMessages` and `checkPullRequest -PtitleFile=title.txt` with the other inputs
  - In job `checks`, also run `./gradlew :app:assembleRelease`, then `test -s app/build/outputs/mapping/release/mapping.txt`. This proves R8 ran (spec §7.2, manifest and build hardening).
  - **Workflow-level `concurrency`:** cancel in-progress runs for the same ref.

- [ ] **Step 3: Write `codeql.yml`**
  - Matrix languages `java-kotlin` (`build-mode: manual`, build step `./gradlew assembleDebug`) and `actions` (`build-mode: none`).
  - Permissions `security-events: write` and `contents: read`, at job level only.

- [ ] **Step 4: Write `dependabot.yml`**
  - Ecosystems `gradle` (directories `/` and `/build-logic/checks`) and `github-actions`, weekly.
  - `commit-message: { prefix: build, include: scope }`
  - `labels: [dependencies]`
  - `groups` that group all updates per ecosystem

- [ ] **Step 5: Generate dependency verification**

  Run: `./gradlew --write-verification-metadata sha256 check :app:assembleRelease` and also `-p build-logic/checks check`.

  Then delete the generated XML comments, and set `<verify-metadata>true</verify-metadata>`.

  Run: `./gradlew --refresh-dependencies check`

  Expected: success.

  Prove it bites: corrupt one `sha256` value, run, paste the `Dependency verification failed` output, revert. Also confirm whether the included build is covered by the root file, and record the finding as evidence.

- [ ] **Step 6: Run the local checks**

  Run: `./gradlew checkWorkflows checkNoComments`

  Expected: success on the new YAML.

  Also run `zizmor` on `.github/workflows/` through mise. Expected: zero findings, with no ignore or suppression config.

- [ ] **Step 7: Open the PR and confirm every job runs**

  Branch `ci/pipeline`. Open the PR.

  Expected: jobs `checks`, `pr-rules`, `analyze (java-kotlin)` and `analyze (actions)` all green. Paste the run URL.

  **Evidence for spec §11 "CodeQL supports Kotlin 2.4.20":** the `analyze (java-kotlin)` log shows Kotlin files extracted with no unsupported-version warning.

- [ ] **Step 8: Prove the PR gates bite on a throwaway PR**
  - Use a throwaway PR with commit message `update stuff` and no `Closes #`.
  - Expected: `pr-rules` fails, with both errors in the log. Paste the URL.
  - Also prove zizmor bites: on the throwaway PR (or locally, pasted), a workflow change zizmor flags makes `checks` fail. Paste the output.
  - Close the PR and delete the branch.

- [ ] **Step 9: Merge, then require the checks**
  - Merge the CI PR.
  - Extend `apply-repo-settings.sh` so the `main-protection` ruleset requires the four checks with `strict_required_status_checks_policy: true`.
  - Run it. Expected: `all settings match`.
  - Commit this change in a follow-up PR, branch `ci/require-checks`. That PR is itself the first one gated by the checks.

---

### Task 9: Onboarding docs and ADRs

**Files:**
- Rewrite (bootstrap versions already exist):
  - `README.md`
  - `CLAUDE.md`
- Create:
  - `CONTRIBUTING.md`
  - `docs/game-rules.md`
  - `docs/adr/0001-native-kotlin-libgdx.md`
  - `docs/adr/0002-no-ktx.md`
  - `docs/adr/0003-no-di-framework.md`
  - `docs/adr/0004-no-detekt.md`
  - `docs/adr/0005-signing-key.md`
  - `docs/adr/0006-kotlin-capped-by-codeql.md`
  - `docs/adr/0007-squash-merge.md`

**Interfaces:**
- Consumes:
  - the spec sections cited per file
  - the hook and task names from Tasks 3–8

**Content requirements:**
- Each ADR uses the sections **Context**, **Decision**, **Consequences** and **Status: accepted 2026-10-22**, with content taken from the cited spec section:

  | ADR | Spec section |
  |-----|--------------|
  | 0001 | §4 Why native Kotlin + libGDX |
  | 0002 | §4 Why no KTX, including the table |
  | 0003 | §4 Why no DI framework |
  | 0004 | §11 Why no detekt |
  | 0005 | §8 Signing key |
  | 0006 | §11 Kotlin cap |
  | 0007 | §11 Merge method |

- `docs/game-rules.md` is spec §2 verbatim, including the source citation and the S/C tags.
- `CONTRIBUTING.md`:
  - spec §7 as a how-to, with the rules table and the enforcer of each rule
  - the practices section, labelled "not gated"
  - the evidence rule
  - the dependency rule from spec §6
  - the version-verification procedure, including the CodeQL Kotlin cap
  - the Dependabot follow-up procedure from spec §13.13
  - how to strip comments that tools write
- `CLAUDE.md` gives AI agents the same rules in short form, with pointers to `CONTRIBUTING.md` and the plugin skills (the plugin install is added in Task 10).
- `README.md` contains:
  - what SuperSnake is
  - the four purposes
  - the toolchain (JDK 25 via mise, Android SDK 37, `gh` with `gh-stack`)
  - a quick start: `scripts/bootstrap.sh`, then `./gradlew check`

**Steps:**

- [ ] **Step 1: Write the files**

  Write them according to the content requirements above.

- [ ] **Step 2: Prove onboarding from a fresh clone**

  ```bash
  git clone https://github.com/EastwoodNo1/SuperSnake /tmp-scratch/fresh && cd /tmp-scratch/fresh && scripts/bootstrap.sh && ./gradlew check
  ```

  The clone goes in the session scratchpad.

  Expected: bootstrap prints `hooks installed`, and the Gradle run ends with `BUILD SUCCESSFUL`. Paste both lines as evidence.

- [ ] **Step 3: Commit and PR**

  Branch `docs/onboarding`.

  Subject: `docs: add onboarding guides, game rules and ADRs 0001-0007`

---

### Task 10: Plugin repo `android-sdlc-skills` v0.1.0, proven on a fresh project

**Files (new repo `EastwoodNo1/android-sdlc-skills`):**
- `.claude-plugin/plugin.json`
- `.claude-plugin/marketplace.json`
- `skills/git-hooks/SKILL.md`
- `skills/git-hooks/templates/**`: copies of `.githooks/*`, `scripts/bootstrap.sh`, `scripts/test-hooks.sh`
- `skills/android-ci/SKILL.md`
- `skills/android-ci/templates/**`: copies of:
  - `.github/workflows/*`
  - `mise.toml`
  - `mise.lock`
  - `.github/dependabot.yml`
  - `.github/pull_request_template.md`
  - `SECURITY.md`
  - `scripts/github/*`
  - `build-logic/checks/**`
  - `gradle/ktlint.gradle.kts`
  - `.editorconfig`
- `skills/sdlc-rules/SKILL.md`
- `scripts/verify-templates.sh`
- `README.md`
- `CONTRIBUTING.md`
- `.github/workflows/ci.yml`

**SuperSnake files:**
- Create: `.claude/settings.json`
- Modify: `CLAUDE.md`

**Interfaces:**
- Produces:
  - plugin name `android-sdlc`
  - skills `android-sdlc:git-hooks`, `android-sdlc:android-ci`, `android-sdlc:sdlc-rules`
  - tag `v0.1.0`
  - `verify-templates.sh <consumer-repo-path>`: diffs every template against the consumer's file and exits 1 on drift

- [ ] **Step 1: Check the current plugin format**

  Read the current Claude Code plugin and marketplace documentation, and record the doc URLs. Use the `plugin-dev`/`write-a-skill` skills for structure. Each SKILL.md:
  - has a `description` that states when to trigger it
  - has numbered steps, with the evidence the agent must show at each step
  - for `android-ci`, tells the agent to re-verify action SHAs and versions, and zizmor's version, instead of trusting the templates
  - for `sdlc-rules`, encodes spec §6–§7 and §13.12–13 plus the evidence rule

- [ ] **Step 2: Create the repo**

  `gh repo create EastwoodNo1/android-sdlc-skills --public`. Apply the same settings script to it, using the checks that apply to a non-Android repo:
  - the CI job runs `verify-templates.sh` against a SuperSnake checkout
  - CI also runs `shellcheck`, which is preinstalled on `ubuntu-latest` runners; verify that in the runner image docs and record it as evidence
  - CI runs the copied `checks` build's own tests

- [ ] **Step 3: Prove `verify-templates.sh` against SuperSnake**

  Run: `scripts/verify-templates.sh ../SuperSnake`

  Expected: `0 drift`.

  Change one template byte, run again, and paste the drift output (exit 1). Revert.

- [ ] **Step 4: Prove the skills on a fresh project**
  - In the scratchpad, create a new empty Android project: copy the SuperSnake skeleton from Task 1 under a different package.
  - Start a fresh subagent that only has the plugin installed, and ask it to "set up hooks and CI for this project".
  - Expected:
    - the subagent ran the `git-hooks` and `android-ci` skills
    - `scripts/test-hooks.sh` reports `9/9 passed`
    - `./gradlew check checkNoComments checkWorkflows` succeeds
  - Paste the outputs. Don't create a GitHub repo for the scratch project.

- [ ] **Step 5: Tag v0.1.0 and install the plugin in SuperSnake**
  - Tag `v0.1.0` and create a GitHub release.
  - In SuperSnake, `.claude/settings.json` declares the marketplace `EastwoodNo1/android-sdlc-skills` at `v0.1.0` and enables `android-sdlc`.
  - `CLAUDE.md` points to the three skills.
  - Run `claude plugin list` (or the documented equivalent). Expected: `android-sdlc` is listed and enabled.

- [ ] **Step 6: Commit and PR (SuperSnake)**

  Branch `chore/install-sdlc-plugin`.

  Subject: `chore: install android-sdlc plugin v0.1.0`
