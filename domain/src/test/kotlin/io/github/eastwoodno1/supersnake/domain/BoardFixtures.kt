package io.github.eastwoodno1.supersnake.domain

fun almostFullSnake(): List<Cell> {
    val path =
        (0 until Board.SIZE).flatMap { y ->
            val xs = if (y % 2 == 0) 0 until Board.SIZE else (Board.SIZE - 1) downTo 0
            xs.map { x -> Cell(x, y) }
        }
    return path.take(399).reversed()
}
