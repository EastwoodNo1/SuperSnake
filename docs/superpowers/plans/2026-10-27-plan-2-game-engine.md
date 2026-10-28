# SuperSnake Plan 2: Game Engine Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development to implement this plan task-by-task (owner's choice, confirmed 2026-10-28). Every subagent runs on model Sonnet 5.5: pass `model: "sonnet"` on each Agent call. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Every game decision lives in `:domain` and `:data`, as pure Kotlin JVM code at 100% line and branch coverage, so Plan 3 can build the UI and board on top without adding a single rule.

**Architecture:**
- `:domain` (exists, package `io.github.eastwoodno1.supersnake.domain`) gains, in one flat package with one concern per file:
  - the rules engine: S1–S5, C1–C4, C7–C12 and C13–C25, as pure functions of state, input and an injected random source
  - the turn queue, the countdown, pause and step timing
  - input classification: a swipe tracker and a platform-neutral key → game input
  - render maths: letterbox layout, segment glide, glow pulse, particle bursts
  - high scores and settings: models, table rules, repository interfaces, use cases
  - `GameSession`: the one stateful object a ViewModel drives, so ViewModels never branch (spec §5)
- `:data` (new, Kotlin JVM, package `io.github.eastwoodno1.supersnake.data`) implements the two repository interfaces on `androidx.datastore:datastore-core` with our own text serializers. No okio.
- Neither module knows Android, libGDX, Compose or the system clock. `:app` (Plan 3) supplies the `Clock` and `RandomSource` adapters and the store files.

**Tech Stack:** Kotlin 2.4.20 (JVM toolchain 25), kotlinx-coroutines 1.11.0 (core, test), DataStore core 1.3.0, JUnit 6.1.3 + kotlin-test, Kover 0.9.11, ktlint 1.8.0.

**Spec:** two documents, read together:
- `docs/superpowers/specs/2026-10-22-supersnake-design.md`, the main spec, approved 2026-10-22
- `docs/superpowers/specs/2026-10-28-engine-feel-and-storage-design.md`, the engine feel and storage design, approved by the owner on 2026-10-28. Where they differ, this one wins.

The rules are numbered in `docs/game-rules.md` (S1–S5, C1–C25). Plan 1, `docs/superpowers/plans/2026-10-22-plan-1-foundation.md`, built the repo, gates, hooks and CI this plan relies on.

## Start here (for a fresh session)

Together with the two spec documents, this plan is self-contained. A new session needs nothing from the conversations that wrote them. Statements marked **[owner]** or "confirmed by the owner" record the owner's decisions. Treat them as settled.

`README.md` (for humans) and `CLAUDE.md` (for agents) at the repo root point here.

### To start or resume

**AI agent (Claude Code):** open Claude Code in `/Users/r03418/AndroidStudioProjects/SuperSnake` and say:

> Execute `docs/superpowers/plans/2026-10-27-plan-2-game-engine.md` with superpowers:subagent-driven-development, starting at the first task that is not done.

**Human:** work through the tasks in order, by hand. Every step gives the command and the output that means it passed.

### Read first, in this order

1. This file, all of it.
2. `docs/superpowers/specs/2026-10-28-engine-feel-and-storage-design.md`, all of it.
3. The main spec: §2 (rules), §5 (architecture), §6 (dependencies), §7 (SDLC).
4. `docs/game-rules.md`: the numbered rules the tests cite.
5. `CONTRIBUTING.md`: the per-change flow and how each rule is enforced.

### Progress lives in git

- **A task is done** when all of its checkboxes are ticked in this file on `origin/main`. The task's PR ticks them, so the ticks land together with the work.
- **A task is in progress** if any of these exists for it: a worktree (`git worktree list`), its branch (named in its **Branch** line), or an open PR (`gh pr list`). Continue in that worktree. Ticked steps there are done; re-run the first unticked step's verification before moving on.
- **A task is not started** if none of those exist.
- Run `git pull` in the main checkout first, and use `origin/main` as the record.
- **Start from the first task that is not done.** Never skip a task: each one consumes the previous one's types.

### State at handoff (2026-10-28)

