package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class BurstTest {
    @Test
    fun burstStartsOnOrbEaten() {
        val frame = sceneFrame(snake = listOf(Cell(7, 8), Cell(6, 8)), events = listOf(GameEvent.ORB_EATEN))
        assertEquals(Burst(Cell(7, 8), 500), Burst.next(null, frame, 500))
    }

    @Test
    fun burstKeptOtherwise() {
        val previous = Burst(Cell(1, 1), 100)
        assertSame(previous, Burst.next(previous, sceneFrame(events = listOf(GameEvent.LOST)), 900))
    }

    @Test
    fun burstReplacedByNextOrb() {
        val previous = Burst(Cell(1, 1), 100)
        val frame = sceneFrame(snake = listOf(Cell(2, 2)), events = listOf(GameEvent.ORB_EATEN))
        assertEquals(Burst(Cell(2, 2), 700), Burst.next(previous, frame, 700))
    }

    @Test
    fun motionReducedOnlyAtZero() {
        assertEquals(true, Motion.reduced(0f))
        assertEquals(false, Motion.reduced(0.5f))
        assertEquals(false, Motion.reduced(1f))
    }
}
