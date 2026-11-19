package io.github.eastwoodno1.supersnake.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.io.IOException
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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

    private class FailingRepository(
        private val failure: Throwable,
    ) : HighScoreRepository {
        override val entries: Flow<List<HighScore>> = flow { throw failure }

        override suspend fun update(transform: (List<HighScore>) -> List<HighScore>) = Unit
    }

    @Test
    fun unreadableStoreYieldsNoScoresInsteadOfFailing() =
        runTest {
            val scores = HighScores(FailingRepository(IOException("unreadable")))
            assertEquals(emptyList(), scores.top.first())
            assertEquals(0, scores.best.first())
        }

    @Test
    fun programmingErrorsStillSurface() =
        runTest {
            val scores = HighScores(FailingRepository(IllegalStateException("bug")))
            assertFailsWith<IllegalStateException> { scores.top.first() }
        }
}
