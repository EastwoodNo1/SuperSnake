package io.github.eastwoodno1.supersnake.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReducedMotionTest {
    @Test
    fun startsFromTheCurrentScale() {
        assertTrue(ReducedMotion { 0f }.reduced.value)
        assertFalse(ReducedMotion { 1f }.reduced.value)
    }

    @Test
    fun followsAScaleChangeOnRefresh() {
        var scale = 1f
        val motion = ReducedMotion { scale }
        scale = 0f
        assertFalse(motion.reduced.value)
        motion.refresh()
        assertTrue(motion.reduced.value)
        scale = 1.5f
        motion.refresh()
        assertEquals(false, motion.reduced.value)
    }
}
