package io.github.eastwoodno1.supersnake.domain

sealed interface GameInput {
    data class Turn(
        val direction: Direction,
    ) : GameInput

    data object TogglePause : GameInput
}

enum class GameKey(
    val input: GameInput,
) {
    UP(GameInput.Turn(Direction.UP)),
    DOWN(GameInput.Turn(Direction.DOWN)),
    LEFT(GameInput.Turn(Direction.LEFT)),
    RIGHT(GameInput.Turn(Direction.RIGHT)),
    PAUSE(GameInput.TogglePause),
}
