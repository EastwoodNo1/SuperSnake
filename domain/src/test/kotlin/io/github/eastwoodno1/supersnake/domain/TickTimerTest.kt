package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TickTimerTest {
    private val timer = TickTimer(100, 0)

    @Test
    fun noStepBeforeInterval() {
        val advance = timer.advance(99)
        assertFalse(advance.ticked)
        assertEquals(timer, advance.timer)
    }

    @Test
    fun jitterKeepsRhythm() {
        val advance = timer.advance(130)
        assertTrue(advance.ticked)
        assertEquals(100, advance.timer.lastTickMillis)
    }

    @Test
    fun stallStepsOnceAndDropsLeftover() {
        val advance = timer.advance(2000)
        assertTrue(advance.ticked)
        assertEquals(2000, advance.timer.lastTickMillis)
    }

    @Test
    fun exactlyTwoIntervalsIsAStall() {
        val advance = timer.advance(200)
        assertTrue(advance.ticked)
        assertEquals(200, advance.timer.lastTickMillis)
    }

    @Test
    fun backwardsClockDoesNothing() {
        val late = TickTimer(100, 500)
        val advance = late.advance(400)
        assertFalse(advance.ticked)
        assertEquals(late, advance.timer)
        assertEquals(0f, late.progress(400))
    }

    @Test
    fun progressIsClamped() {
        assertEquals(0.5f, timer.progress(50))
        assertEquals(1f, timer.progress(250))
    }
}
