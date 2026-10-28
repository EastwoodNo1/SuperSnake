package io.github.eastwoodno1.supersnake.domain

fun sceneFrame(
    snake: List<Cell> = listOf(Cell(10, 10)),
    previousSnake: List<Cell> = snake,
    progress: Float = 0f,
    orb: Cell? = Cell(5, 5),
    status: GameStatus = GameStatus.RUNNING,
    events: List<GameEvent> = emptyList(),
) = GameFrame(
    state = GameState(snake, Direction.RIGHT, emptyList(), orb, 0, 0, Difficulty.LEVEL_1, status),
    previousSnake = previousSnake,
    progress = progress,
    countdown = null,
    events = events,
    announcements = emptyList(),
    result = null,
)
