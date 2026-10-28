package io.github.eastwoodno1.supersnake.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class HighScoresTest {
    private fun entry(score: Int) = HighScore(score, Difficulty.LEVEL_1, LocalDate.of(2026, 10, 28))

    @Test
    fun bestIsTopScoreOrZero() =
        runTest {
            assertEquals(0, HighScores(FakeHighScoreRepository()).best.first())
            val repository = FakeHighScoreRepository(listOf(entry(70), entry(20)))
            val scores = HighScores(repository)
            assertEquals(70, scores.best.first())
            assertEquals(listOf(70, 20), scores.top.first().map { it.score })
        }
}
