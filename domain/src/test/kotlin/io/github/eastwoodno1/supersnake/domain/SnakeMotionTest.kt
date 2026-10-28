package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SnakeMotionTest {
    @Test
    fun c23GlideMovesEachSegment() =
        assertEquals(
            listOf(Point(10.5f, 10f), Point(9.5f, 10f), Point(8.5f, 10f)),
            SnakeMotion.interpolate(
                listOf(Cell(10, 10), Cell(9, 10), Cell(8, 10)),
                listOf(Cell(11, 10), Cell(10, 10), Cell(9, 10)),
                0.5f,
            ),
        )

    @Test
    fun c23GrowthHoldsNewTail() {
        val previous = listOf(Cell(10, 10), Cell(9, 10))
        val current = listOf(Cell(11, 10), Cell(10, 10), Cell(9, 10))
        listOf(0f, 0.25f, 0.5f, 1f).forEach {
            assertEquals(Point(9f, 10f), SnakeMotion.interpolate(previous, current, it).last())
        }
    }

    @Test
    fun glideClampsProgress() {
        val previous = listOf(Cell(1, 1))
        val current = listOf(Cell(2, 1))
        assertEquals(SnakeMotion.interpolate(previous, current, 0f), SnakeMotion.interpolate(previous, current, -1f))
        assertEquals(SnakeMotion.interpolate(previous, current, 1f), SnakeMotion.interpolate(previous, current, 2f))
    }

    @Test
    fun glideFirstFrame() =
        assertEquals(
            listOf(Point(3f, 4f), Point(2f, 4f)),
            SnakeMotion.interpolate(emptyList(), listOf(Cell(3, 4), Cell(2, 4)), 0.5f),
        )
}
