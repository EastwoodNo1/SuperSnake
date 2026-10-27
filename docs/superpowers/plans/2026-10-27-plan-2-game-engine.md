# SuperSnake Plan 2: Game Engine Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development to implement this plan task-by-task (owner's choice for Plan 1, carried over). Every subagent runs on model Sonnet 5.5: pass `model: "sonnet"` on each Agent call. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Every game decision lives in `:domain` and `:data`, as pure Kotlin JVM code at 100% line and branch coverage, so Plan 3 can build the UI and board on top without adding a single rule.

**Architecture:**
- `:domain` (exists, package `io.github.eastwoodno1.supersnake.domain`) gains, in one flat package with one concern per file:
  - the rules engine: S1–S5 and C1–C11, as pure functions of state, input and an injected random source
  - turn buffering, pause and tick timing
  - input classification: swipe vector and platform-neutral key → game input
  - render maths: letterbox layout, segment interpolation, glow pulse, particle bursts
  - high scores (C12) and settings: models, table rules, repository interfaces, use cases
  - `GameSession`: the one stateful object a ViewModel drives, so ViewModels never branch (spec §5)
- `:data` (new, Kotlin JVM, package `io.github.eastwoodno1.supersnake.data`) implements the two repository interfaces on `androidx.datastore:datastore-preferences-core`, which is a JVM artifact. This settles spec §12 "whether `:data` can be pure JVM": yes.
- Neither module knows Android, libGDX, Compose or the system clock. `:app` (Plan 3) supplies `Clock` and `RandomSource` adapters and the DataStore files.

**Tech Stack:** Kotlin 2.4.20 (JVM toolchain 25), kotlinx-coroutines 1.11.0 (core, test), DataStore Preferences core 1.3.0, JUnit 6.1.3 + kotlin-test, Kover 0.9.11, ktlint 1.8.0.

**Spec:** `docs/superpowers/specs/2026-10-22-supersnake-design.md`, approved by the owner on 2026-10-22. The rules are also in `docs/game-rules.md`. Plan 1, `docs/superpowers/plans/2026-10-22-plan-1-foundation.md`, built the repo, gates, hooks and CI this plan relies on.

## Start here (for a fresh session)

Together with the spec, this plan is self-contained. A new session needs nothing from the conversation that wrote it. Statements marked "confirmed by the owner" record decisions the owner made. Treat them as settled.

`README.md` (for humans) and `CLAUDE.md` (for agents) at the repo root point here.

### To start or resume

**AI agent (Claude Code):** open Claude Code in `/Users/r03418/AndroidStudioProjects/SuperSnake` and say:

> Execute `docs/superpowers/plans/2026-10-27-plan-2-game-engine.md` with superpowers:subagent-driven-development, starting at the first task that is not done.

**Human:** work through the tasks in order, by hand. Every step gives the command and the output that means it passed.

### Read first, in this order

1. This file, all of it.
2. The spec: §2 (rules), §5 (architecture), §6 (dependencies), §7 (SDLC), §12 (left to the builder).
3. `CONTRIBUTING.md`: the per-change flow and how each rule is enforced.

### Progress lives in git

- **A task is done** when all of its checkboxes are ticked in this file on `origin/main`. The task's PR ticks them, so the ticks land together with the work.
- **A task is in progress** if any of these exists for it: a worktree (`git worktree list`), its branch (named in its **Branch** line), or an open PR (`gh pr list`). Continue in that worktree. Ticked steps there are done; re-run the first unticked step's verification before moving on.
- **A task is not started** if none of those exist.
- Run `git pull` in the main checkout first, and use `origin/main` as the record.
- **Start from the first task that is not done.** Never skip a task: each one consumes the previous one's types.

### State at handoff (2026-10-27)