- **Plan 1 is complete** (PRs #2 to #45). Plan 2 and the engine feel and storage design are written and approved; no Plan 2 task has started. Run `git log --oneline` for the current head.
- **Decided on 2026-10-28 [owner]:** storage is DataStore **core** with our own serializers, so okio never enters the build; no grace period at walls; a 3-2-1 countdown; fixed speed. See the design document.
- **Plugin:** `EastwoodNo1/android-sdlc-skills` at `v0.2.0`, installed through `.claude/settings.json`. `scripts/verify-templates.sh` printed `35 templates checked` / `0 drift` against `main` on 2026-10-27. No task here changes a file the plugin copies; Task 9 re-runs the check.
- **Dependabot PR #41** (Kotlin 2.4.20 → 2.4.21) stays open: CodeQL's supported-languages page still read "Kotlin 1.8.0 to 2.4.20" on 2026-10-27 (ADR-0006). Tasks 5 and 8 re-check it; when CodeQL lists 2.4.21, handle #41 as `docs/versions.md` "Dependency verification updates" says, as its own PR, between tasks.
- **ADLC capture is on.** Every PR runs the fold-in in the per-task workflow below.
- **Never commit** `local.properties`, `.idea/`, `.gradle/`, `.superpowers/` or `.claude/settings.local.json`.

### Machine prerequisites

Plan 1's "Machine prerequisites" table lists every tool and its check command; all were current on 2026-10-26. This plan needs only mise, JDK 25 through mise, gh as `EastwoodNo1`, and git. Re-run:

```sh
~/.local/bin/mise --version
~/.local/bin/mise exec -- java -version
~/.local/bin/mise exec -- gh auth status
git --version
```

Expected: mise 2026.10.x, `Temurin-25.0.5+8`, `Logged in to github.com account EastwoodNo1`, git ≥ 2.36. Fix anything that differs before Task 1.

### How the owner works

- Solo. Evidence for every claim. No comments in code. Latest stable versions, verified on the day. Minimal third-party dependencies.
- Claude reviews run locally from Claude Code. No Claude GitHub App, no Claude Code Action, no Anthropic API key in GitHub.
- **Plan approval covers:** one GitHub issue and one PR per task in this repo, and adding `kotlinx-coroutines-core`, `kotlinx-coroutines-test` and `datastore-core`.
- **Stop and ask the owner before:**
  - anything outward-facing this plan doesn't list
  - deleting anything this plan doesn't list
  - any change to a decision in the two spec documents or `docs/game-rules.md`
  - **any fallback if Task 8 Step 1 fails** (design §3): do not switch to another storage approach on your own

### After Plan 2 is merged

- Start a fresh session and use superpowers:writing-plans to write Plan 3 (App) from the specs and the Roadmap below, re-verifying versions and CodeQL's Kotlin support.
- Give Plan 3 the same "Start here" section, updated with the state at that point.

## Roadmap (this plan is 2 of 4)

1. **Foundation.** Done (Plan 1).
2. **Game engine (this plan).** `:domain` and `:data` at 100% coverage.
3. **App.**
   - Neon Compose UI, splash, libGDX `:board` embedded in Compose, controls, accessibility.
   - UI tests on 3 emulator profiles, the 16 KB alignment check, final manifest hardening, assets.
   - Backup rules naming exactly the two store files from Task 8.
   - The `Clock` and `RandomSource` adapters; `MotionEvent` → `SwipeTracker` calls and `KeyEvent` → `GameKey`; the swipe threshold in dp; lifecycle and rotation → `GameSession.pause()` (C20); TalkBack for `Announcement`s (C25).
   - Any new decision Plan 3 meets, such as which `GameEvent` plays a sound or haptic under the current `Settings`, goes into `:domain` with tests, never into a ViewModel.
   - `android-ci` gains the UI-test job.
4. **Release.** As recorded in Plan 1's Roadmap, including its open design point on the `release` environment.

## Global Constraints

Copied from the specs and Plan 1. Every task's requirements include this section.

**Code and commits**
- **No comments** in `*.kt`, `*.kts`, `*.xml`, `*.toml`, `*.yml`, `*.yaml`, `*.sh`, `*.pro`, `*.properties`. Strip comments tools write.
- **Commits:** Conventional Commits; a body with **Why:** and **What:** for `feat`, `fix`, `refactor`, `perf` (and others when there is a why). Subagent commits end with `Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>`; the orchestrator uses the line its own session's system reminder gives. Never copy another model's line.
- **Evidence:** every "passes / works / done" claim carries the exact command and its real output, or a CI run link.

**Versions and dependencies** (verified 2026-10-27/28)

| Item | Version | Source |
|------|---------|--------|
| Kotlin | 2.4.20 (2.4.21 exists; capped by CodeQL, which lists "Kotlin 1.8.0 to 2.4.20") | repo1 `kotlin-gradle-plugin/maven-metadata.xml`; codeql.github.com/docs/codeql-overview/supported-languages-and-frameworks/ |
| kotlinx-coroutines-core, kotlinx-coroutines-test | 1.11.0 (POM Last-Modified 2026-05-07) | https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-core/maven-metadata.xml |
| datastore-core | 1.3.0. Its `-jvm` module metadata lists only `kotlin-stdlib`, `kotlinx-coroutines-core` and `androidx.annotation:annotation` (read 2026-10-28) | https://dl.google.com/android/maven2/androidx/datastore/datastore-core/maven-metadata.xml |
| AGP, Gradle, Kover, JUnit BOM, ktlint | unchanged: 9.4.1, 9.8.2, 0.9.11, 6.1.3, 1.8.0 (still latest stable on 2026-10-27) | `docs/versions.md` |

- Each task that adds a dependency re-verifies it that day, uses a newer stable if one exists, records it in `docs/versions.md` (source URL, date), and regenerates `gradle/verification-metadata.xml` with `docs/versions.md` "Dependency verification updates" step 2.
- **Third-party:** none added. Never add `datastore-preferences*`, okio, kotlinx-serialization or kotlinx-datetime. No Gradle plugins beyond AGP, Kotlin and Kover.

**Modules and packages**
- `:domain`: Kotlin JVM, package `io.github.eastwoodno1.supersnake.domain`, flat. May depend only on the Kotlin stdlib, the JDK and `kotlinx-coroutines-core`.
- `:data`: Kotlin JVM, package `io.github.eastwoodno1.supersnake.data`. Depends on `:domain` and `datastore-core`. No Android, libGDX or Compose.
- Both: JVM toolchain 25, `allWarningsAsErrors = true`, JUnit Platform, Kover verify 100% LINE and 100% BRANCH, **no exclusions**. If a branch can't be covered, restructure the code.
- Dates are `java.time.LocalDate`.

**Process**
- Every task: its own GitHub issue, its own worktree and branch, squash-merged PR whose title is a Conventional header of at most 64 characters, body with `Closes #<issue>` and a filled `## Evidence` section. See "Per-task workflow".
- `pr-rules` requires a PR that changes `src/main` in `:domain` or `:data` to change `src/test` in the same module. Every task below does.
- Tests name the rule they prove in their name or a `@DisplayName`, for example `c16FullQueueReplacesLastTurn`.

## Per-task workflow

Every task runs these around its own steps. Each task's last step says "Finish per the per-task workflow".

**Before the task's Step 1:**
1. `git -C /Users/r03418/AndroidStudioProjects/SuperSnake pull --ff-only`
2. Create the issue: `~/.local/bin/mise exec -- gh issue create --title "<task title>" --body "<one-paragraph why, citing the rule numbers the task implements>"`. Note the number.
3. `git -C /Users/r03418/AndroidStudioProjects/SuperSnake worktree add ../SuperSnake-<name> -b <branch>` with the task's **Branch** line, then work in `/Users/r03418/AndroidStudioProjects/SuperSnake-<name>`.
4. `cp /Users/r03418/AndroidStudioProjects/SuperSnake/local.properties /Users/r03418/AndroidStudioProjects/SuperSnake-<name>/` (untracked; without it Gradle stops with `SDK location not found`, as `CONTRIBUTING.md` step 2 says).

Gradle runs as `~/.local/bin/mise exec -- ./gradlew <tasks>` from the worktree. Commit after each green step with a Conventional message; small commits are fine because the PR is squashed.

**Finish:**
1. Strict gate, from the worktree:

   ```sh
   ~/.local/bin/mise exec -- scripts/gradle-strict.sh check :checkNoComments :domain:koverVerify
   ```

   Add `:data:koverVerify` from Task 8 on. Expected: `BUILD SUCCESSFUL`, no deprecation other than the allowlisted AGP one.
2. Coverage counters for Evidence (`koverLog` prints only lines, so use the XML report):

   ```sh
   ~/.local/bin/mise exec -- ./gradlew :domain:koverXmlReport
   tail -8 domain/build/reports/kover/report.xml | grep -o '<counter type="\(LINE\|BRANCH\)"[^>]*/>'
   ```

   Same for `:data` from Task 8. Expected: both counters with `missed="0"`, as on 2026-10-27 for `Direction` (`BRANCH missed="0" covered="4"`, `LINE missed="0" covered="12"`).
3. Add a row to the "Engine details **[builder]**" table in `docs/game-rules.md` for each **[builder]** value the task names (ids `B1`, `B2`, … in task order).
4. Tick this task's checkboxes in this plan file, in the same branch.
5. ADLC fold-in, as `CLAUDE.md` "Fold-in before a pull request" says: if `$(git rev-parse --path-format=absolute --git-common-dir)/adlc/staged/` has notes, run `android-sdlc:adlc-documenter` (`model: "sonnet"`) with `MODE: fold-in`, `STAGED: <dir>`, `REPO: <worktree>`; run its `scan.sh` until it exits 0; commit `docs(adlc): fold in session notes`; save the returned lessons as memories; then delete the folded files from `staged/`.
6. `git push -u origin <branch>` (pre-push runs the strict `check`), then `~/.local/bin/mise exec -- gh pr create --title "<task's PR title>" --body-file <file>`. The body has `## Why`, `## What`, `Closes #<issue>`, and `## Evidence` with the outputs from 1 and 2 and the task's red and green test output, and ends with the attribution line the session's system reminder gives.
7. Wait for the four required checks (`checks`, `pr-rules`, `analyze (java-kotlin)`, `analyze (actions)`) with `gh pr checks <n> --watch`. Paste the result in a PR comment. Squash-merge only when all are green: `gh pr merge <n> --squash --delete-branch`.
8. `git worktree remove ../SuperSnake-<name>` from the main checkout, then `git pull --ff-only`.

## Review Focus

1. **A fast double turn that reverses direction.** Heading right, the player swipes up then left within one step. Expected: up on the next step, left on the one after, no death (C15, C16). Pinned in Task 3.
2. **A frame stall or a clock that steps backwards, including during the countdown.** A 2-second hitch, or `nowMillis` below the last step. Expected: at most one step per frame, no burst, no exception, glide clamped to 0..1; a stall during the countdown goes straight to the current number or "Go" (C19). Pinned in Tasks 6.
3. **A corrupted or hand-edited store file.** Random bytes, an unknown difficulty name, a malformed high-score line. Expected: no crash; settings fall back field by field; bad high-score lines are dropped and the rest kept (design §3). Pinned in Task 8.
4. **A zero-size surface.** The first layout reports 0 × 0 or a negative size. Expected: cell size 0, no `NaN`, no exception. Pinned in Task 7.
5. **Pause during the countdown, then resume.** Expected: queued turns cleared, countdown restarts from 3, the snake does not move until "Go" plus one step interval (C13, C20). Pinned in Task 6.

---

### Task 1: Board geometry, difficulty and the ports

**Branch:** `feat/board-and-difficulty` · **PR title:** `feat: add board geometry, difficulty and random source`

**Files:**
- Create in `domain/src/main/kotlin/io/github/eastwoodno1/supersnake/domain/`: `Cell.kt`, `Board.kt`, `Difficulty.kt`, `RandomSource.kt`, `Clock.kt`
- Create in `domain/src/test/kotlin/io/github/eastwoodno1/supersnake/domain/`: `CellTest.kt`, `BoardTest.kt`, `DifficultyTest.kt`, `RandomSourceTest.kt`

**Interfaces:**
- Consumes: `Direction` (exists: `UP(0,-1)`, `DOWN(0,1)`, `LEFT(-1,0)`, `RIGHT(1,0)`, `opposite`). y grows downward.
- Produces:
  - `data class Cell(val x: Int, val y: Int) { operator fun plus(direction: Direction): Cell }`
  - `object Board { const val SIZE = 20; val cells: List<Cell>; fun contains(cell: Cell): Boolean }`. `cells` is row-major: y outer, x inner.
  - `enum class Difficulty(val label: String) { ASSIST("A"), LEVEL_1("1"), …, LEVEL_9("9"); val tickMillis: Long; val pointsPerOrb: Int }`
  - `fun interface RandomSource { fun nextInt(bound: Int): Int }` returning `0 until bound`, and `class KotlinRandomSource(private val random: kotlin.random.Random) : RandomSource`
  - `interface Clock { fun nowMillis(): Long; fun today(): java.time.LocalDate }`. Monotonic millis for steps and the countdown, local date for high scores. No implementation in this plan; tests use fakes.

- [x] **Step 1: Write the failing tests**

```kotlin
@Test fun c8TickIntervals() = assertEquals(listOf(400L, 250L, 227L, 205L, 182L, 160L, 137L, 115L, 92L, 70L), Difficulty.entries.map { it.tickMillis })
@Test fun c8PointsPerOrb() = assertEquals(listOf(1, 1, 2, 3, 4, 5, 6, 7, 8, 9), Difficulty.entries.map { it.pointsPerOrb })
@Test fun c8Labels() = assertEquals(listOf("A", "1", "2", "3", "4", "5", "6", "7", "8", "9"), Difficulty.entries.map { it.label })

@Test fun c1BoardIsTwentyByTwentyRowMajor() {
    assertEquals(400, Board.cells.size)
    assertEquals(listOf(Cell(0, 0), Cell(1, 0)), Board.cells.take(2))
    assertEquals(Cell(0, 1), Board.cells[20])
    assertEquals(Cell(19, 19), Board.cells.last())
}
@Test fun s2ContainsEdgesOnly() {
    assertTrue(Board.contains(Cell(0, 0)) && Board.contains(Cell(19, 19)))
    listOf(Cell(-1, 0), Cell(20, 0), Cell(0, -1), Cell(0, 20)).forEach { assertFalse(Board.contains(it)) }
}

@Test fun plusStepsOneCell() = assertEquals(listOf(Cell(3, 3), Cell(3, 5), Cell(2, 4), Cell(4, 4)), Direction.entries.map { Cell(3, 4) + it })

@Test fun kotlinRandomSourceStaysInBound() {
    val source = KotlinRandomSource(Random(7))
    repeat(1000) { assertTrue(source.nextInt(5) in 0 until 5) }
    assertEquals(0, source.nextInt(1))
}
```

- [x] **Step 2: Run them and confirm they fail**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test`
Expected: compilation fails with `Unresolved reference 'Difficulty'` (and the other new types).

- [x] **Step 3: Implement the five files**

`tickMillis`: `ASSIST` is 400; level `n` is `250 - (45 * (n - 1) + 1) / 2` in integer maths, which is C8's `250 − round(22.5 × (n − 1))` with halves rounded up. `pointsPerOrb`: 1 for `ASSIST`, else `n`. Derive `n` from `ordinal`, not from a ten-branch `when`.

- [x] **Step 4: Run them and confirm they pass**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test :domain:koverVerify`
Expected: `BUILD SUCCESSFUL`.

- [x] **Step 5: Finish per the per-task workflow**

---

### Task 2: Game state and the step

**Branch:** `feat/game-step` · **PR title:** `feat: add game state and the step rules`

**Files:**
- Create in `domain/src/main/…/domain/`: `GameState.kt` (`GameState`, `GameStatus`, `GameEvent`, `TickResult`), `GameRules.kt`
- Create in `domain/src/test/…/domain/`: `GameRulesTest.kt`, `FixedRandom.kt` (test helper)

**Interfaces:**
- Consumes: Task 1 `Cell`, `Board`, `Difficulty`, `RandomSource`.
- Produces:
  - `enum class GameStatus { COUNTDOWN, RUNNING, PAUSED, LOST, WON }`
  - `enum class GameEvent { ORB_EATEN, LOST, WON }`
  - `data class GameState(val snake: List<Cell>, val direction: Direction, val pendingTurns: List<Direction>, val orb: Cell?, val score: Int, val orbsEaten: Int, val difficulty: Difficulty, val status: GameStatus)`. `snake` is head first.
  - `data class TickResult(val state: GameState, val events: List<GameEvent>)`
  - `object GameRules { fun newGame(difficulty: Difficulty, random: RandomSource): GameState; fun tick(state: GameState, random: RandomSource): TickResult }`. `newGame` returns status `COUNTDOWN` (C13); only `RUNNING` steps.
  - Test helper `class FixedRandom(vararg values: Int) : RandomSource` returns the values in order and fails the test with `error("unexpected random draw")` when exhausted. Later tasks reuse it.

- [x] **Step 1: Write the failing tests** (`GameRulesTest`; tests other than the first build `RUNNING` states with `copy`)

  | Test | Setup | Assertions |
  |------|-------|------------|
  | `c2c13NewGameCountsDownCentredHeadingRight` | `newGame(LEVEL_1, FixedRandom(0))` | `snake == listOf(Cell(10,10), Cell(9,10), Cell(8,10))`, `direction == RIGHT`, `pendingTurns` empty, `score == 0`, `orbsEaten == 0`, `status == COUNTDOWN`, orb present |
  | `c3OrbSpawnsOnRandomFreeCell` | `FixedRandom(0)`, then `FixedRandom(396)` | orb is `Cell(0,0)`, then `Cell(19,19)`: an index into `Board.cells` minus the snake, row-major |
  | `s1StepMovesOneCell` | running, orb far away | snake becomes `(11,10),(10,10),(9,10)`, events empty |
  | `c4c8EatingGrowsByOneAndScoresLevel` | `LEVEL_4`, orb at `Cell(11,10)`, `FixedRandom(0)` for the respawn | snake `(11,10),(10,10),(9,10),(8,10)` (tail stayed), `score == 4`, `orbsEaten == 1`, events `[ORB_EATEN]`, new orb `Cell(0,0)` |
  | `c8AssistScoresOnePoint` | as above with `ASSIST` | `score == 1` |
  | `s4c17WallEndsTheGameOnThatStep` | head at each edge moving outward, one case per direction | `status == LOST`, snake unchanged (C21), events `[LOST]` |
  | `s4BodyEndsTheGame` | 5-segment hook whose next head cell is a body cell that is not the tail | `LOST`, events `[LOST]` |
  | `c7EnteringLeavingTailIsLegal` | `snake = (1,1),(1,2),(2,2),(2,1)`, heading `RIGHT`, orb at `(10,10)` | `RUNNING`, head `(2,1)`, length 4 |
  | `c7EnteringTailOnGrowthStepCollides` | same snake, orb placed on the tail `(2,1)` (crafted; play never puts an orb on the snake) | `LOST` |
  | `c9FillingTheBoardWins` | snake = the first 399 cells of the boustrophedon path (row y even: x 0→19; odd: x 19→0), head first, so head `(1,19)`, heading `LEFT`, orb `(0,19)`, `FixedRandom()` with no values | `status == WON`, `orb == null`, `snake.size == 400`, events `[ORB_EATEN, WON]`, no random draw |
  | `stepOutsideRunningChangesNothing` | `COUNTDOWN`, `PAUSED`, `LOST`, `WON` | `TickResult(state, emptyList())` with the same state |

- [x] **Step 2: Run them and confirm they fail**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test --tests '*GameRulesTest'`
Expected: compilation fails with `Unresolved reference 'GameRules'`.

- [x] **Step 3: Implement `GameRules.newGame` and `GameRules.tick`**

The step, in order:

```text
if status != RUNNING                     -> unchanged, no events
direction = pendingTurns.firstOrNull() ?: direction; pendingTurns = pendingTurns.drop(1)
head = snake.first() + direction
grows = head == orb
blocking = if grows: snake else snake.dropLast(1)                 (C7)
if head !in Board or head in blocking   -> LOST, state otherwise unchanged, events [LOST]   (S4, C17, C21)
snake = [head] + (if grows: snake else snake.dropLast(1))
if !grows                                -> RUNNING, no events
score += difficulty.pointsPerOrb; orbsEaten += 1
free = Board.cells - snake                (as a set lookup, not a list scan per cell)
if free empty                            -> WON, orb = null, events [ORB_EATEN, WON]         (C9)
orb = free[random.nextInt(free.size)], events [ORB_EATEN]                                    (C3)
```

The turn pop is here so Task 3 only adds the enqueue side.

- [x] **Step 4: Run them and confirm they pass**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test :domain:koverVerify`
Expected: `BUILD SUCCESSFUL`.

- [x] **Step 5: Finish per the per-task workflow**

---

### Task 3: Turn queue, countdown status and pause

**Branch:** `feat/turns-and-pause` · **PR title:** `feat: queue turns and pause the game`

**Files:**
- Create: `domain/src/main/…/domain/Turns.kt`, `domain/src/test/…/domain/TurnsTest.kt`
- Modify: `domain/src/main/…/domain/GameRules.kt` (add `go`, `pause`, `resume`), `domain/src/test/…/domain/GameRulesTest.kt`

**Interfaces:**
- Consumes: Task 2 `GameState`, `GameStatus`, `GameRules.tick`, `FixedRandom`.
- Produces:
  - `object Turns { const val CAPACITY = 2; fun enqueue(state: GameState, direction: Direction): GameState }`
  - `GameRules.go(state: GameState): GameState`: `COUNTDOWN → RUNNING`, every other status unchanged
  - `GameRules.pause(state: GameState): GameState`: `COUNTDOWN` or `RUNNING → PAUSED`, clearing `pendingTurns`; every other status unchanged
  - `GameRules.resume(state: GameState): GameState`: `PAUSED → COUNTDOWN`; every other status unchanged

- [x] **Step 1: Write the failing tests**

  | Test | Setup | Assertions |
  |------|-------|------------|
  | `c16TurnIsQueued` | running, heading `RIGHT`, `enqueue(UP)` | `pendingTurns == [UP]` |
  | `c15OppositeOfCurrentIgnoredWhenQueueEmpty` | heading `RIGHT`, `enqueue(LEFT)` | empty |
  | `c15OppositeOfLastQueuedIgnored` | queue `[UP]`, `enqueue(DOWN)` | `[UP]` |
  | `c15SameAsReferenceIgnored` | heading `RIGHT`, `enqueue(RIGHT)`; queue `[UP]`, `enqueue(UP)` | empty; `[UP]` |
  | `c16SecondTurnAppends` | queue `[UP]`, `enqueue(LEFT)` | `[UP, LEFT]` |
  | `c16FullQueueReplacesLastTurn` | queue `[UP, LEFT]`, `enqueue(RIGHT)` | `[UP, RIGHT]` (checked against `UP`) |
  | `c16FullQueueReplacementIsChecked` | queue `[UP, LEFT]`, `enqueue(DOWN)`; and `enqueue(UP)` | both `[UP, LEFT]` (opposite of, and same as, `UP`) |
  | `c14TurnsAcceptedDuringCountdown` | `COUNTDOWN`, heading `RIGHT`, `enqueue(UP)` | `[UP]` |
  | `c14TurnsIgnoredWhenPausedOrEnded` | `PAUSED`, `LOST`, `WON` | state unchanged |
  | `c16OneTurnAppliedPerStep` | running, queue `[UP, LEFT]`, tick, tick | after first: `direction == UP`, queue `[LEFT]`; after second: `LEFT`, queue empty |
  | `c15FastReversalSurvives` (Review Focus 1) | running new game, `enqueue(UP)`, `enqueue(LEFT)`, tick twice | `RUNNING`, head `(10,9)` then `(9,9)` |
  | `c13GoStartsMovingOnlyFromCountdown` | `go` on each status | `COUNTDOWN → RUNNING`; others unchanged |
  | `c20PauseClearsTurns` | `RUNNING` with `[UP]`; `COUNTDOWN` with `[UP]` | both `PAUSED`, queue empty; `tick` changes nothing |
  | `c20ResumeCountsDownAgain` | `resume` on each status; `pause` on `PAUSED`, `LOST`, `WON` | `PAUSED → COUNTDOWN`; every other case unchanged |

- [x] **Step 2: Run them and confirm they fail**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test --tests '*TurnsTest' --tests '*GameRulesTest'`
Expected: compilation fails with `Unresolved reference 'Turns'`.

- [x] **Step 3: Implement `Turns.enqueue`, `go`, `pause` and `resume`**

`enqueue`: if the status is not `COUNTDOWN` or `RUNNING`, unchanged. Let `kept` be `pendingTurns` when it has fewer than `CAPACITY` entries, else `pendingTurns.dropLast(1)`. The reference is `kept.lastOrNull() ?: direction`. Ignore `direction` equal to the reference or its opposite; otherwise `pendingTurns = kept + direction`. When a full queue's replacement is ignored, the queue stays as it was.

- [x] **Step 4: Run them and confirm they pass**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test :domain:koverVerify`
Expected: `BUILD SUCCESSFUL`.

- [x] **Step 5: Finish per the per-task workflow**

---

### Task 4: Input classification

**Branch:** `feat/input-classification` · **PR title:** `feat: classify swipes and keys into game input`

**Files:**
- Create: `domain/src/main/…/domain/GameInput.kt` (`GameInput`, `GameKey`), `SwipeTracker.kt` (`SwipeTracker`, `SwipeStep`)
- Create: `domain/src/test/…/domain/GameInputTest.kt`, `SwipeTrackerTest.kt`

**Interfaces:**
- Consumes: `Direction`.
- Produces:
  - `sealed interface GameInput { data class Turn(val direction: Direction) : GameInput; data object TogglePause : GameInput }`
  - `enum class GameKey { UP, DOWN, LEFT, RIGHT, PAUSE; val input: GameInput }`. Plan 3 maps arrow keys, D-pad, WASD and gamepad buttons onto it; the on-screen D-pad sends `GameInput.Turn` directly.
  - `data class SwipeTracker(val pointerId: Int? = null, val originX: Float = 0f, val originY: Float = 0f) { fun down(pointerId: Int, x: Float, y: Float): SwipeTracker; fun move(pointerId: Int, x: Float, y: Float, threshold: Float): SwipeStep; fun up(pointerId: Int): SwipeTracker }`
  - `data class SwipeStep(val tracker: SwipeTracker, val direction: Direction?)`
  - Screen coordinates: `dy > 0` is `DOWN`. `threshold` is in pixels, converted from dp by `:app` (design §4.8).

C24: `down` starts tracking only when no pointer is tracked. `move` from another pointer, or with nothing tracked, gives no turn and no change. A turn fires when the longer axis of `(x - originX, y - originY)` is at least `threshold`, and then the origin moves to `(x, y)`. An exact diagonal or a non-finite value gives no turn and keeps the origin. `up` from the tracked pointer stops tracking; from another pointer it changes nothing.

- [x] **Step 1: Write the failing tests**

  | Test | Actions | Assertions |
  |------|---------|------------|
  | `c24FiresOnceThresholdPassed` | `down(1, 0, 0)`, `move(1, 30, 0, 48)`, `move(1, 50, 5, 48)` | `null`, then `RIGHT` with origin `(50, 5)` |
  | `c24DominantAxis` | from `(0,0)`: `(-100, 10)`, `(5, -90)`, `(5, 90)` | `LEFT`, `UP`, `DOWN` |
  | `c24ThresholdInclusive` | from `(0,0)`: `(48, 0)`; `(47.9, 0)` | `RIGHT`; `null` |
  | `c24OneStrokeTurnsTwice` | `down(1,0,0)`, `move(1,50,5)`, `move(1,52,-50)` | `RIGHT`, then `UP` |
  | `c24ExactDiagonalIsNoTurn` | from `(0,0)`: `(60, -60)` | `null`, origin still `(0,0)` |
  | `c24NonFiniteIsNoTurn` | `(NaN, 100)`, `(+∞, 0)` | `null` |
  | `c24OnlyFirstFingerCounts` | `down(1,0,0)`, `down(2,500,500)`, `move(2,900,500)` | tracker still pointer 1 at `(0,0)`; no turn |
  | `c24LiftAndNewStroke` | `down(1,…)`, `up(2)`, `up(1)`, `down(2,10,10)`, `move(2,70,10)` | `up(2)` changes nothing; after `up(1)` nothing tracked; pointer 2 then turns `RIGHT` |
  | `moveWithoutDownIsIgnored` | fresh tracker, `move(1, 100, 0, 48)` | `null`, tracker unchanged |
  | `keysMapToInput` | `GameKey.entries.map { it.input }` | `[Turn(UP), Turn(DOWN), Turn(LEFT), Turn(RIGHT), TogglePause]` |

- [x] **Step 2: Run them and confirm they fail**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test --tests '*SwipeTrackerTest' --tests '*GameInputTest'`
Expected: compilation fails with `Unresolved reference 'SwipeTracker'`.

- [x] **Step 3: Implement `SwipeTracker` and `GameKey.input`**

- [x] **Step 4: Run them and confirm they pass**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test :domain:koverVerify`
Expected: `BUILD SUCCESSFUL`.

- [x] **Step 5: Finish per the per-task workflow**

---

### Task 5: High scores, settings and their use cases

**Branch:** `feat/scores-and-settings` · **PR title:** `feat: add high score and settings use cases`

**Files:**
- Modify: `gradle/libs.versions.toml` (`coroutines = "1.11.0"`, libraries `kotlinx-coroutines-core`, `kotlinx-coroutines-test`), `domain/build.gradle.kts` (`api(libs.kotlinx.coroutines.core)`, because public signatures expose `Flow`; `testImplementation(libs.kotlinx.coroutines.test)`), `gradle/verification-metadata.xml`, `docs/versions.md`
- Create in `domain/src/main/…/domain/`: `HighScore.kt` (`HighScore`, `HighScoreTable`, `Placement`), `Settings.kt` (`Settings`, `ControlScheme`), `Repositories.kt` (both interfaces), `RecordHighScore.kt` (`RecordHighScore`, `ScoreResult`), `HighScores.kt`, `SettingsActions.kt`
- Create in `domain/src/test/…/domain/`: `HighScoreTableTest.kt`, `RecordHighScoreTest.kt`, `HighScoresTest.kt`, `SettingsActionsTest.kt`, `FakeRepositories.kt` (in-memory fakes on `MutableStateFlow`), `FakeClock.kt` (`class FakeClock(var now: Long = 0, var date: LocalDate = LocalDate.of(2026, 10, 28)) : Clock`)

**Interfaces:**
- Consumes: Task 1 `Difficulty`, `Clock`.
- Produces:
  - `data class HighScore(val score: Int, val difficulty: Difficulty, val date: LocalDate)`
  - `data class Placement(val table: List<HighScore>, val rank: Int?)`, rank 1-based, `null` when the entry did not make the table
  - `object HighScoreTable { const val CAPACITY = 10; fun insert(table: List<HighScore>, entry: HighScore): Placement; fun normalize(entries: List<HighScore>): List<HighScore> }`
  - `enum class ControlScheme { SWIPE, DPAD }`
  - `data class Settings(val difficulty: Difficulty = Difficulty.LEVEL_1, val soundOn: Boolean = true, val hapticsOn: Boolean = true, val controlScheme: ControlScheme = ControlScheme.SWIPE)`. **[builder] B1:** these defaults.
  - `interface HighScoreRepository { val entries: Flow<List<HighScore>>; suspend fun update(transform: (List<HighScore>) -> List<HighScore>) }`
  - `interface SettingsRepository { val settings: Flow<Settings>; suspend fun update(transform: (Settings) -> Settings) }`
  - `data class ScoreResult(val rank: Int?, val isNewBest: Boolean)`
  - `class RecordHighScore(private val repository: HighScoreRepository, private val clock: Clock) { suspend operator fun invoke(score: Int, difficulty: Difficulty): ScoreResult }`
  - `class HighScores(repository: HighScoreRepository) { val top: Flow<List<HighScore>>; val best: Flow<Int> }`
  - `class SettingsActions(private val repository: SettingsRepository) { val settings: Flow<Settings>; suspend fun setDifficulty(value: Difficulty); suspend fun setSound(on: Boolean); suspend fun setHaptics(on: Boolean); suspend fun setControlScheme(value: ControlScheme) }`

- [x] **Step 1: Verify and add the coroutines dependencies**

Re-check `https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-core/maven-metadata.xml` for the newest stable (no alpha, beta, RC). Use it; record core and test in `docs/versions.md` with the URL, the POM Last-Modified date and today's date. Re-check CodeQL's Kotlin line too.

- [x] **Step 2: Write the failing tests**

  | Test | Assertions |
  |------|------------|
  | `c12InsertKeepsDescendingOrder` | inserting 50 into `[80, 20]` gives `[80, 50, 20]`, rank 2 |
  | `c22TieGoesBelowExisting` | inserting 50 into `[50]` (older date) gives rank 2, older entry first |
  | `c22FullTableNeedsToBeatTenth` | ten scores `100, 90, …, 10`: inserting 10 → rank `null`, table unchanged; inserting 11 → rank 10, last is 11, size 10 |
  | `c22ZeroIsNotSaved` | `insert([], score 0)` → rank `null`, table empty |
  | `normalizeSortsCapsAndDropsNonPositive` | 12 mixed entries including 0 and −3 → 10 entries, descending, all > 0, ties in input order |
  | `c12RecordStoresWithTodaysDate` | `FakeClock(date = 2026-10-28)`, record 30 at `LEVEL_2` → repository holds `HighScore(30, LEVEL_2, 2026-10-28)`, result `ScoreResult(1, true)` |
  | `c22EquallingBestIsNotNewBest` | repository `[40]`, record 40 → `ScoreResult(2, false)` |
  | `recordBelowBestIsNotNewBest` | repository `[40]`, record 30 → `ScoreResult(2, false)` |
  | `recordOffTableIsNeither` | ten entries ≥ 50, record 10 → `ScoreResult(null, false)`, repository unchanged |
  | `bestIsTopScoreOrZero` | empty → `0`; `[70, 20]` → `70` (`HighScores.best.first()`) |
  | `settingsActionsUpdateOneField` | each setter changes only its field; `settings.first()` defaults equal `Settings()` |

Use `kotlinx.coroutines.test.runTest`.

- [x] **Step 3: Run them and confirm they fail**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test`
Expected: compilation fails with `Unresolved reference 'HighScoreTable'`.

- [x] **Step 4: Implement the types above**

`HighScoreTable.normalize` drops scores ≤ 0, sorts by score descending with a stable sort (input order breaks ties, so stored order is kept), and keeps the first 10. `insert` appends the entry and normalizes; its rank is its index + 1 if it survived. `RecordHighScore` computes the `Placement` inside `repository.update { … }` and returns its rank, so the read and the write are one atomic edit. `HighScores.best` is `top.map { it.firstOrNull()?.score ?: 0 }`.

- [x] **Step 5: Run them and confirm they pass**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test :domain:koverVerify`
Expected: `BUILD SUCCESSFUL`.

- [x] **Step 6: Refresh dependency verification**

Run `docs/versions.md` "Dependency verification updates" step 2 (fresh `GRADLE_USER_HOME`, `--refresh-dependencies`), then `git diff --stat gradle/verification-metadata.xml`.
Expected: new `org.jetbrains.kotlinx` coroutines components; `:checkNoComments` passes. If CI later names a Linux-only artifact, follow step 4 of the same section.

- [x] **Step 7: Finish per the per-task workflow**

The PR body's Evidence includes the version sources from Step 1.

---

### Task 6: Step timing, countdown and the game session

**Branch:** `feat/game-session` · **PR title:** `feat: drive countdown, steps and announcements`

**Files:**
- Create in `domain/src/main/…/domain/`: `TickTimer.kt` (`TickTimer`, `TickAdvance`), `Announcement.kt`, `GameSession.kt` (`GameSession`, `GameFrame`)
- Create in `domain/src/test/…/domain/`: `TickTimerTest.kt`, `GameSessionTest.kt`

**Interfaces:**
- Consumes: Task 2 `GameRules.newGame`, `tick`; Task 3 `Turns.enqueue`, `go`, `pause`, `resume`; Task 4 `GameInput`; Task 5 `RecordHighScore`, `ScoreResult`, `FakeClock`, fake repositories; `FixedRandom`.
- Produces:
  - `data class TickTimer(val intervalMillis: Long, val lastTickMillis: Long) { fun advance(nowMillis: Long): TickAdvance; fun progress(nowMillis: Long): Float }`
  - `data class TickAdvance(val ticked: Boolean, val timer: TickTimer)`
  - `sealed interface Announcement { data class Countdown(val number: Int); data object Go; data object Paused; data class OrbMilestone(val orbs: Int); data class GameOver(val score: Int); data class Won(val score: Int); data class NewHighScore(val score: Int) }`, each `: Announcement`. `Go` is the game-start announcement (C25).
  - `data class GameFrame(val state: GameState, val previousSnake: List<Cell>, val progress: Float, val countdown: Int?, val events: List<GameEvent>, val announcements: List<Announcement>, val result: ScoreResult?)`. `countdown` is 3, 2 or 1 while counting down, else `null`.
  - `class GameSession internal constructor(initial: GameState, private val clock: Clock, private val random: RandomSource, private val recordHighScore: RecordHighScore) { constructor(difficulty: Difficulty, clock: Clock, random: RandomSource, recordHighScore: RecordHighScore); var frame: GameFrame; private set; fun input(input: GameInput): GameFrame; fun pause(): GameFrame; suspend fun advance(): GameFrame; companion object { const val COUNTDOWN_FROM = 3; const val COUNTDOWN_STEP_MILLIS = 1000L } }`
  - The public constructor starts from `GameRules.newGame(difficulty, random)`; tests use the internal one to start from a crafted state (its status is used as given). A new game, Restart and Play again are each a new `GameSession` (C13). `:app` calls `pause()` on background and rotation (C20) and `advance()` once per rendered frame.

Session behaviour (C13, C19, C20, C21, C22, C25):
- **Countdown.** Starts when the session is built and on every resume, at `clock.nowMillis()`. The shown number is `3 − elapsed / 1000` for `elapsed` in `0..2999` (a negative elapsed shows 3). The frame that enters a number announces `Countdown(n)`; the first frame is `Countdown(3)`. When `elapsed ≥ 3000`: `GameRules.go`, announce `Go`, and start the timer at that frame's `now`, so the first step is one interval later. **[builder] B2:** numbers skipped by a stall are not announced; the frame jumps to the current number or to `Go`.
- **Steps.** In `RUNNING`, `advance` asks the timer; on a tick it runs `GameRules.tick` and sets `previousSnake` to the snake before the step. Without a tick, `previousSnake` is kept.
- **Announcements.** `ORB_EATEN` with `orbsEaten % 10 == 0` → `OrbMilestone(orbsEaten)`. `LOST` → `GameOver(score)`; `WON` → `Won(score)`; then, if the recorded result is a new best, `NewHighScore(score)`.
- **End.** On `LOST` or `WON`, call `recordHighScore(score, difficulty)` once, in that same `advance`, before returning (C22). The result stays on every later frame. On the `LOST` frame and after, `previousSnake == state.snake` and `progress == 1f`, so nothing glides into the wall (C21).
- **Pause and input.** `pause()` and `TogglePause` from `COUNTDOWN`/`RUNNING` → `GameRules.pause`, announce `Paused`. `TogglePause` from `PAUSED` → `GameRules.resume` and restart the countdown. `Turn` → `Turns.enqueue`. Anything else changes nothing and announces nothing.
- **Glide while still.** Pausing sets `previousSnake = state.snake`. While `COUNTDOWN` or `PAUSED`, `progress` is `0f` and `previousSnake == state.snake`, so the board shows the snake still on its cells and nothing jumps on resume.
- **Internal constructor.** If the given state is `COUNTDOWN`, the countdown starts at `clock.nowMillis()`; if `RUNNING`, the timer starts there, so the first step is one interval later. The first frame announces `Countdown(3)` only in the first case.

- [x] **Step 1: Write the failing `TickTimerTest`** (C19)

  | Test | Timer | Call | Expected |
  |------|-------|------|----------|
  | `noStepBeforeInterval` | `(100, 0)` | `advance(99)` | `ticked == false`, timer unchanged |
  | `jitterKeepsRhythm` | `(100, 0)` | `advance(130)` | `ticked`, `lastTickMillis == 100` |
  | `stallStepsOnceAndDropsLeftover` (Review Focus 2) | `(100, 0)` | `advance(2000)` | `ticked`, `lastTickMillis == 2000` |
  | `exactlyTwoIntervalsIsAStall` | `(100, 0)` | `advance(200)` | `ticked`, `lastTickMillis == 200` |
  | `backwardsClockDoesNothing` (Review Focus 2) | `(100, 500)` | `advance(400)`, `progress(400)` | no step, timer unchanged, `0f` |
  | `progressIsClamped` | `(100, 0)` | `progress(50)`, `progress(250)` | `0.5f`, `1f` |

- [x] **Step 2: Write the failing `GameSessionTest`**

Each test builds the session with `LEVEL_1` (250 ms steps), `FakeClock(now = 0)`, a `FixedRandom` and `RecordHighScore(fakeRepo, clock)`, and moves `clock.now` by hand. A helper `skipCountdown()` sets `now += 3000` and calls `advance()`.

  | Test | Actions | Assertions |
  |------|---------|------------|
  | `c13StartsCountingDown` | construct | `COUNTDOWN`, `countdown == 3`, `announcements == [Countdown(3)]`, `previousSnake == state.snake` |
  | `c13c25CountsDownThenGo` | `now` = 999, 1000, 2000, 2999, 3000, each `advance()` | announcements `[]`, `[Countdown(2)]`, `[Countdown(1)]`, `[]`, `[Go]`; status `RUNNING` and `countdown == null` at 3000; head still `(10,10)` |
  | `c13FirstStepOneIntervalAfterGo` | `skipCountdown()`, then `now = 3249`, `3250` | no step at 3249; head `(11,10)` at 3250, `previousSnake` head `(10,10)` |
  | `c14TurnDuringCountdownUsedFirst` | `input(Turn(UP))` at 0, `skipCountdown()`, `now = 3250` | head `(10,9)` |
  | `stallDuringCountdownJumpsToGo` (Review Focus 2) | `now = 3500; advance()` | `[Go]` only, `RUNNING` |
  | `c23ProgressBetweenSteps` | `skipCountdown()`, `now = 3125` | `progress == 0.5f` |
  | `c20PauseDuringCountdownRestartsIt` (Review Focus 5) | `input(Turn(UP))`, `now = 1500`, `pause()`, `now = 9000`, `input(TogglePause)` | after pause: `PAUSED`, `[Paused]`, queue empty; after resume: `COUNTDOWN`, `[Countdown(3)]`; `Go` at `now = 12000`, not before |
  | `c20ResumeRunsCountdownBeforeMoving` | `skipCountdown()`, `pause()`, `now = 5000`, `input(TogglePause)`, then `now = 7999`, `8000`, `8250` | no movement until 8250; `Go` at 8000 |
  | `pauseIsIdempotent` | `pause()` twice | second frame has no announcements |
  | `everyTenthOrbIsAnnounced` | internal constructor: running state with `orbsEaten = 9`, orb at `(11,10)`, timer from `now`; step. Then `orbsEaten = 10`, same setup; step | first: `[OrbMilestone(10)]`; second: `[]` |
  | `c22GameOverSavesAndAnnounces` | `FixedRandom(208, 0)` puts the first orb on `(11,10)` (index 211 minus the 3 snake cells before it) and the next on `(0,0)`; skip the countdown and step until the head leaves the right wall (score 1); empty repository | final frame: `LOST`, `announcements == [GameOver(1), NewHighScore(1)]`, `result == ScoreResult(1, true)`, repository holds the entry |
  | `c21CrashFrameDoesNotGlide` | the `LOST` frame above | `previousSnake == state.snake`, `progress == 1f` |
  | `c22ZeroScoreNotSaved` | crash with score 0 | `[GameOver(0)]`, `result == ScoreResult(null, false)`, repository empty |
  | `c9c22WinSavesAndAnnounces` | internal constructor with Task 2's `c9FillingTheBoardWins` state as `RUNNING`; step | `WON`, `[Won(score), NewHighScore(score)]` |
  | `afterTheEndNothingChanges` | `advance()`, `input(Turn(UP))`, `pause()` after `LOST` | same state, no events, no announcements, `result` kept |

- [x] **Step 3: Run them and confirm they fail**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test --tests '*TickTimerTest' --tests '*GameSessionTest'`
Expected: compilation fails with `Unresolved reference 'TickTimer'`.

- [x] **Step 4: Implement `TickTimer` and `GameSession`**

`TickTimer.advance`: `elapsed = now − last`; `elapsed < interval` → no step; `elapsed < 2 × interval` → step, `last + interval`; else step, `now`. `progress` is `elapsed / interval` clamped to `0f..1f`.

- [x] **Step 5: Run them and confirm they pass**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test :domain:koverVerify`
Expected: `BUILD SUCCESSFUL`.

- [x] **Step 6: Finish per the per-task workflow**

---

### Task 7: Render maths

**Branch:** `feat/render-maths` · **PR title:** `feat: add board layout, glide, pulse and particle maths`

**Files:**
- Create in `domain/src/main/…/domain/`: `BoardLayout.kt` (`BoardLayout`, `BoardFrame`), `SnakeMotion.kt` (`SnakeMotion`, `Point`), `Pulse.kt`, `Particles.kt` (`Particles`, `Particle`)
- Create in `domain/src/test/…/domain/`: one test file per main file

**Interfaces:**
- Consumes: `Board`, `Cell`; `GameFrame.previousSnake`, `.state.snake`, `.progress` from Task 6.
- Produces, all in cell units except `BoardFrame`:
  - `data class BoardFrame(val left: Float, val top: Float, val cellSize: Float)` in pixels; `object BoardLayout { fun fit(width: Float, height: Float): BoardFrame }` (C1)
  - `data class Point(val x: Float, val y: Float)`; `object SnakeMotion { fun interpolate(previous: List<Cell>, current: List<Cell>, progress: Float): List<Point> }` (C23)
  - `object Pulse { const val PERIOD_MILLIS = 1200L; fun brightness(nowMillis: Long, reduceMotion: Boolean): Float }`
  - `data class Particle(val x: Float, val y: Float, val alpha: Float)`; `object Particles { const val COUNT = 12; const val LIFETIME_MILLIS = 400L; const val TRAVEL_CELLS = 1.2f; fun burst(origin: Cell, startMillis: Long, nowMillis: Long, reduceMotion: Boolean): List<Particle> }`
  - `:board` (Plan 3) only multiplies by `cellSize` and adds `left`/`top`.

**[builder] values:** B3: pulse brightness `0.8 + 0.2 × sin(2π × t / 1200 ms)`, constant `1.0` with reduce motion. B4: an orb burst is 12 particles at even angles from the cell centre, travelling 1.2 cells with ease-out `1 − (1 − t)²` over 400 ms, alpha `1 − t`; none with reduce motion (main spec §3).

- [x] **Step 1: Write the failing tests**

  | Test | Assertions |
  |------|------------|
  | `c1FitLetterboxesPortrait` | `fit(1000f, 1600f) == BoardFrame(0f, 300f, 50f)` |
  | `c1FitLetterboxesLandscape` | `fit(1600f, 1000f) == BoardFrame(300f, 0f, 50f)` |
  | `fitZeroOrNegativeIsEmpty` (Review Focus 4) | `fit(0f, 0f)`, `fit(-5f, 100f)`: `cellSize == 0f`, no `NaN` in any field |
  | `c23GlideMovesEachSegment` | previous `(10,10),(9,10),(8,10)`, current `(11,10),(10,10),(9,10)`, progress `0.5f` → `(10.5,10),(9.5,10),(8.5,10)` |
  | `c23GrowthHoldsNewTail` | current has one more segment than previous; the extra tail point equals the previous last cell at every progress |
  | `glideClampsProgress` | progress `-1f` equals `0f`; `2f` equals `1f` |
  | `glideFirstFrame` | previous empty → current cells as points |
  | `pulseRangeAndPeriod` | `brightness(0, false) == 0.8f`, `brightness(300, false) == 1.0f` (±1e-4), `brightness(900, false) == 0.6f` (±1e-4), `brightness(1200, false) == brightness(0, false)` |
  | `pulseStaticWithReduceMotion` | `brightness(300, true) == 1f` |
  | `burstStartsAtCentre` | `burst(Cell(4,4), 0, 0, false)`: 12 particles, all at `(4.5, 4.5)`, alpha 1 |
  | `burstEasesOut` | at 200 ms: each particle at distance `1.2 × 0.75 = 0.9` from the centre (±1e-4), alpha `0.5`; the first particle's angle is 0 (towards `+x`) |
  | `burstEndsAndRespectsReduceMotion` | at 400 ms, at −1 ms, and with `reduceMotion = true`: empty |

- [x] **Step 2: Run them and confirm they fail**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test`
Expected: compilation fails with `Unresolved reference 'BoardLayout'`.

- [x] **Step 3: Implement the four files**

`fit`: `side = max(0f, min(width, height))`, `cellSize = side / Board.SIZE`, `left = (max(0f, width) − side) / 2`, `top` likewise. `interpolate`: point `i` goes from `previous.getOrElse(i) { previous.lastOrNull() ?: current[i] }` to `current[i]`.

- [x] **Step 4: Run them and confirm they pass**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test :domain:koverVerify`
Expected: `BUILD SUCCESSFUL`.

- [x] **Step 5: Finish per the per-task workflow**

---

### Task 8: `:data` on DataStore core

**Branch:** `feat/data-datastore` · **PR title:** `feat: persist settings and high scores with DataStore`

**Files:**
- Create: `data/build.gradle.kts` (same shape as `domain/build.gradle.kts`, plus `implementation(project(":domain"))`, `implementation(libs.androidx.datastore.core)`, `testImplementation(libs.kotlinx.coroutines.test)`)
- Create in `data/src/main/kotlin/io/github/eastwoodno1/supersnake/data/`: `Stores.kt`, `SettingsSerializer.kt`, `HighScoresSerializer.kt`, `DataStoreSettingsRepository.kt`, `DataStoreHighScoreRepository.kt`
- Create in `data/src/test/kotlin/io/github/eastwoodno1/supersnake/data/`: one test file per main file
- Create: `docs/adr/0008-datastore-core-in-jvm-data.md`
- Modify: `settings.gradle.kts` (`include(":data")`), `build.gradle.kts` (`kover(project(":data"))`), `gradle/libs.versions.toml` (`datastore = "1.3.0"`, library `androidx-datastore-core` = `androidx.datastore:datastore-core`), `gradle/verification-metadata.xml`, `docs/versions.md`, `CONTRIBUTING.md` (the coverage row: drop "(`:data` once it exists)" and name `:data` beside `:domain`)

**Interfaces:**
- Consumes: Task 5 `HighScoreRepository`, `SettingsRepository`, `HighScore`, `HighScoreTable.normalize`, `Settings`, `ControlScheme`; `Difficulty`.
- Produces (Plan 3's composition root uses these):
  - `object Stores { const val SETTINGS_FILE = "settings.txt"; const val HIGH_SCORES_FILE = "high_scores.txt"; fun settings(file: java.io.File, scope: CoroutineScope): DataStore<Settings>; fun highScores(file: java.io.File, scope: CoroutineScope): DataStore<List<HighScore>> }`, built with `DataStoreFactory.create(serializer, scope = scope, produceFile = { file })`.
  - `object SettingsSerializer : Serializer<Settings>`: UTF-8 lines `key=value` with keys `difficulty`, `sound`, `haptics`, `control_scheme`. **[builder] B5:** enum names; booleans `true`/`false`; unknown keys and lines without `=` ignored; a missing or unparsable value takes that field's `Settings()` default. `readFrom` never throws.
  - `object HighScoresSerializer : Serializer<List<HighScore>>`: one entry per line, `score|DIFFICULTY_NAME|yyyy-MM-dd` (**[builder] B6**). `readFrom` drops lines that don't parse, applies `HighScoreTable.normalize`, and never throws.
  - `class DataStoreSettingsRepository(private val store: DataStore<Settings>) : SettingsRepository` and `class DataStoreHighScoreRepository(private val store: DataStore<List<HighScore>>) : HighScoreRepository`: `data` as the flow, `updateData` for `update` (high scores normalized on write).
  - Decoding is malformed-input tolerant (`decoder.onMalformedInput(CodingErrorAction.REPLACE)` or `String(bytes, UTF_8)`), which is why no corruption handler is needed. Parse enums with `entries.firstOrNull { it.name == value }`, never `valueOf`.

- [ ] **Step 1: Prove `datastore-core` in a plain Kotlin JVM module**

1. Re-check `https://dl.google.com/android/maven2/androidx/datastore/datastore-core/maven-metadata.xml` for the newest stable, and read its `-jvm` `.module` file's `jvmRuntimeElements-published` dependencies. Expected on 2026-10-28: `androidx.annotation:annotation`, `kotlin-stdlib`, `kotlinx-coroutines-core`. If anything outside `androidx.*`, `org.jetbrains.*` appears, stop and ask the owner.
2. Create the module files from the list above with only `Stores.kt` and a smoke test that writes a `Settings` through `Stores.settings` into a `@TempDir` file and reads it back from a second store on the same file.
3. Run `~/.local/bin/mise exec -- ./gradlew :data:test :data:dependencies --configuration runtimeClasspath`.

Expected: `BUILD SUCCESSFUL`, and the runtime classpath has no `com.squareup` or other third-party group. **If it fails or a third-party group appears, stop and ask the owner** (design §3); do not choose a fallback.

- [ ] **Step 2: Write the failing tests**

Store tests use a `@TempDir` file and a store scope `CoroutineScope(Dispatchers.IO + Job())` cancelled in `@AfterEach`, inside `runTest`.

  | Test | Assertions |
  |------|------------|
  | `settingsRoundTrip` | write `Settings(LEVEL_7, false, false, DPAD)` to a byte stream, read it back → equal |
  | `settingsEmptyIsDefault` | reading `""` → `Settings()` |
  | `settingsFallBackFieldByField` (Review Focus 3) | `"difficulty=LEVEL_42\nsound=maybe\nhaptics=false\ncontrol_scheme=JOYSTICK\nnoise\nextra=1\n"` → `Settings(hapticsOn = false)` |
  | `highScoresRoundTrip` | three entries across `ASSIST` and `LEVEL_9` round-trip equal |
  | `highScoresDropBadLines` (Review Focus 3) | `"30|LEVEL_2|2026-10-28\nnope\n5|LEVEL_X|2026-10-28\n7|ASSIST|2026-13-40\n|\n-4|LEVEL_1|2026-10-28\n"` → only the first entry |
  | `randomBytesReadAsDefaults` (Review Focus 3) | `byteArrayOf(0, -1, 19, -128, 10, 61, -2)` → `Settings()` and an empty list |
  | `settingsPersistAcrossStores` | `update { Settings(LEVEL_3) }`, cancel the scope, new store on the same file → equal |
  | `highScoresPersistAcrossStores` | `update { listOf(entry) }`, reopen → `listOf(entry)` |
  | `corruptFileOnDiskStillWorks` (Review Focus 3) | write the random bytes to the file, open both stores → defaults; then an `update` succeeds and reads back |
  | `fileNamesAreStable` | `Stores.SETTINGS_FILE == "settings.txt"`, `Stores.HIGH_SCORES_FILE == "high_scores.txt"` (Plan 3's backup rules name them) |

- [ ] **Step 3: Run them and confirm they fail**

Run: `~/.local/bin/mise exec -- ./gradlew :data:test`
Expected: compilation fails with `Unresolved reference 'SettingsSerializer'`.

- [ ] **Step 4: Implement the serializers and repositories**

- [ ] **Step 5: Run them and confirm they pass**

Run: `~/.local/bin/mise exec -- ./gradlew :data:test :data:koverVerify`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 6: Write ADR-0008 and refresh verification**

ADR-0008 "DataStore core with our own serializers in a pure JVM `:data`", in the format of `docs/adr/0003-no-di-framework.md`:
- context: main spec §12 left the module kind open; `datastore-preferences` needs okio
- decision: owner, 2026-10-28, design §3
- the dependency evidence from Step 1
- consequences: no third-party runtime code, our serializers sit under the 100% gate, Plan 3 places the two files in `filesDir/datastore/` and names them in the backup rules

Then run `docs/versions.md` "Dependency verification updates" step 2 and record `datastore-core` in `docs/versions.md` (and mark the `datastore-preferences` line in §11 as not used, if `docs/versions.md` lists it).

Expected: `git diff --stat gradle/verification-metadata.xml` shows `androidx.datastore` and `androidx.annotation` components and no `com.squareup`.

- [ ] **Step 7: Finish per the per-task workflow**

From this task on, the strict gate includes `:data:koverVerify` and Evidence includes `:data`'s coverage counters.

---

### Task 9: Close Plan 2

**Branch:** `docs/close-plan-2` · **PR title:** `docs: mark Plan 2 complete and hand off to Plan 3`

**Files:**
- Modify: this plan's "State at handoff", `README.md` (the plans paragraph), `docs/game-rules.md` (B1–B6 present). `CLAUDE.md` keeps pointing here until Plan 3 is written.

- [ ] **Step 1: Prove the whole engine on `main`'s head**

From the worktree:

```sh
~/.local/bin/mise exec -- scripts/gradle-strict.sh check :checkNoComments :domain:koverVerify :data:koverVerify
~/.local/bin/mise exec -- ./gradlew :domain:koverXmlReport :data:koverXmlReport
```

Expected: `BUILD SUCCESSFUL`; the per-task workflow's counter command shows `missed="0"` for LINE and BRANCH in both modules.

- [ ] **Step 2: Check rule coverage**

For each of S1–S5 and C1–C25, name the test that proves it, found with `grep -rn -i '<rule id>' domain/src/test data/src/test`. Rules that only Plan 3 can prove (C10/C20's lifecycle trigger, C11 "no multiplayer code", C25's TalkBack wiring, C24's dp value) say so. Put the table in the PR body.

- [ ] **Step 3: Confirm no plugin drift**

Run: `/Users/r03418/AndroidStudioProjects/android-sdlc-skills/scripts/verify-templates.sh /Users/r03418/AndroidStudioProjects/SuperSnake-close-plan-2`
Expected: `0 drift`. If anything drifted, stop: a task changed a file the plugin copies, and the plugin repo needs its own PR first.

- [ ] **Step 4: Update the handoff text**

"State at handoff": Plan 2 complete, PR numbers, `main` head, Dependabot state, next action "write Plan 3". README: "Plan 2 (game engine) is complete. Plan 3 is next."

- [ ] **Step 5: Finish per the per-task workflow**
