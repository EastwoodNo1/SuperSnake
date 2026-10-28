package io.github.eastwoodno1.supersnake.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class SwipeControlTest {
    private val control = SwipeControl(10f)

    @Test
    fun c24SwipeTurnsOncePastThreshold() {
        control.down(1, 0f, 0f)
        assertEquals(emptyList(), control.move(1, 9f, 0f))
        assertEquals(listOf<GameInput>(GameInput.Turn(Direction.RIGHT)), control.move(1, 10f, 0f))
    }

    @Test
    fun c24OtherFingerIgnored() {
        control.down(1, 0f, 0f)
        assertEquals(emptyList(), control.move(2, 50f, 0f))
    }

    @Test
    fun upResets() {
        control.down(1, 0f, 0f)
        control.up(1)
        control.down(2, 0f, 0f)
        assertEquals(listOf<GameInput>(GameInput.Turn(Direction.UP)), control.move(2, 0f, -10f))
    }
}
