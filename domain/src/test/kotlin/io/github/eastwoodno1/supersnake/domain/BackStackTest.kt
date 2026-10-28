package io.github.eastwoodno1.supersnake.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BackStackTest {
    @Test
    fun startsAtMenu() {
        assertEquals(Screen.MENU, BackStack().top)
        assertFalse(BackStack().canPop)
    }

    @Test
    fun pushAndPop() {
        val pushed = BackStack().push(Screen.GAME)
        assertEquals(Screen.GAME, pushed.top)
        assertTrue(pushed.canPop)
        assertEquals(Screen.MENU, pushed.pop().top)
    }

    @Test
    fun pushingTheTopAgainChangesNothing() {
        val once = BackStack().push(Screen.GAME)
        assertEquals(listOf(Screen.MENU, Screen.GAME), once.push(Screen.GAME).screens)
    }

    @Test
    fun popAtRootStays() {
        assertEquals(BackStack(), BackStack().pop())
    }

    @Test
    fun homeClearsToMenu() {
        val deep = BackStack(listOf(Screen.MENU, Screen.SETTINGS, Screen.GAME))
        assertEquals(listOf(Screen.MENU), deep.home().screens)
    }

    @Test
    fun pushMenuIsHome() {
        val deep = BackStack(listOf(Screen.MENU, Screen.SETTINGS, Screen.GAME))
        assertEquals(listOf(Screen.MENU), deep.push(Screen.MENU).screens)
    }
}
