package io.github.eastwoodno1.supersnake.domain

object Board {
    const val SIZE = 20

    val cells: List<Cell> = (0 until SIZE).flatMap { y -> (0 until SIZE).map { x -> Cell(x, y) } }

    fun contains(cell: Cell): Boolean = cell.x in 0 until SIZE && cell.y in 0 until SIZE
}
