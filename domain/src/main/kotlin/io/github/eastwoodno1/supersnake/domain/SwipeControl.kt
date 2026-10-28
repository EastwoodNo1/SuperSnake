package io.github.eastwoodno1.supersnake.domain

class SwipeControl(
    private val thresholdPx: Float,
) {
    private var tracker = SwipeTracker()

    fun down(
        pointerId: Int,
        x: Float,
        y: Float,
    ) {
        tracker = tracker.down(pointerId, x, y)
    }

    fun move(
        pointerId: Int,
        x: Float,
        y: Float,
    ): List<GameInput> {
        val step = tracker.move(pointerId, x, y, thresholdPx)
        tracker = step.tracker
        return listOfNotNull(step.direction?.let { GameInput.Turn(it) })
    }

    fun up(pointerId: Int) {
        tracker = tracker.up(pointerId)
    }
}
