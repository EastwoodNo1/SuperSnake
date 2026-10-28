package io.github.eastwoodno1.supersnake.domain

import kotlin.math.abs

data class SwipeStep(
    val tracker: SwipeTracker,
    val direction: Direction?,
)

data class SwipeTracker(
    val pointerId: Int? = null,
    val originX: Float = 0f,
    val originY: Float = 0f,
) {
    fun down(
        pointerId: Int,
        x: Float,
        y: Float,
    ): SwipeTracker = if (this.pointerId == null) SwipeTracker(pointerId, x, y) else this

    fun move(
        pointerId: Int,
        x: Float,
        y: Float,
        threshold: Float,
    ): SwipeStep {
        val dx = x - originX
        val dy = y - originY
        val ax = abs(dx)
        val ay = abs(dy)
        val direction =
            when {
                pointerId != this.pointerId -> null
                !dx.isFinite() || !dy.isFinite() -> null
                ax == ay || maxOf(ax, ay) < threshold -> null
                ax > ay -> if (dx > 0f) Direction.RIGHT else Direction.LEFT
                else -> if (dy > 0f) Direction.DOWN else Direction.UP
            }
        return if (direction == null) SwipeStep(this, null) else SwipeStep(SwipeTracker(pointerId, x, y), direction)
    }

    fun up(pointerId: Int): SwipeTracker = if (pointerId == this.pointerId) SwipeTracker() else this
}
