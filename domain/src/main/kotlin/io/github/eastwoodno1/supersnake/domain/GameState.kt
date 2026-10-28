package io.github.eastwoodno1.supersnake.domain

enum class GameStatus { COUNTDOWN, RUNNING, PAUSED, LOST, WON }

enum class GameEvent { ORB_EATEN, LOST, WON }

data class GameState(
    val snake: List<Cell>,
    val direction: Direction,
    val pendingTurns: List<Direction>,
    val orb: Cell?,
    val score: Int,
    val orbsEaten: Int,
    val difficulty: Difficulty,
    val status: GameStatus,
)

data class TickResult(
    val state: GameState,
    val events: List<GameEvent>,
)
