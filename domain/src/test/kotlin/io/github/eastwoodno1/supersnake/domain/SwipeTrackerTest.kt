package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SwipeTrackerTest {
    private val threshold = 48f

    private fun downAtOrigin() = SwipeTracker().down(1, 0f, 0f)

    private fun directionOf(
        x: Float,
        y: Float,
    ) = downAtOrigin().move(1, x, y, threshold).direction

    @Test
    fun c24FiresOnceThresholdPassed() {
        val first = downAtOrigin().move(1, 30f, 0f, threshold)
        assertNull(first.direction)
        val second = first.tracker.move(1, 50f, 5f, threshold)
        assertEquals(Direction.RIGHT, second.direction)
        assertEquals(SwipeTracker(1, 50f, 5f), second.tracker)
    }

    @Test
    fun c24DominantAxis() {
        assertEquals(Direction.LEFT, directionOf(-100f, 10f))
        assertEquals(Direction.UP, directionOf(5f, -90f))
        assertEquals(Direction.DOWN, directionOf(5f, 90f))
    }

    @Test
    fun c24ThresholdInclusive() {
        assertEquals(Direction.RIGHT, directionOf(48f, 0f))
        assertNull(directionOf(47.9f, 0f))
    }

    @Test
    fun c24OneStrokeTurnsTwice() {
        val first = downAtOrigin().move(1, 50f, 5f, threshold)
        val second = first.tracker.move(1, 52f, -50f, threshold)
        assertEquals(Direction.RIGHT, first.direction)
        assertEquals(Direction.UP, second.direction)
    }

    @Test
    fun c24ExactDiagonalIsNoTurn() {
        val step = downAtOrigin().move(1, 60f, -60f, threshold)
        assertNull(step.direction)
        assertEquals(downAtOrigin(), step.tracker)
    }

    @Test
    fun c24NonFiniteIsNoTurn() {
        assertNull(directionOf(Float.NaN, 100f))
        assertNull(directionOf(Float.POSITIVE_INFINITY, 0f))
        assertNull(directionOf(0f, Float.NaN))
        assertEquals(downAtOrigin(), downAtOrigin().move(1, Float.NaN, 100f, threshold).tracker)
    }

    @Test
    fun c24OnlyFirstFingerCounts() {
        val tracker = downAtOrigin().down(2, 500f, 500f)
        assertEquals(downAtOrigin(), tracker)
        val step = tracker.move(2, 900f, 500f, threshold)
        assertNull(step.direction)
        assertEquals(downAtOrigin(), step.tracker)
    }

    @Test
    fun c24LiftAndNewStroke() {
        val tracker = downAtOrigin()
        assertEquals(tracker, tracker.up(2))
        val lifted = tracker.up(1)
        assertNull(lifted.pointerId)
        val step = lifted.down(2, 10f, 10f).move(2, 70f, 10f, threshold)
        assertEquals(Direction.RIGHT, step.direction)
        assertEquals(SwipeTracker(), lifted.up(1))
    }

    @Test
    fun moveWithoutDownIsIgnored() {
        val fresh = SwipeTracker()
        val step = fresh.move(1, 100f, 0f, threshold)
        assertNull(step.direction)
        assertEquals(fresh, step.tracker)
    }
}
