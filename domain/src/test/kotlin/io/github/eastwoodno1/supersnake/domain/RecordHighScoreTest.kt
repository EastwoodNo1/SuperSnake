package io.github.eastwoodno1.supersnake.domain

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class RecordHighScoreTest {
    private val day = LocalDate.of(2026, 10, 28)

    private fun entry(score: Int) = HighScore(score, Difficulty.LEVEL_1, day)

    @Test
    fun c12RecordStoresWithTodaysDate() =
        runTest {
            val repository = FakeHighScoreRepository()
            val result = RecordHighScore(repository, FakeClock(date = day))(30, Difficulty.LEVEL_2)
            assertEquals(listOf(HighScore(30, Difficulty.LEVEL_2, day)), repository.current)
            assertEquals(ScoreResult(1, true), result)
        }

    @Test
    fun c22EquallingBestIsNotNewBest() =
        runTest {
            val repository = FakeHighScoreRepository(listOf(entry(40)))
            assertEquals(ScoreResult(2, false), RecordHighScore(repository, FakeClock())(40, Difficulty.LEVEL_1))
        }

    @Test
    fun recordBelowBestIsNotNewBest() =
        runTest {
            val repository = FakeHighScoreRepository(listOf(entry(40)))
            assertEquals(ScoreResult(2, false), RecordHighScore(repository, FakeClock())(30, Difficulty.LEVEL_1))
        }

    @Test
    fun recordOffTableIsNeither() =
        runTest {
            val full = (1..10).map { entry(50 + it) }.reversed()
            val repository = FakeHighScoreRepository(full)
            assertEquals(ScoreResult(null, false), RecordHighScore(repository, FakeClock())(10, Difficulty.LEVEL_1))
            assertEquals(full, repository.current)
        }
}