- **Plan 1 is complete** (PRs #2 to #45). Plan 2 is written; no Plan 2 task has started. Run `git log --oneline` for the current head.
- **Plugin:** `EastwoodNo1/android-sdlc-skills` at `v0.2.0`, installed through `.claude/settings.json`. `scripts/verify-templates.sh` printed `35 templates checked` / `0 drift` against `main` on 2026-10-27. No task in this plan changes a file the plugin copies; Task 9 re-runs the check to prove it.
- **Dependabot PR #41** (Kotlin 2.4.20 → 2.4.21) stays open: CodeQL's supported-languages page still read "Kotlin 1.8.0 to 2.4.20" on 2026-10-27 (ADR-0006). Each task's version step re-checks it; when CodeQL lists 2.4.21, handle #41 as `docs/versions.md` "Dependency verification updates" says, as its own PR, between tasks.
- **ADLC capture is on.** `staged/` was empty on 2026-10-27. Every PR runs the fold-in in the per-task workflow below.
- **Never commit** `local.properties`, `.idea/`, `.gradle/`, `.superpowers/` or `.claude/settings.local.json`.

### Machine prerequisites

Plan 1's table ("Machine prerequisites") lists each tool and its check command; all were current on 2026-10-26. This plan needs only mise, JDK 25 through mise, gh as `EastwoodNo1` and git. Re-run:

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
- **Plan approval covers:** one GitHub issue and one PR per task in this repo, and adding `kotlinx-coroutines-core`, `kotlinx-coroutines-test` and `datastore-preferences-core`.
- **Stop and ask the owner before:**
  - anything outward-facing this plan doesn't list
  - deleting anything this plan doesn't list
  - any change to a decision in the spec. The **[builder]** calls in this plan fill gaps the spec leaves; Task 1 to Task 8 record each in `docs/game-rules.md`. Task 8 has one explicit owner checkpoint (okio).

### After Plan 2 is merged

- Start a fresh session and use superpowers:writing-plans to write Plan 3 (App) from the spec and the Roadmap below, re-verifying versions and CodeQL's Kotlin support.
- Give Plan 3 the same "Start here" section, updated with the state at that point.

## Roadmap (this plan is 2 of 4)

1. **Foundation.** Done (Plan 1).
2. **Game engine (this plan).** `:domain` rules S1–S5 and C1–C12, input classification, render maths, use cases; `:data` DataStore repositories; all at 100% coverage.
3. **App.** Neon Compose UI, splash, libGDX `:board` embedded in Compose, controls, accessibility, UI tests on 3 emulator profiles, 16 KB alignment check, final manifest hardening (backup rules naming the two DataStore files from Task 8), assets. The `Clock` and `RandomSource` adapters, `MotionEvent`/`KeyEvent` → `GameKey`/swipe translation, and the "remove animations" lookup live here. `android-ci` gains the UI-test job. Any new decision Plan 3 meets, such as which `GameEvent` plays a sound or haptic under the current `Settings`, goes into `:domain` with tests, never into a ViewModel.
4. **Release.** As recorded in Plan 1's Roadmap, including its open design point on the `release` environment.

## Global Constraints

Copied from the spec and Plan 1. Every task's requirements include this section.

**Code and commits**
- **No comments** in `*.kt`, `*.kts`, `*.xml`, `*.toml`, `*.yml`, `*.yaml`, `*.sh`, `*.pro`, `*.properties`. Strip comments tools write.
- **Commits:** Conventional Commits; a body with **Why:** and **What:** for `feat`, `fix`, `refactor`, `perf` (and others when there is a why). Subagent commits end with `Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>`; the orchestrator uses the line its own session's system reminder gives. Never copy another model's line.
- **Evidence:** every "passes / works / done" claim carries the exact command and its real output, or a CI run link.

**Versions and dependencies** (verified 2026-10-27)

| Item | Version | Source |
|------|---------|--------|
| Kotlin | 2.4.20 (2.4.21 exists; capped by CodeQL, which lists "Kotlin 1.8.0 to 2.4.20") | repo1 `kotlin-gradle-plugin/maven-metadata.xml`; codeql.github.com/docs/codeql-overview/supported-languages-and-frameworks/ |
| kotlinx-coroutines-core, kotlinx-coroutines-test | 1.11.0 (POM Last-Modified 2026-05-07) | https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-core/maven-metadata.xml |
| datastore-preferences-core | 1.3.0 (POM Last-Modified 2026-10-21) | https://dl.google.com/android/maven2/androidx/datastore/datastore-preferences-core/maven-metadata.xml |
| AGP, Gradle, Kover, JUnit BOM, ktlint | unchanged: 9.4.1, 9.8.2, 0.9.11, 6.1.3, 1.8.0 (still latest stable on 2026-10-27) | `docs/versions.md` |

- Each task that adds a dependency re-verifies it that day, uses a newer stable if one exists, records it in `docs/versions.md` (source URL, date), and regenerates `gradle/verification-metadata.xml` with the procedure in `docs/versions.md` "Dependency verification updates" step 2.
- **Third-party:** no new third-party library is declared. `datastore-preferences-core` pulls `com.squareup.okio:okio-jvm` transitively; Task 8 handles it under spec §6. No Gradle plugins beyond AGP, Kotlin and Kover.

**Modules and packages**
- `:domain`: Kotlin JVM, package `io.github.eastwoodno1.supersnake.domain`, flat. May depend only on the Kotlin stdlib, the JDK and `kotlinx-coroutines-core`.
- `:data`: Kotlin JVM, package `io.github.eastwoodno1.supersnake.data`. Depends on `:domain` and DataStore Preferences core. No Android, libGDX or Compose.
- Both: JVM toolchain 25, `allWarningsAsErrors = true`, JUnit Platform, Kover verify 100% LINE and 100% BRANCH, **no exclusions**. If a branch can't be covered, restructure the code.
- Use `java.time.LocalDate` for dates. No `kotlinx-datetime`.

**Process**
- Every task: its own GitHub issue, its own worktree and branch, squash-merged PR whose title is a Conventional header of at most 64 characters, body with `Closes #<issue>` and a filled `## Evidence` section. See "Per-task workflow".
- `pr-rules` requires a PR that changes `src/main` in `:domain` or `:data` to change `src/test` in the same module. Every task below does.

## Per-task workflow

Every task runs these around its own steps. They are written once here; each task's last step says "Finish per the per-task workflow".

**Before the task's Step 1:**
1. `git -C /Users/r03418/AndroidStudioProjects/SuperSnake pull --ff-only`
2. Create the issue: `~/.local/bin/mise exec -- gh issue create --title "<task title>" --body "<one-paragraph why, citing the rules the task implements>"`. Note the number.
3. `git -C /Users/r03418/AndroidStudioProjects/SuperSnake worktree add ../SuperSnake-<name> -b <branch>` with the task's **Branch** line, then work in `/Users/r03418/AndroidStudioProjects/SuperSnake-<name>`.

Gradle runs as `~/.local/bin/mise exec -- ./gradlew <tasks>` from the worktree. Commit after each green step with a Conventional message; small commits are fine because the PR is squashed.

**Finish:**
1. Strict gate, from the worktree:

   ```sh
   ~/.local/bin/mise exec -- scripts/gradle-strict.sh check :checkNoComments :domain:koverVerify
   ```

   Add `:data:koverVerify` from Task 8 on. Expected: `BUILD SUCCESSFUL`, no deprecation other than the allowlisted AGP one. Keep the output for Evidence.
2. Coverage counters for Evidence (`koverLog` prints only lines, so use the XML report):

   ```sh
   ~/.local/bin/mise exec -- ./gradlew :domain:koverXmlReport
   tail -8 domain/build/reports/kover/report.xml | grep -o '<counter type="\(LINE\|BRANCH\)"[^>]*/>'
   ```

   Same for `:data` from Task 8. Expected: both counters with `missed="0"`, as on 2026-10-27 for `Direction` (`BRANCH missed="0" covered="4"`, `LINE missed="0" covered="12"`).
3. Record each **[builder]** call the task made as a row in the "Engine details **[builder]**" table of `docs/game-rules.md` (Task 1 creates the table: columns `#`, `Rule`, `Why`; ids `B1`, `B2`, …).
4. Tick this task's checkboxes in this plan file, in the same branch.
5. ADLC fold-in, as `CLAUDE.md` "Fold-in before a pull request" says: if `$(git rev-parse --path-format=absolute --git-common-dir)/adlc/staged/` has notes, run `android-sdlc:adlc-documenter` (`model: "sonnet"`) with `MODE: fold-in`, `STAGED: <dir>`, `REPO: <worktree>`; run its `scan.sh` until it exits 0; commit `docs(adlc): fold in session notes`; save the returned lessons as memories; then delete the folded files from `staged/`.
6. `git push -u origin <branch>` (pre-push runs the strict `check`), then `~/.local/bin/mise exec -- gh pr create --title "<task's PR title>" --body-file <file>`. The body has `## Why`, `## What`, `Closes #<issue>`, and `## Evidence` with the outputs from 1 and 2 and the task's own red/green test output, and ends with the attribution line the session's system reminder gives.
7. Wait for the four required checks (`checks`, `pr-rules`, `analyze (java-kotlin)`, `analyze (actions)`) with `gh pr checks <n> --watch`. Paste the result in a PR comment. Squash-merge only when all are green: `gh pr merge <n> --squash --delete-branch`.
8. `git worktree remove ../SuperSnake-<name>` from the main checkout, then `git pull --ff-only`.

## Review Focus

1. **A fast double turn that reverses direction.** Heading right, the player swipes up then left within one tick. Expected: the snake turns up on the next tick and left on the one after, and does not die. Pinned in Task 3.
2. **A frame stall or a clock that steps backwards.** A 2-second hitch, or `nowMillis` lower than the last tick. Expected: at most one tick per `advance`, no burst of catch-up ticks, no exception, progress clamped to 0..1. Pinned in Task 6.
3. **A corrupted or hand-edited DataStore file.** Random bytes in the file, an unknown difficulty name, a malformed high-score line. Expected: no crash; settings fall back to defaults; bad high-score lines are dropped and the rest kept. Pinned in Task 8.
4. **A zero-size surface.** The first layout pass reports 0 × 0 (or a negative size). Expected: cell size 0, no `NaN`, no exception. Pinned in Task 7.
5. **A turn pressed just before pausing.** Expected: queued turns are cleared by pause, so nothing turns the snake after resume until the player acts. Pinned in Task 3.

---

### Task 1: Board geometry, difficulty and the random source

**Branch:** `feat/board-and-difficulty` · **PR title:** `feat: add board geometry, difficulty and random source`

**Files:**
- Create in `domain/src/main/kotlin/io/github/eastwoodno1/supersnake/domain/`: `Cell.kt`, `Board.kt`, `Difficulty.kt`, `RandomSource.kt`, `Clock.kt`
- Create in `domain/src/test/kotlin/io/github/eastwoodno1/supersnake/domain/`: `CellTest.kt`, `BoardTest.kt`, `DifficultyTest.kt`, `RandomSourceTest.kt`
- Modify: `docs/game-rules.md` (append the empty "Engine details **[builder]**" table)

**Interfaces:**
- Consumes: `Direction` (exists: `UP(0,-1)`, `DOWN(0,1)`, `LEFT(-1,0)`, `RIGHT(1,0)`, `opposite`). y grows downward.
- Produces:
  - `data class Cell(val x: Int, val y: Int) { operator fun plus(direction: Direction): Cell }`
  - `object Board { const val SIZE = 20; val cells: List<Cell>; fun contains(cell: Cell): Boolean }`. `cells` is row-major: y outer, x inner.
  - `enum class Difficulty(val label: String) { ASSIST("A"), LEVEL_1("1"), …, LEVEL_9("9"); val tickMillis: Long; val pointsPerOrb: Int }`
  - `fun interface RandomSource { fun nextInt(bound: Int): Int }` returning `0 until bound`, and `class KotlinRandomSource(private val random: kotlin.random.Random) : RandomSource`
  - `interface Clock { fun nowMillis(): Long; fun today(): java.time.LocalDate }`. Monotonic millis for ticks, local date for high scores. No implementation in this plan; tests use fakes.

- [ ] **Step 1: Write the failing tests**

```kotlin
@Test fun tickIntervalsMatchC8() = assertEquals(listOf(400L, 250L, 227L, 205L, 182L, 160L, 137L, 115L, 92L, 70L), Difficulty.entries.map { it.tickMillis })
@Test fun pointsPerOrbMatchC8() = assertEquals(listOf(1, 1, 2, 3, 4, 5, 6, 7, 8, 9), Difficulty.entries.map { it.pointsPerOrb })
@Test fun labels() = assertEquals(listOf("A", "1", "2", "3", "4", "5", "6", "7", "8", "9"), Difficulty.entries.map { it.label })

@Test fun boardIsTwentyByTwentyRowMajor() {
    assertEquals(400, Board.cells.size)
    assertEquals(listOf(Cell(0, 0), Cell(1, 0)), Board.cells.take(2))
    assertEquals(Cell(0, 1), Board.cells[20])
    assertEquals(Cell(19, 19), Board.cells.last())
}
@Test fun containsEdgesOnly() {
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

- [ ] **Step 2: Run them and confirm they fail**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test`
Expected: compilation fails with `Unresolved reference 'Difficulty'` (and the other new types).

- [ ] **Step 3: Implement the five files**

`tickMillis`: `ASSIST` is 400; level `n` is `250 - (45 * (n - 1) + 1) / 2` in integer maths, which is C8's `250 − round(22.5 × (n − 1))` with halves rounded up. `pointsPerOrb`: 1 for `ASSIST`, else `n`. Derive `n` from `ordinal`, not from a `when` with ten branches.

- [ ] **Step 4: Run them and confirm they pass**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test :domain:koverVerify`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Finish per the per-task workflow**

---

### Task 2: Game state and the tick

**Branch:** `feat/game-tick` · **PR title:** `feat: add game state and the tick rules`

**Files:**
- Create in `domain/src/main/…/domain/`: `GameState.kt` (`GameState`, `GameStatus`, `GameEvent`, `TickResult`), `GameRules.kt`
- Create in `domain/src/test/…/domain/`: `GameRulesTest.kt`, `FixedRandom.kt` (test helper)

**Interfaces:**
- Consumes: Task 1 `Cell`, `Board`, `Difficulty`, `RandomSource`.
- Produces:
  - `enum class GameStatus { RUNNING, PAUSED, LOST, WON }`
  - `enum class GameEvent { ORB_EATEN, LOST, WON }`
  - `data class GameState(val snake: List<Cell>, val direction: Direction, val pendingTurns: List<Direction>, val orb: Cell?, val score: Int, val orbsEaten: Int, val difficulty: Difficulty, val status: GameStatus)`. `snake` is head first.
  - `data class TickResult(val state: GameState, val events: List<GameEvent>)`
  - `object GameRules { fun newGame(difficulty: Difficulty, random: RandomSource): GameState; fun tick(state: GameState, random: RandomSource): TickResult }`
  - Test helper `class FixedRandom(vararg values: Int) : RandomSource` returns the values in order and fails the test (`error("unexpected random draw")`) when exhausted. Later tasks reuse it.

- [ ] **Step 1: Write the failing tests** (`GameRulesTest`)

  | Test | Setup | Assertions |
  |------|-------|------------|
  | `newGameStartsCentredHeadingRight` (C2) | `newGame(LEVEL_1, FixedRandom(0))` | `snake == listOf(Cell(10,10), Cell(9,10), Cell(8,10))`, `direction == RIGHT`, `pendingTurns` empty, `score == 0`, `orbsEaten == 0`, `status == RUNNING` |
  | `orbSpawnsOnRandomFreeCell` (C3) | `FixedRandom(0)`, then `FixedRandom(396)` | orb is `Cell(0,0)`, then `Cell(19,19)`: index into `Board.cells` minus the snake, row-major |
  | `tickMovesOneCell` (S1) | new game, orb far away | snake becomes `(11,10),(10,10),(9,10)`, events empty |
  | `eatingGrowsByOneAndScoresLevel` (S3, C4, C8) | `LEVEL_4`, orb at `Cell(11,10)`, `FixedRandom(0)` for the respawn | snake `(11,10),(10,10),(9,10),(8,10)` (tail stayed), `score == 4`, `orbsEaten == 1`, events `[ORB_EATEN]`, new orb `Cell(0,0)` |
  | `assistScoresOnePoint` | as above with `ASSIST` | `score == 1` |
  | `wallEndsTheGame` (S2, S4) | head at each edge moving outward, one case per direction | `status == LOST`, snake unchanged, events `[LOST]` |
  | `bodyEndsTheGame` (S4) | 5-segment hook whose next head cell is a body cell that is not the tail | `LOST`, events `[LOST]` |
  | `enteringLeavingTailIsLegal` (C7) | `snake = (1,1),(1,2),(2,2),(2,1)`, heading `RIGHT`, orb at `(10,10)` | `RUNNING`, head `(2,1)`, length 4 |
  | `enteringTailOnGrowthTickCollides` (C7) | same snake, orb placed on the tail `(2,1)` (crafted; normal play never puts an orb on the snake) | `LOST` |
  | `fillingTheBoardWins` (C9) | snake = the first 399 cells of the boustrophedon path (row y even: x 0→19; odd: x 19→0), head first, so head `(1,19)`, heading `LEFT`, orb `(0,19)`, `FixedRandom()` with no values | `status == WON`, `orb == null`, `snake.size == 400`, events `[ORB_EATEN, WON]`, no random draw |
  | `tickOutsideRunningChangesNothing` | `PAUSED`, `LOST`, `WON` | `TickResult(state, emptyList())` with the same state |

- [ ] **Step 2: Run them and confirm they fail**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test --tests '*GameRulesTest'`
Expected: compilation fails with `Unresolved reference 'GameRules'`.

- [ ] **Step 3: Implement `GameRules.newGame` and `GameRules.tick`**

The tick, in order:

```text
if status != RUNNING                     -> unchanged, no events
direction = pendingTurns.firstOrNull() ?: direction; pendingTurns = pendingTurns.drop(1)
head = snake.first() + direction
grows = head == orb
blocking = if grows: snake else snake.dropLast(1)      (C7)
if head !in Board or head in blocking   -> LOST, snake unchanged, events [LOST]
snake = [head] + (if grows: snake else snake.dropLast(1))
if !grows                                -> RUNNING, no events
score += difficulty.pointsPerOrb; orbsEaten += 1
free = Board.cells - snake
if free empty                            -> WON, orb = null, events [ORB_EATEN, WON]   (C9)
orb = free[random.nextInt(free.size)], events [ORB_EATEN]                            (C3)
```

The turn pop is here so Task 3 only adds the enqueue side. On `LOST`, also keep `direction` and `pendingTurns` as they were.

- [ ] **Step 4: Run them and confirm they pass**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test :domain:koverVerify`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Finish per the per-task workflow**

---

### Task 3: Turn buffering and pause

**Branch:** `feat/turns-and-pause` · **PR title:** `feat: buffer turns and pause the game`

**Files:**
- Create: `domain/src/main/…/domain/Turns.kt`, `domain/src/test/…/domain/TurnsTest.kt`
- Modify: `domain/src/main/…/domain/GameRules.kt` (add `pause`, `resume`), `domain/src/test/…/domain/GameRulesTest.kt`

**Interfaces:**
- Consumes: Task 2 `GameState`, `GameRules.tick`, `FixedRandom`.
- Produces:
  - `object Turns { const val CAPACITY = 2; fun enqueue(state: GameState, direction: Direction): GameState }`
  - `GameRules.pause(state: GameState): GameState`, `GameRules.resume(state: GameState): GameState`

**[builder] calls recorded in `docs/game-rules.md`:**
- B1: a turn equal to the reference direction (last queued, else current) is ignored, so a repeated swipe can't fill the buffer and push out a real turn. C5 only names the opposite.
- B2: turns are ignored unless the game is `RUNNING`.
- B3: pause clears queued turns (Review Focus 5).

- [ ] **Step 1: Write the failing tests**

  | Test | Setup | Assertions |
  |------|-------|------------|
  | `turnIsQueued` | new game (heading `RIGHT`), `enqueue(UP)` | `pendingTurns == [UP]` |
  | `oppositeOfCurrentIgnoredWhenQueueEmpty` (C5) | heading `RIGHT`, `enqueue(LEFT)` | `pendingTurns` empty |
  | `oppositeOfLastQueuedIgnored` (C5) | queue `[UP]`, `enqueue(DOWN)` | `[UP]` |
  | `sameAsReferenceIgnored` (B1) | heading `RIGHT`, `enqueue(RIGHT)`; queue `[UP]`, `enqueue(UP)` | empty; `[UP]` |
  | `queueHoldsTwo` (C6) | `enqueue(UP)`, `enqueue(LEFT)`, `enqueue(DOWN)` | `[UP, LEFT]` |
  | `turnsIgnoredUnlessRunning` (B2) | `PAUSED`, `LOST`, `WON` | state unchanged |
  | `oneTurnAppliedPerTick` (C6) | queue `[UP, LEFT]`, tick, tick | after first: `direction == UP`, queue `[LEFT]`; after second: `LEFT`, queue empty |
  | `fastReversalSurvives` (Review Focus 1) | new game, `enqueue(UP)`, `enqueue(LEFT)`, tick twice | `RUNNING`, head `(9,9)` after `(10,9)` |
  | `pauseStopsRunningAndClearsTurns` (C10, B3) | queue `[UP]`, `pause` | `PAUSED`, queue empty; `tick` changes nothing |
  | `resumeOnlyFromPaused` (C10) | `resume` on `PAUSED`, `RUNNING`, `LOST`, `WON`; `pause` on `PAUSED`, `LOST`, `WON` | `PAUSED → RUNNING`; every other case unchanged |

- [ ] **Step 2: Run them and confirm they fail**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test --tests '*TurnsTest' --tests '*GameRulesTest'`
Expected: compilation fails with `Unresolved reference 'Turns'`.

- [ ] **Step 3: Implement `Turns.enqueue`, `GameRules.pause` and `GameRules.resume`**

Reference direction is `pendingTurns.lastOrNull() ?: direction`. `Turns` never validates against anything else: Task 2's pop already guarantees the first queued turn is never the opposite of the current direction.

- [ ] **Step 4: Run them and confirm they pass**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test :domain:koverVerify`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Finish per the per-task workflow**

---

### Task 4: Input classification

**Branch:** `feat/input-classification` · **PR title:** `feat: classify swipes and keys into game input`

**Files:**
- Create: `domain/src/main/…/domain/GameInput.kt` (`GameInput`, `GameKey`), `SwipeClassifier.kt`
- Create: `domain/src/test/…/domain/GameInputTest.kt`, `SwipeClassifierTest.kt`

**Interfaces:**
- Consumes: `Direction`.
- Produces:
  - `sealed interface GameInput { data class Turn(val direction: Direction) : GameInput; data object TogglePause : GameInput }`
  - `enum class GameKey { UP, DOWN, LEFT, RIGHT, PAUSE; val input: GameInput }`. Plan 3 maps arrow keys, D-pad, WASD and gamepad buttons 1:1 onto it, and the on-screen D-pad sends `GameInput.Turn` directly.
  - `object SwipeClassifier { fun classify(dx: Float, dy: Float, minDistance: Float): Direction? }`. Screen coordinates: `dy > 0` is `DOWN`. `minDistance` is in pixels, converted from dp by `:app`.

**[builder] calls:** B4: a swipe is classified by its dominant axis, which must be at least `minDistance` (inclusive); an exact diagonal (`|dx| == |dy|`) or a non-finite value is no swipe.

- [ ] **Step 1: Write the failing tests**

```kotlin
@Test fun dominantAxisWins() {
    assertEquals(Direction.RIGHT, SwipeClassifier.classify(100f, 10f, 48f))
    assertEquals(Direction.LEFT, SwipeClassifier.classify(-100f, 10f, 48f))
    assertEquals(Direction.UP, SwipeClassifier.classify(5f, -90f, 48f))
    assertEquals(Direction.DOWN, SwipeClassifier.classify(5f, 90f, 48f))
}
@Test fun thresholdIsInclusive() {
    assertEquals(Direction.RIGHT, SwipeClassifier.classify(48f, 0f, 48f))
    assertNull(SwipeClassifier.classify(47.9f, 0f, 48f))
}
@Test fun diagonalAndNonFiniteAreNotSwipes() {
    assertNull(SwipeClassifier.classify(60f, -60f, 48f))
    assertNull(SwipeClassifier.classify(Float.NaN, 100f, 48f))
    assertNull(SwipeClassifier.classify(Float.POSITIVE_INFINITY, 0f, 48f))
}
@Test fun keysMapToInput() = assertEquals(
    listOf(GameInput.Turn(Direction.UP), GameInput.Turn(Direction.DOWN), GameInput.Turn(Direction.LEFT), GameInput.Turn(Direction.RIGHT), GameInput.TogglePause),
    GameKey.entries.map { it.input },
)
```

- [ ] **Step 2: Run them and confirm they fail**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test --tests '*SwipeClassifierTest' --tests '*GameInputTest'`
Expected: compilation fails with `Unresolved reference 'SwipeClassifier'`.

- [ ] **Step 3: Implement `SwipeClassifier.classify` and `GameKey.input`**

- [ ] **Step 4: Run them and confirm they pass**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test :domain:koverVerify`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Finish per the per-task workflow**

---

### Task 5: High scores, settings and their use cases

**Branch:** `feat/scores-and-settings` · **PR title:** `feat: add high score and settings use cases`

**Files:**
- Modify: `gradle/libs.versions.toml` (add `coroutines = "1.11.0"`, libraries `kotlinx-coroutines-core`, `kotlinx-coroutines-test`), `domain/build.gradle.kts` (`api(libs.kotlinx.coroutines.core)`, because public signatures expose `Flow`; `testImplementation(libs.kotlinx.coroutines.test)`), `gradle/verification-metadata.xml`, `docs/versions.md`
- Create in `domain/src/main/…/domain/`: `HighScore.kt` (`HighScore`, `HighScoreTable`, `Placement`), `Settings.kt` (`Settings`, `ControlScheme`), `Repositories.kt` (both interfaces), `RecordHighScore.kt` (`RecordHighScore`, `ScoreResult`), `HighScores.kt`, `SettingsActions.kt`
- Create in `domain/src/test/…/domain/`: `HighScoreTableTest.kt`, `RecordHighScoreTest.kt`, `HighScoresTest.kt`, `SettingsActionsTest.kt`, `FakeRepositories.kt` (in-memory fakes on `MutableStateFlow`), `FakeClock.kt` (`class FakeClock(var now: Long = 0, var date: LocalDate = LocalDate.of(2026, 10, 27)) : Clock`)

**Interfaces:**
- Consumes: Task 1 `Difficulty`, `Clock`.
- Produces:
  - `data class HighScore(val score: Int, val difficulty: Difficulty, val date: LocalDate)`
  - `data class Placement(val table: List<HighScore>, val rank: Int?)`, rank 1-based, `null` when the entry did not make the table
  - `object HighScoreTable { const val CAPACITY = 10; fun insert(table: List<HighScore>, entry: HighScore): Placement; fun normalize(entries: List<HighScore>): List<HighScore> }`
  - `enum class ControlScheme { SWIPE, DPAD }`
  - `data class Settings(val difficulty: Difficulty = Difficulty.LEVEL_1, val soundOn: Boolean = true, val hapticsOn: Boolean = true, val controlScheme: ControlScheme = ControlScheme.SWIPE)`
  - `interface HighScoreRepository { val entries: Flow<List<HighScore>>; suspend fun update(transform: (List<HighScore>) -> List<HighScore>) }`
  - `interface SettingsRepository { val settings: Flow<Settings>; suspend fun update(transform: (Settings) -> Settings) }`
  - `data class ScoreResult(val rank: Int?, val isNewBest: Boolean)`
  - `class RecordHighScore(private val repository: HighScoreRepository, private val clock: Clock) { suspend operator fun invoke(score: Int, difficulty: Difficulty): ScoreResult }`
  - `class HighScores(repository: HighScoreRepository) { val top: Flow<List<HighScore>>; val best: Flow<Int> }`
  - `class SettingsActions(private val repository: SettingsRepository) { val settings: Flow<Settings>; suspend fun setDifficulty(value: Difficulty); suspend fun setSound(on: Boolean); suspend fun setHaptics(on: Boolean); suspend fun setControlScheme(value: ControlScheme) }`

**[builder] calls:**
- B5: a score of 0 is never recorded.
- B6: the table is sorted by score, highest first; on a tie the older entry stays above, so a new entry must beat the 10th score to enter.
- B7: "new best" means rank 1.
- B8: default settings are level 1, sound on, haptics on, swipe.

- [ ] **Step 1: Verify and add the coroutines dependencies**

Re-check `https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-core/maven-metadata.xml` for the newest stable (no alpha, beta, RC). Use it; record core and test in `docs/versions.md` with the URL, the POM Last-Modified date and today's date. Re-check CodeQL's Kotlin line too (see State at handoff).

- [ ] **Step 2: Write the failing tests**

  | Test | Assertions |
  |------|------------|
  | `insertKeepsDescendingOrder` | inserting 50 into `[80, 20]` gives `[80, 50, 20]`, rank 2 |
  | `tieGoesBelowExisting` (B6) | inserting 50 into `[50]` (older date) gives rank 2, and the older entry is first |
  | `fullTableNeedsToBeatTenth` (B6) | table of ten scores `100, 90, …, 10`: inserting 10 → rank `null`, table unchanged; inserting 11 → rank 10, last is 11, size 10 |
  | `zeroIsNotRecorded` (B5) | `insert([], score 0)` → rank `null`, table empty |
  | `normalizeSortsCapsAndDropsNonPositive` | 12 mixed entries including 0 and −3 → 10 entries, descending, all > 0 |
  | `recordStoresWithTodaysDate` (C12) | `FakeClock(date = 2026-10-27)`, record 30 at `LEVEL_2` → repository holds `HighScore(30, LEVEL_2, 2026-10-27)`, result `ScoreResult(1, true)` |
  | `recordBelowBestIsNotNewBest` | repository `[40]`, record 30 → `ScoreResult(2, false)` |
  | `recordOffTableIsNeither` | ten entries ≥ 50, record 10 → `ScoreResult(null, false)`, repository unchanged |
  | `bestIsTopScoreOrZero` | empty → `0`; `[70, 20]` → `70` (`HighScores.best.first()`) |
  | `settingsActionsUpdateOneField` | each setter changes only its field; `settings.first()` defaults equal `Settings()` |

Use `kotlinx.coroutines.test.runTest`.

- [ ] **Step 3: Run them and confirm they fail**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test`
Expected: compilation fails with `Unresolved reference 'HighScoreTable'`.

- [ ] **Step 4: Implement the types above**

`RecordHighScore` computes the `Placement` inside `repository.update { … }` and returns its rank, so the read and the write are one atomic edit. `HighScores.best` is `top.map { it.firstOrNull()?.score ?: 0 }`.

- [ ] **Step 5: Run them and confirm they pass**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test :domain:koverVerify`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 6: Refresh dependency verification**

Run step 2 of `docs/versions.md` "Dependency verification updates" (fresh `GRADLE_USER_HOME`, `--refresh-dependencies`), then `git diff --stat gradle/verification-metadata.xml`.
Expected: new `<component group="org.jetbrains.kotlinx" name="kotlinx-coroutines-core…">` entries; `:checkNoComments` passes. If CI later reports a Linux-only artifact, follow step 4 of the same section.

- [ ] **Step 7: Finish per the per-task workflow**

The PR body's Evidence includes the version sources from Step 1.

---

### Task 6: Tick timing and the game session

**Branch:** `feat/game-session` · **PR title:** `feat: drive ticks and announcements from a game session`

**Files:**
- Create in `domain/src/main/…/domain/`: `TickTimer.kt` (`TickTimer`, `TickAdvance`), `Announcement.kt`, `GameSession.kt` (`GameSession`, `GameFrame`)
- Create in `domain/src/test/…/domain/`: `TickTimerTest.kt`, `GameSessionTest.kt`

**Interfaces:**
- Consumes: Task 2 `GameRules`, Task 3 `Turns`, pause/resume, Task 4 `GameInput`, Task 5 `RecordHighScore`, `ScoreResult`, `FakeClock`, fake repositories, Task 2 `FixedRandom`.
- Produces:
  - `data class TickTimer(val intervalMillis: Long, val lastTickMillis: Long) { fun advance(nowMillis: Long): TickAdvance; fun progress(nowMillis: Long): Float; fun restartedAt(nowMillis: Long): TickTimer }`
  - `data class TickAdvance(val ticked: Boolean, val timer: TickTimer)`
  - `sealed interface Announcement { data object GameStarted; data object Paused; data class OrbMilestone(val orbs: Int); data class GameOver(val score: Int); data class Won(val score: Int); data class NewHighScore(val score: Int) }`, each `: Announcement`
  - `data class GameFrame(val state: GameState, val previousSnake: List<Cell>, val progress: Float, val events: List<GameEvent>, val announcements: List<Announcement>, val result: ScoreResult?)`
  - `class GameSession internal constructor(initial: GameState, private val clock: Clock, private val random: RandomSource, private val recordHighScore: RecordHighScore) { constructor(difficulty: Difficulty, clock: Clock, random: RandomSource, recordHighScore: RecordHighScore); var frame: GameFrame; private set; fun input(input: GameInput): GameFrame; fun pause(): GameFrame; suspend fun advance(): GameFrame }`. The public constructor starts from `GameRules.newGame(difficulty, random)`; tests use the internal one to start from a crafted state. A new game is a new `GameSession`. `:app` calls `pause()` when the app goes to the background (C10) and `advance()` once per rendered frame.

**[builder] calls:**
- B9: at most one tick per `advance`. If more than two intervals have passed (a stall), the timer restarts at `now` instead of catching up. A clock step backwards yields no tick.
- B10: resume restarts the timer at `now`, so the snake never jumps after a pause.
- B11: announcement order at the end of a game is `GameOver`/`Won` first, then `NewHighScore`.

- [ ] **Step 1: Write the failing `TickTimerTest`**

  | Test | Timer | Call | Expected |
  |------|-------|------|----------|
  | `noTickBeforeInterval` | `(100, 0)` | `advance(99)` | `ticked == false`, timer unchanged |
  | `tickKeepsCadence` | `(100, 0)` | `advance(130)` | `ticked`, `lastTickMillis == 100` |
  | `stallDoesNotCatchUp` (Review Focus 2) | `(100, 0)` | `advance(2000)` | `ticked`, `lastTickMillis == 2000` |
  | `backwardsClockDoesNothing` (Review Focus 2) | `(100, 500)` | `advance(400)`, `progress(400)` | no tick, `0f` |
  | `progressIsClamped` | `(100, 0)` | `progress(50)`, `progress(250)` | `0.5f`, `1f` |
  | `restartedAtMovesBaseline` | `(100, 0)` | `restartedAt(900)` | `lastTickMillis == 900` |

- [ ] **Step 2: Write the failing `GameSessionTest`**

Each test builds `GameSession(LEVEL_1, FakeClock(now = 0), FixedRandom(…), RecordHighScore(fakeRepo, clock))` and moves `clock.now` by hand. `LEVEL_1` ticks every 250 ms.

  | Test | Actions | Assertions |
  |------|---------|------------|
  | `startsRunningAndAnnounces` | construct | `frame.state.status == RUNNING`, `announcements == [GameStarted]`, `previousSnake == state.snake`, `progress == 0f` |
  | `advanceTicksOnSchedule` | `now = 249; advance()`, then `now = 250; advance()` | first: head still `(10,10)`, events empty, announcements empty; second: head `(11,10)`, `previousSnake` head `(10,10)` |
  | `progressBetweenTicks` | `now = 125; advance()` | `progress == 0.5f` |
  | `turnInputIsQueued` | `input(Turn(UP))`, `now = 250; advance()` | head `(10,9)` |
  | `togglePausePausesAndResumes` (C10, B10) | `input(TogglePause)`, `now = 5000; advance()`, `input(TogglePause)`, `now = 5249; advance()` | after pause: `PAUSED`, `[Paused]`, no tick at 5000; after resume: `RUNNING`, still no tick at 5249; tick at `now = 5250` |
  | `pauseIsIdempotent` | `pause()` twice | second frame has no announcements |
  | `everyTenthOrbIsAnnounced` | internal constructor: new-game state with `orbsEaten = 9` and orb at `(11,10)`; tick. Then `orbsEaten = 10`, same orb; tick | first: `announcements == [OrbMilestone(10)]`; second: empty |
  | `gameOverRecordsAndAnnounces` (C12, B11) | `FixedRandom(208, 0)` puts the first orb on `(11,10)` (index 211 minus the 3 snake cells before it) and the next on `(0,0)`; tick until the head leaves the right wall (score 1); empty repository | final frame: `LOST`, `announcements == [GameOver(1), NewHighScore(1)]`, `result == ScoreResult(1, true)`, repository holds the entry |
  | `gameOverWithoutRecord` | crash with score 0 | `announcements == [GameOver(0)]`, `result == ScoreResult(null, false)` |
  | `winRecordsAndAnnounces` (C9) | internal constructor with Task 2's `fillingTheBoardWins` state; tick | `WON`, `announcements == [Won(score), NewHighScore(score)]` |
  | `afterTheEndNothingTicks` | `advance()` again after `LOST` | same state, no events, no announcements, `result` kept |

- [ ] **Step 3: Run them and confirm they fail**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test --tests '*TickTimerTest' --tests '*GameSessionTest'`
Expected: compilation fails with `Unresolved reference 'TickTimer'`.

- [ ] **Step 4: Implement `TickTimer` and `GameSession`**

`TickTimer.advance`: `elapsed = now - last`; `elapsed < interval` → no tick; `elapsed < 2 * interval` → tick, `last + interval`; else tick, `now`. `progress` is `(elapsed / interval)` clamped to `0f..1f`. `GameSession` maps `GameEvent`s to announcements, and on `LOST`/`WON` calls `recordHighScore(score, difficulty)` once.

- [ ] **Step 5: Run them and confirm they pass**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test :domain:koverVerify`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 6: Finish per the per-task workflow**

---

### Task 7: Render maths

**Branch:** `feat/render-maths` · **PR title:** `feat: add board layout, motion, pulse and particle maths`

**Files:**
- Create in `domain/src/main/…/domain/`: `BoardLayout.kt` (`BoardLayout`, `BoardFrame`), `SnakeMotion.kt` (`SnakeMotion`, `Point`), `Pulse.kt`, `Particles.kt` (`Particles`, `Particle`)
- Create in `domain/src/test/…/domain/`: one test file per main file

**Interfaces:**
- Consumes: `Board`, `Cell`; `GameFrame.previousSnake`, `.state.snake`, `.progress` from Task 6.
- Produces, all in cell units except `BoardFrame`:
  - `data class BoardFrame(val left: Float, val top: Float, val cellSize: Float)` in pixels; `object BoardLayout { fun fit(width: Float, height: Float): BoardFrame }` (C1)
  - `data class Point(val x: Float, val y: Float)`; `object SnakeMotion { fun interpolate(previous: List<Cell>, current: List<Cell>, progress: Float): List<Point> }`
  - `object Pulse { const val PERIOD_MILLIS = 1200L; fun brightness(nowMillis: Long, reduceMotion: Boolean): Float }`
  - `data class Particle(val x: Float, val y: Float, val alpha: Float)`; `object Particles { const val COUNT = 12; const val LIFETIME_MILLIS = 400L; const val TRAVEL_CELLS = 1.2f; fun burst(origin: Cell, startMillis: Long, nowMillis: Long, reduceMotion: Boolean): List<Particle> }`
  - `:board` (Plan 3) only multiplies by `cellSize` and adds `left`/`top`.

**[builder] calls:** B12: pulse brightness `0.8 + 0.2 × sin(2π × t / 1200 ms)`, constant `1.0` with reduce motion. B13: an orb burst is 12 particles at even angles from the cell centre, travelling 1.2 cells with ease-out `1 − (1 − t)²` over 400 ms, alpha `1 − t`; none with reduce motion (spec §3).

- [ ] **Step 1: Write the failing tests**

  | Test | Assertions |
  |------|------------|
  | `fitLetterboxesPortrait` | `fit(1000f, 1600f) == BoardFrame(0f, 300f, 50f)` |
  | `fitLetterboxesLandscape` | `fit(1600f, 1000f) == BoardFrame(300f, 0f, 50f)` |
  | `fitZeroOrNegativeIsEmpty` (Review Focus 4) | `fit(0f, 0f)`, `fit(-5f, 100f)`: `cellSize == 0f`, no `NaN` in any field |
  | `interpolateMovesEachSegment` | previous `(10,10),(9,10),(8,10)`, current `(11,10),(10,10),(9,10)`, progress `0.5f` → `(10.5,10),(9.5,10),(8.5,10)` |
  | `interpolateGrowthHoldsNewTail` | current has one more segment than previous; the extra tail point equals previous last cell at every progress |
  | `interpolateClampsProgress` | progress `-1f` equals `0f`; `2f` equals `1f` |
  | `interpolateFirstFrame` | previous empty → current cells as points |
  | `pulseRangeAndPeriod` | `brightness(0, false) == 0.8f`, `brightness(300, false) == 1.0f` (±1e-4), `brightness(900, false) == 0.6f` (±1e-4), `brightness(1200, false) == brightness(0, false)` |
  | `pulseStaticWithReduceMotion` | `brightness(300, true) == 1f` |
  | `burstStartsAtCentre` | `burst(Cell(4,4), 0, 0, false)`: 12 particles, all at `(4.5, 4.5)`, alpha 1 |
  | `burstEasesOut` | at 200 ms: each particle at distance `1.2 × 0.75 = 0.9` from centre (±1e-4), alpha `0.5`; first particle's angle is 0 (to `+x`) |
  | `burstEndsAndRespectsReduceMotion` | at 400 ms, at −1 ms, and with `reduceMotion = true`: empty |

- [ ] **Step 2: Run them and confirm they fail**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test`
Expected: compilation fails with `Unresolved reference 'BoardLayout'`.

- [ ] **Step 3: Implement the four files**

`fit`: `side = max(0f, min(width, height))`, `cellSize = side / Board.SIZE`, `left = (max(0f, width) - side) / 2`, `top` likewise. `interpolate`: point `i` goes from `previous.getOrElse(i) { previous.lastOrNull() ?: current[i] }` to `current[i]`.

- [ ] **Step 4: Run them and confirm they pass**

Run: `~/.local/bin/mise exec -- ./gradlew :domain:test :domain:koverVerify`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Finish per the per-task workflow**

---

### Task 8: `:data` DataStore repositories

**Branch:** `feat/data-datastore` · **PR title:** `feat: persist settings and high scores with DataStore`

**Files:**
- Create: `data/build.gradle.kts` (same shape as `domain/build.gradle.kts`, plus `implementation(project(":domain"))`, `implementation(libs.androidx.datastore.preferences.core)`, `testImplementation(libs.kotlinx.coroutines.test)`)
- Create in `data/src/main/kotlin/io/github/eastwoodno1/supersnake/data/`: `PreferenceStores.kt`, `HighScoreCodec.kt`, `DataStoreHighScoreRepository.kt`, `DataStoreSettingsRepository.kt`
- Create in `data/src/test/kotlin/io/github/eastwoodno1/supersnake/data/`: `HighScoreCodecTest.kt`, `DataStoreHighScoreRepositoryTest.kt`, `DataStoreSettingsRepositoryTest.kt`, `PreferenceStoresTest.kt`
- Create: `docs/adr/0008-datastore-core-in-jvm-data.md`
- Modify: `settings.gradle.kts` (`include(":data")`), `build.gradle.kts` (`kover(project(":data"))`), `gradle/libs.versions.toml` (`datastore = "1.3.0"`, library `androidx-datastore-preferences-core`), `gradle/verification-metadata.xml`, `docs/versions.md`, `CONTRIBUTING.md` (line 46: drop "(`:data` once it exists)" and name `:data` beside `:domain`)

**Interfaces:**
- Consumes: Task 5 `HighScoreRepository`, `SettingsRepository`, `HighScore`, `HighScoreTable.normalize`, `Settings`, `ControlScheme`; `Difficulty`.
- Produces (Plan 3's composition root uses these):
  - `object PreferenceStores { const val SETTINGS_FILE = "settings.preferences_pb"; const val HIGH_SCORES_FILE = "high_scores.preferences_pb"; fun create(file: java.io.File, scope: CoroutineScope): DataStore<Preferences> }`. It installs `ReplaceFileCorruptionHandler { emptyPreferences() }`.
  - `object HighScoreCodec { fun encode(entries: List<HighScore>): String; fun decode(text: String): List<HighScore> }`. One entry per line, `score|DIFFICULTY_NAME|yyyy-MM-dd`. `decode` drops any line that doesn't parse, then applies `HighScoreTable.normalize`.
  - `class DataStoreHighScoreRepository(private val store: DataStore<Preferences>) : HighScoreRepository` (key `high_scores`, a string)
  - `class DataStoreSettingsRepository(private val store: DataStore<Preferences>) : SettingsRepository` (keys `difficulty` and `control_scheme` as enum names, `sound` and `haptics` as booleans; a missing key or unknown name falls back to the `Settings()` default for that field)

- [ ] **Step 1: Verify the dependency and its transitive third-party code**

1. Re-check `datastore-preferences-core` on Google Maven for the newest stable; use it.
2. Run `~/.local/bin/mise exec -- ./gradlew :data:dependencies --configuration runtimeClasspath` (after Step 3's module exists, or with a scratch module) and list every group that is not `androidx.*`, `org.jetbrains.*` or `com.google.*`. On 2026-10-27 the POM showed `com.squareup.okio:okio-jvm:3.17.0`.
3. For each such group, collect spec §6 evidence with `gh api`: stars, last push, latest stable release date, issues opened and closed in the last 30 days.
4. **Owner checkpoint.** Spec §6 says "Current third-party runtime dependencies: libGDX only". DataStore cannot run without okio (the Android artifact the spec chose pulls it too). Show the owner the evidence and ask them to confirm okio as a transitive runtime dependency. Do not merge this task without that confirmation; record it in ADR-0008 with the date.

- [ ] **Step 2: Write the failing tests**

All repository tests use a `@TempDir` file and a store scope `CoroutineScope(Dispatchers.IO + Job())` cancelled in `@AfterEach`, inside `runTest`.

  | Test | Assertions |
  |------|------------|
  | `codecRoundTrips` | `decode(encode(list)) == list` for three entries across `ASSIST` and `LEVEL_9` |
  | `codecDropsBadLines` (Review Focus 3) | `"30|LEVEL_2|2026-10-27\nnope\n5|LEVEL_X|2026-10-27\n7|ASSIST|2026-13-40\n|\n"` → only the first entry |
  | `codecEmpty` | `decode("") == emptyList()`, `encode(emptyList()) == ""` |
  | `highScoresStartEmpty` | `entries.first()` is empty |
  | `highScoresPersistAcrossStores` | `update { listOf(entry) }`, cancel scope, new store on the same file → `entries.first() == listOf(entry)` |
  | `settingsDefaultWhenEmpty` | `settings.first() == Settings()` |
  | `settingsPersist` | update to `Settings(LEVEL_7, false, false, DPAD)`, reopen → equal |
  | `settingsUnknownNamesFallBack` (Review Focus 3) | write `difficulty = "LEVEL_42"` and `control_scheme = "JOYSTICK"` with raw `store.edit` → `settings.first() == Settings()` |
  | `corruptFileReadsAsEmpty` (Review Focus 3) | write random bytes to the file, then `PreferenceStores.create` → settings are `Settings()`, high scores empty, and a later `update` succeeds |
  | `fileNamesAreStable` | the two constants equal `"settings.preferences_pb"` and `"high_scores.preferences_pb"` (Plan 3's backup rules name them) |

- [ ] **Step 3: Run them and confirm they fail**

Run: `~/.local/bin/mise exec -- ./gradlew :data:test`
Expected: compilation fails with `Unresolved reference 'HighScoreCodec'`.

- [ ] **Step 4: Implement the four files**

`DataStoreSettingsRepository.update` reads the current `Settings` from the preferences inside `store.edit`, applies `transform`, and writes all four keys. `DataStoreHighScoreRepository` does the same with the codec. Parse enum names with `entries.firstOrNull { it.name == value }`, never `valueOf`.

- [ ] **Step 5: Run them and confirm they pass**

Run: `~/.local/bin/mise exec -- ./gradlew :data:test :data:koverVerify`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 6: Write ADR-0008 and refresh verification**

ADR-0008 "DataStore Preferences core in a pure JVM `:data`": context (spec §12 left the module kind open), decision (Kotlin JVM with `datastore-preferences-core`; Plan 3 gives it files in the app's `filesDir/datastore/`), the transitive dependencies with Step 1's evidence and the owner's confirmation, and consequences (no Android in `:data`, the 100% gate applies). Same format as `docs/adr/0003-no-di-framework.md`. Then run step 2 of `docs/versions.md` "Dependency verification updates" and record DataStore in `docs/versions.md`.

Expected: `git diff --stat gradle/verification-metadata.xml` shows `androidx.datastore` and `com.squareup.okio` components.

- [ ] **Step 7: Finish per the per-task workflow**

From this task on, the strict gate includes `:data:koverVerify` and Evidence includes `:data`'s coverage counters.

---

### Task 9: Close Plan 2

**Branch:** `docs/close-plan-2` · **PR title:** `docs: mark Plan 2 complete and hand off to Plan 3`

**Files:**
- Modify: this plan's "State at handoff", `README.md` (the plans paragraph), `docs/game-rules.md` (check B1–B13 are all present). `CLAUDE.md` keeps pointing here until Plan 3 is written.

- [ ] **Step 1: Prove the whole engine on `main`'s head**

From the worktree:

```sh
~/.local/bin/mise exec -- scripts/gradle-strict.sh check :checkNoComments :domain:koverVerify :data:koverVerify
~/.local/bin/mise exec -- ./gradlew :domain:koverXmlReport :data:koverXmlReport
```

Expected: `BUILD SUCCESSFUL`; then the per-task workflow's counter command for both modules shows `missed="0"` for LINE and BRANCH.

- [ ] **Step 2: Check spec coverage**

For each of S1–S5 and C1–C12, name the test that proves it (C1's square letterbox: `fitLetterbox*`; C10's background trigger and C11 are Plan 3 and "no multiplayer code", say so). Put the table in the PR body.

- [ ] **Step 3: Confirm no plugin drift**

Run: `/Users/r03418/AndroidStudioProjects/android-sdlc-skills/scripts/verify-templates.sh /Users/r03418/AndroidStudioProjects/SuperSnake-close-plan-2`
Expected: `0 drift`. If anything drifted, stop: a task changed a file the plugin copies, and the plugin repo needs its own PR first.

- [ ] **Step 4: Update the handoff text**

"State at handoff": Plan 2 complete, PR numbers, `main` head, Dependabot state, next action "write Plan 3". README: "Plan 2 (game engine) is complete. Plan 3 is next."

- [ ] **Step 5: Finish per the per-task workflow**
