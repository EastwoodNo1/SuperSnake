package io.github.eastwoodno1.supersnake.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KeyBindingsTest {
    private val bindings = KeyBindings(mapOf(19 to GameKey.UP, 62 to GameKey.PAUSE))

    @Test
    fun boundCodeSendsItsInput() {
        val sent = mutableListOf<GameInput>()
        assertTrue(bindings.press(19, sent::add))
        assertTrue(bindings.press(62, sent::add))
        assertEquals(listOf(GameInput.Turn(Direction.UP), GameInput.TogglePause), sent)
    }

    @Test
    fun unboundCodeSendsNothing() {
        val sent = mutableListOf<GameInput>()
        assertFalse(bindings.press(7, sent::add))
        assertEquals(emptyList(), sent)
    }
}
