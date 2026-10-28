package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TurnsTest {
    private fun state(
        status: GameStatus = GameStatus.RUNNING,
        direction: Direction = Direction.RIGHT,
        queue: List<Direction> = emptyList(),
    ): GameState =
        GameState(
            snake = listOf(Cell(10, 10), Cell(9, 10), Cell(8, 10)),
            direction = direction,
            pendingTurns = queue,
            orb = Cell(0, 0),
            score = 0,
            orbsEaten = 0,
            difficulty = Difficulty.LEVEL_1,
            status = status,
        )

    @Test
    fun c16TurnIsQueued() {
        assertEquals(listOf(Direction.UP), Turns.enqueue(state(), Direction.UP).pendingTurns)
    }

    @Test
    fun c15OppositeOfCurrentIgnoredWhenQueueEmpty() {
        assertEquals(emptyList(), Turns.enqueue(state(), Direction.LEFT).pendingTurns)
    }

    @Test
    fun c15OppositeOfLastQueuedIgnored() {
        val queued = state(queue = listOf(Direction.UP))
        assertEquals(listOf(Direction.UP), Turns.enqueue(queued, Direction.DOWN).pendingTurns)
    }

    @Test
    fun c15SameAsReferenceIgnored() {
        assertEquals(emptyList(), Turns.enqueue(state(), Direction.RIGHT).pendingTurns)
        val queued = state(queue = listOf(Direction.UP))
        assertEquals(listOf(Direction.UP), Turns.enqueue(queued, Direction.UP).pendingTurns)
    }

    @Test
    fun c16SecondTurnAppends() {
        val queued = state(queue = listOf(Direction.UP))
        assertEquals(listOf(Direction.UP, Direction.LEFT), Turns.enqueue(queued, Direction.LEFT).pendingTurns)
    }

    @Test
    fun c16FullQueueReplacesLastTurn() {
        val full = state(queue = listOf(Direction.UP, Direction.LEFT))
        assertEquals(listOf(Direction.UP, Direction.RIGHT), Turns.enqueue(full, Direction.RIGHT).pendingTurns)
    }

    @Test
    fun c16FullQueueReplacementIsChecked() {
        val full = state(queue = listOf(Direction.UP, Direction.LEFT))
        assertEquals(full.pendingTurns, Turns.enqueue(full, Direction.DOWN).pendingTurns)
        assertEquals(full.pendingTurns, Turns.enqueue(full, Direction.UP).pendingTurns)
    }

    @Test
    fun c14TurnsAcceptedDuringCountdown() {
        val countdown = state(status = GameStatus.COUNTDOWN)
        assertEquals(listOf(Direction.UP), Turns.enqueue(countdown, Direction.UP).pendingTurns)
    }

    @Test
    fun c14TurnsIgnoredWhenPausedOrEnded() {
        listOf(GameStatus.PAUSED, GameStatus.LOST, GameStatus.WON).forEach {
            val s = state(status = it)
            assertEquals(s, Turns.enqueue(s, Direction.UP))
        }
    }
}
