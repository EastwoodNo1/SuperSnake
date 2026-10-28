package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class BoardLayoutTest {
    @Test
    fun c1FitLetterboxesPortrait() = assertEquals(BoardFrame(0f, 300f, 50f), BoardLayout.fit(1000f, 1600f))

    @Test
    fun c1FitLetterboxesLandscape() = assertEquals(BoardFrame(300f, 0f, 50f), BoardLayout.fit(1600f, 1000f))

    @Test
    fun fitZeroOrNegativeIsEmpty() {
        listOf(BoardLayout.fit(0f, 0f), BoardLayout.fit(-5f, 100f)).forEach {
            assertEquals(0f, it.cellSize)
            assertFalse(it.left.isNaN() || it.top.isNaN() || it.cellSize.isNaN())
        }
    }
}
