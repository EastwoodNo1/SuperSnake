package io.github.eastwoodno1.supersnake.domain

object GameRules {
    private const val START_X = 10
    private const val START_Y = 10
    private const val START_LENGTH = 3

    fun newGame(
        difficulty: Difficulty,
        random: RandomSource,
    ): GameState {
        val snake = List(START_LENGTH) { Cell(START_X - it, START_Y) }
        return GameState(
            snake = snake,
            direction = Direction.RIGHT,
            pendingTurns = emptyList(),
            orb = spawnOrb(snake.toSet(), random),
            score = 0,
            orbsEaten = 0,
            difficulty = difficulty,
            status = GameStatus.COUNTDOWN,
        )
    }

    fun tick(
        state: GameState,
        random: RandomSource,
    ): TickResult {
        if (state.status != GameStatus.RUNNING) return TickResult(state, emptyList())
        val direction = state.pendingTurns.firstOrNull() ?: state.direction
        val pendingTurns = state.pendingTurns.drop(1)
        val head = state.snake.first() + direction
        val grows = head == state.orb
        val body = if (grows) state.snake else state.snake.dropLast(1)
        if (!Board.contains(head) || head in body) {
            return TickResult(state.copy(status = GameStatus.LOST), listOf(GameEvent.LOST))
        }
        val snake = listOf(head) + body
        val moved = state.copy(snake = snake, direction = direction, pendingTurns = pendingTurns)
        if (!grows) return TickResult(moved, emptyList())
        val eaten =
            moved.copy(
                score = state.score + state.difficulty.pointsPerOrb,
                orbsEaten = state.orbsEaten + 1,
            )
        val orb =
            spawnOrb(snake.toSet(), random)
                ?: return TickResult(
                    eaten.copy(orb = null, status = GameStatus.WON),
                    listOf(GameEvent.ORB_EATEN, GameEvent.WON),
                )
        return TickResult(eaten.copy(orb = orb), listOf(GameEvent.ORB_EATEN))
    }

    private fun spawnOrb(
        occupied: Set<Cell>,
        random: RandomSource,
    ): Cell? {
        val free = Board.cells.filter { it !in occupied }
        return if (free.isEmpty()) null else free[random.nextInt(free.size)]
    }
}
