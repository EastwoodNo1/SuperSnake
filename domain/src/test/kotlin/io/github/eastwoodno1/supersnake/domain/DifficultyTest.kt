package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DifficultyTest {
    @Test
    fun c8TickIntervals() =
        assertEquals(
            listOf(400L, 250L, 227L, 205L, 182L, 160L, 137L, 115L, 92L, 70L),
            Difficulty.entries.map { it.tickMillis },
        )

    @Test
    fun c8PointsPerOrb() =
        assertEquals(
            listOf(1, 1, 2, 3, 4, 5, 6, 7, 8, 9),
            Difficulty.entries.map { it.pointsPerOrb },
        )

    @Test
    fun c8Labels() =
        assertEquals(
            listOf("A", "1", "2", "3", "4", "5", "6", "7", "8", "9"),
            Difficulty.entries.map { it.label },
        )
}
