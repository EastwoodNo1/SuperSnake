package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GameRulesTest {
    private fun running(
        snake: List<Cell> = listOf(Cell(10, 10), Cell(9, 10), Cell(8, 10)),
        direction: Direction = Direction.RIGHT,
        orb: Cell? = Cell(0, 0),
        difficulty: Difficulty = Difficulty.LEVEL_1,
    ): GameState =
        GameState(
            snake = snake,
            direction = direction,
            pendingTurns = emptyList(),
            orb = orb,
            score = 0,
            orbsEaten = 0,
            difficulty = difficulty,
            status = GameStatus.RUNNING,
        )

    @Test
    fun c2c13NewGameCountsDownCentredHeadingRight() {
        val state = GameRules.newGame(Difficulty.LEVEL_1, FixedRandom(0))
        assertEquals(listOf(Cell(10, 10), Cell(9, 10), Cell(8, 10)), state.snake)
        assertEquals(Direction.RIGHT, state.direction)
        assertTrue(state.pendingTurns.isEmpty())
        assertEquals(0, state.score)
        assertEquals(0, state.orbsEaten)
        assertEquals(GameStatus.COUNTDOWN, state.status)
        assertNotNull(state.orb)
    }

    @Test
    fun c3OrbSpawnsOnRandomFreeCell() {
        assertEquals(Cell(0, 0), GameRules.newGame(Difficulty.LEVEL_1, FixedRandom(0)).orb)
        assertEquals(Cell(19, 19), GameRules.newGame(Difficulty.LEVEL_1, FixedRandom(396)).orb)
    }

    @Test
    fun s1StepMovesOneCell() {
        val result = GameRules.tick(running(), FixedRandom())
        assertEquals(listOf(Cell(11, 10), Cell(10, 10), Cell(9, 10)), result.state.snake)
        assertTrue(result.events.isEmpty())
        assertEquals(GameStatus.RUNNING, result.state.status)
    }

    @Test
    fun c4c8EatingGrowsByOneAndScoresLevel() {
        val result = GameRules.tick(running(orb = Cell(11, 10), difficulty = Difficulty.LEVEL_4), FixedRandom(0))
        assertEquals(listOf(Cell(11, 10), Cell(10, 10), Cell(9, 10), Cell(8, 10)), result.state.snake)
        assertEquals(4, result.state.score)
        assertEquals(1, result.state.orbsEaten)
        assertEquals(listOf(GameEvent.ORB_EATEN), result.events)
        assertEquals(Cell(0, 0), result.state.orb)
    }

    @Test
    fun c8AssistScoresOnePoint() {
        val result = GameRules.tick(running(orb = Cell(11, 10), difficulty = Difficulty.ASSIST), FixedRandom(0))
        assertEquals(1, result.state.score)
    }

    @Test
    fun s4c17WallEndsTheGameOnThatStep() {
        listOf(
            Direction.UP to listOf(Cell(5, 0), Cell(5, 1), Cell(5, 2)),
            Direction.DOWN to listOf(Cell(5, 19), Cell(5, 18), Cell(5, 17)),
            Direction.LEFT to listOf(Cell(0, 5), Cell(1, 5), Cell(2, 5)),
            Direction.RIGHT to listOf(Cell(19, 5), Cell(18, 5), Cell(17, 5)),
        ).forEach { (direction, snake) ->
            val state = running(snake = snake, direction = direction)
            val result = GameRules.tick(state, FixedRandom())
            assertEquals(GameStatus.LOST, result.state.status)
            assertEquals(snake, result.state.snake)
            assertEquals(listOf(GameEvent.LOST), result.events)
        }
    }

    @Test
    fun s4BodyEndsTheGame() {
        val snake = listOf(Cell(5, 5), Cell(6, 5), Cell(6, 6), Cell(5, 6), Cell(4, 6))
        val result = GameRules.tick(running(snake = snake, direction = Direction.DOWN), FixedRandom())
        assertEquals(GameStatus.LOST, result.state.status)
        assertEquals(listOf(GameEvent.LOST), result.events)
    }

    @Test
    fun c7EnteringLeavingTailIsLegal() {
        val snake = listOf(Cell(1, 1), Cell(1, 2), Cell(2, 2), Cell(2, 1))
        val result = GameRules.tick(running(snake = snake, orb = Cell(10, 10)), FixedRandom())
        assertEquals(GameStatus.RUNNING, result.state.status)
        assertEquals(Cell(2, 1), result.state.snake.first())
        assertEquals(4, result.state.snake.size)
    }

    @Test
    fun c7EnteringTailOnGrowthStepCollides() {
        val snake = listOf(Cell(1, 1), Cell(1, 2), Cell(2, 2), Cell(2, 1))
        val result = GameRules.tick(running(snake = snake, orb = Cell(2, 1)), FixedRandom())
        assertEquals(GameStatus.LOST, result.state.status)
    }

    @Test
    fun c9FillingTheBoardWins() {
        val snake = almostFullSnake()
        assertEquals(Cell(1, 19), snake.first())
        val result = GameRules.tick(running(snake = snake, direction = Direction.LEFT, orb = Cell(0, 19)), FixedRandom())
        assertEquals(GameStatus.WON, result.state.status)
        assertEquals(null, result.state.orb)
        assertEquals(400, result.state.snake.size)
        assertEquals(listOf(GameEvent.ORB_EATEN, GameEvent.WON), result.events)
    }

    @Test
    fun stepOutsideRunningChangesNothing() {
        listOf(GameStatus.COUNTDOWN, GameStatus.PAUSED, GameStatus.LOST, GameStatus.WON).forEach { status ->
            val state = running().copy(status = status)
            assertEquals(TickResult(state, emptyList()), GameRules.tick(state, FixedRandom()))
        }
    }

    @Test
    fun s3StepConsumesOneQueuedTurn() {
        val state = running().copy(pendingTurns = listOf(Direction.DOWN, Direction.LEFT))
        val result = GameRules.tick(state, FixedRandom())
        assertEquals(Direction.DOWN, result.state.direction)
        assertEquals(listOf(Direction.LEFT), result.state.pendingTurns)
        assertEquals(Cell(10, 11), result.state.snake.first())
    }

    @Test
    fun c16OneTurnAppliedPerStep() {
        val queued = running().copy(pendingTurns = listOf(Direction.UP, Direction.LEFT))
        val first = GameRules.tick(queued, FixedRandom()).state
        assertEquals(Direction.UP, first.direction)
        assertEquals(listOf(Direction.LEFT), first.pendingTurns)
        val second = GameRules.tick(first, FixedRandom()).state
        assertEquals(Direction.LEFT, second.direction)
        assertTrue(second.pendingTurns.isEmpty())
    }

    @Test
    fun c15FastReversalSurvives() {
        val start = GameRules.newGame(Difficulty.LEVEL_1, FixedRandom(0)).copy(status = GameStatus.RUNNING)
        val queued = Turns.enqueue(Turns.enqueue(start, Direction.UP), Direction.LEFT)
        val first = GameRules.tick(queued, FixedRandom())
        assertEquals(GameStatus.RUNNING, first.state.status)
        assertEquals(Cell(10, 9), first.state.snake.first())
        val second = GameRules.tick(first.state, FixedRandom())
        assertEquals(GameStatus.RUNNING, second.state.status)
        assertEquals(Cell(9, 9), second.state.snake.first())
    }

    @Test
    fun c13GoStartsMovingOnlyFromCountdown() {
        val base = running()
        GameStatus.entries.forEach {
            val s = base.copy(status = it)
            val expected = if (it == GameStatus.COUNTDOWN) GameStatus.RUNNING else it
            assertEquals(expected, GameRules.go(s).status)
        }
    }

    @Test
    fun c20PauseClearsTurns() {
        listOf(GameStatus.RUNNING, GameStatus.COUNTDOWN).forEach {
            val paused = GameRules.pause(running().copy(status = it, pendingTurns = listOf(Direction.UP)))
            assertEquals(GameStatus.PAUSED, paused.status)
            assertTrue(paused.pendingTurns.isEmpty())
            assertEquals(paused, GameRules.tick(paused, FixedRandom()).state)
        }
    }

    @Test
    fun c20ResumeCountsDownAgain() {
        val base = running()
        GameStatus.entries.forEach {
            val s = base.copy(status = it)
            val expected = if (it == GameStatus.PAUSED) GameStatus.COUNTDOWN else it
            assertEquals(expected, GameRules.resume(s).status)
        }
        listOf(GameStatus.PAUSED, GameStatus.LOST, GameStatus.WON).forEach {
            val s = base.copy(status = it)
            assertEquals(s, GameRules.pause(s))
        }
    }
}
