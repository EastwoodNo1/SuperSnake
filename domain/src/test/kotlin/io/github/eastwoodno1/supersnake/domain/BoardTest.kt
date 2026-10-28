package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BoardTest {
    @Test
    fun c1BoardIsTwentyByTwentyRowMajor() {
        assertEquals(400, Board.cells.size)
        assertEquals(listOf(Cell(0, 0), Cell(1, 0)), Board.cells.take(2))
        assertEquals(Cell(0, 1), Board.cells[20])
        assertEquals(Cell(19, 19), Board.cells.last())
    }

    @Test
    fun s2ContainsEdgesOnly() {
        assertTrue(Board.contains(Cell(0, 0)) && Board.contains(Cell(19, 19)))
        listOf(Cell(-1, 0), Cell(20, 0), Cell(0, -1), Cell(0, 20)).forEach { assertFalse(Board.contains(it)) }
    }
}
