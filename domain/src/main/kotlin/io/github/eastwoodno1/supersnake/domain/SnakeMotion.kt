package io.github.eastwoodno1.supersnake.domain

data class Point(
    val x: Float,
    val y: Float,
)

object SnakeMotion {
    fun interpolate(
        previous: List<Cell>,
        current: List<Cell>,
        progress: Float,
    ): List<Point> {
        val t = progress.coerceIn(0f, 1f)
        return current.mapIndexed { i, to ->
            val from = previous.getOrNull(i) ?: previous.lastOrNull() ?: to
            Point(from.x + (to.x - from.x) * t, from.y + (to.y - from.y) * t)
        }
    }
}
