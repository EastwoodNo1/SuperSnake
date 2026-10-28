package io.github.eastwoodno1.supersnake.domain

object Turns {
    const val CAPACITY = 2

    fun enqueue(
        state: GameState,
        direction: Direction,
    ): GameState {
        if (state.status != GameStatus.COUNTDOWN && state.status != GameStatus.RUNNING) return state
        val kept = if (state.pendingTurns.size < CAPACITY) state.pendingTurns else state.pendingTurns.dropLast(1)
        val reference = kept.lastOrNull() ?: state.direction
        if (direction == reference || direction == reference.opposite) return state
        return state.copy(pendingTurns = kept + direction)
    }
}
