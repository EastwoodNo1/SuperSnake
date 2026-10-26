package io.github.eastwoodno1.supersnake.domain

enum class Direction(
    val dx: Int,
    val dy: Int,
) {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0),
    ;

    val opposite: Direction
        get() =
            when (this) {
                UP -> DOWN
                DOWN -> UP
                LEFT -> RIGHT
                RIGHT -> LEFT
            }
}
