package io.github.eastwoodno1.supersnake.domain

data class Cell(
    val x: Int,
    val y: Int,
) {
    operator fun plus(direction: Direction): Cell = Cell(x + direction.dx, y + direction.dy)
}
