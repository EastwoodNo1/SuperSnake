package io.github.eastwoodno1.supersnake.data

import io.github.eastwoodno1.supersnake.domain.Clock
import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.GameSession
import io.github.eastwoodno1.supersnake.domain.GameStatus
import io.github.eastwoodno1.supersnake.domain.RandomSource
import io.github.eastwoodno1.supersnake.domain.RecordHighScore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.time.LocalDate
import kotlin.test.assertEquals

class GameSessionOverStoreTest {
    @TempDir
    lateinit var dir: File

    private val scopes = mutableListOf<CoroutineScope>()

    private var now = 0L

    private val clock =
        object : Clock {
            override fun nowMillis(): Long = now

            override fun today(): LocalDate = LocalDate.of(2026, 11, 19)
        }

    @AfterEach
    fun cancelScopes() {
        scopes.forEach { it.cancel() }
    }

    private fun open(): DataStoreHighScoreRepository {
        val scope = CoroutineScope(Dispatchers.IO + Job())
        scopes += scope
        return DataStoreHighScoreRepository(Stores.highScores(File(dir, Stores.HIGH_SCORES_FILE), scope))
    }

    private fun coldStart(): DataStoreHighScoreRepository {
        scopes.forEach { it.cancel() }
        scopes.clear()
        return open()
    }

    private fun newSession(
        difficulty: Difficulty,
        repository: DataStoreHighScoreRepository,
    ): GameSession {
        val draws = mutableListOf(208, 0)
        return GameSession(difficulty, clock, RandomSource { draws.removeAt(0) }, RecordHighScore(repository, clock))
    }

    private suspend fun GameSession.playUntilLost() {
        now += 3000
        advance()
        while (frame.state.status != GameStatus.LOST) {
            now += 250
            advance()
        }
    }

    private suspend fun DataStoreHighScoreRepository.scores() = entries.first().map { it.score }

    @Test
    fun twoLostGamesSurviveAColdStart() =
        runTest {
            val repository = open()
            newSession(Difficulty.LEVEL_1, repository).playUntilLost()
            newSession(Difficulty.LEVEL_2, repository).playUntilLost()
            assertEquals(listOf(2, 1), coldStart().scores())
        }

    @Test
    fun twoLostGamesWithEqualScoresBothSurviveAColdStart() =
        runTest {
            val repository = open()
            newSession(Difficulty.LEVEL_1, repository).playUntilLost()
            newSession(Difficulty.LEVEL_1, repository).playUntilLost()
            assertEquals(listOf(1, 1), coldStart().scores())
        }

    @Test
    fun playAgainWhileTheOldSessionHasEndedKeepsBoth() =
        runTest {
            val repository = open()
            val first = newSession(Difficulty.LEVEL_1, repository)
            first.playUntilLost()
            val second = newSession(Difficulty.LEVEL_2, repository)
            now += 250
            first.advance()
            second.playUntilLost()
            first.advance()
            assertEquals(listOf(2, 1), coldStart().scores())
        }

    @Test
    fun aScoreTyingAnExistingEntryIsKept() =
        runTest {
            File(dir, Stores.HIGH_SCORES_FILE).writeText("1|LEVEL_1|2026-11-01\n")
            val repository = open()
            newSession(Difficulty.LEVEL_1, repository).playUntilLost()
            assertEquals(listOf(1, 1), coldStart().scores())
        }
}
