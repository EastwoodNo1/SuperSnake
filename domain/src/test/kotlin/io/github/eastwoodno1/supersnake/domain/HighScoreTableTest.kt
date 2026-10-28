package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HighScoreTableTest {
    private val day = LocalDate.of(2026, 10, 28)

    private fun entry(
        score: Int,
        date: LocalDate = day,
    ) = HighScore(score, Difficulty.LEVEL_1, date)

    @Test
    fun c12InsertKeepsDescendingOrder() {
        val placement = HighScoreTable.insert(listOf(entry(80), entry(20)), entry(50))
        assertEquals(listOf(80, 50, 20), placement.table.map { it.score })
        assertEquals(2, placement.rank)
    }

    @Test
    fun c22TieGoesBelowExisting() {
        val older = entry(50, day.minusDays(3))
        val placement = HighScoreTable.insert(listOf(older), entry(50))
        assertEquals(2, placement.rank)
        assertEquals(older, placement.table.first())
    }

    @Test
    fun c22FullTableNeedsToBeatTenth() {
        val full = (10 downTo 1).map { entry(it * 10) }
        val rejected = HighScoreTable.insert(full, entry(10))
        assertNull(rejected.rank)
        assertEquals(full, rejected.table)
        val accepted = HighScoreTable.insert(full, entry(11))
        assertEquals(10, accepted.rank)
        assertEquals(11, accepted.table.last().score)
        assertEquals(10, accepted.table.size)
    }

    @Test
    fun c22ZeroIsNotSaved() {
        val placement = HighScoreTable.insert(emptyList(), entry(0))
        assertNull(placement.rank)
        assertTrue(placement.table.isEmpty())
    }

    @Test
    fun normalizeSortsCapsAndDropsNonPositive() {
        val first = entry(30, day.minusDays(1))
        val second = entry(30, day.minusDays(2))
        val input =
            listOf(first, entry(0), entry(-3), second) +
                listOf(5, 90, 15, 25, 35, 45, 55, 65).map { entry(it) }
        val result = HighScoreTable.normalize(input)
        assertEquals(10, result.size)
        assertEquals(result.map { it.score }.sortedDescending(), result.map { it.score })
        assertTrue(result.all { it.score > 0 })
        assertEquals(listOf(first, second), result.filter { it.score == 30 })
    }
}
