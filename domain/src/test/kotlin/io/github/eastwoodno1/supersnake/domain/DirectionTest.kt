package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DirectionTest {
    @Test
    fun oppositeOfEachDirection() {
        assertEquals(Direction.DOWN, Direction.UP.opposite)
        assertEquals(Direction.UP, Direction.DOWN.opposite)
        assertEquals(Direction.RIGHT, Direction.LEFT.opposite)
        assertEquals(Direction.LEFT, Direction.RIGHT.opposite)
    }

    @Test
    fun oppositeIsInvolutive() {
        Direction.entries.forEach { assertEquals(it, it.opposite.opposite) }
    }

    @Test
    fun vectorsAreUnitSteps() {
        assertEquals(
            listOf(0 to -1, 0 to 1, -1 to 0, 1 to 0),
            Direction.entries.map { it.dx to it.dy },
        )
    }
}
