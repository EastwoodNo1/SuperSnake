package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CellTest {
    @Test
    fun plusStepsOneCell() =
        assertEquals(
            listOf(Cell(3, 3), Cell(3, 5), Cell(2, 4), Cell(4, 4)),
            Direction.entries.map { Cell(3, 4) + it },
        )
}
