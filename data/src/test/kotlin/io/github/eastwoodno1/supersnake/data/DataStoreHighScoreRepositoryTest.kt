package io.github.eastwoodno1.supersnake.data

import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.HighScore
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

class DataStoreHighScoreRepositoryTest {
    @TempDir
    lateinit var dir: File

    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private val date = LocalDate.of(2026, 10, 28)

    @AfterEach
    fun cancelScope() {
        scope.cancel()
    }

    private fun repository() = DataStoreHighScoreRepository(Stores.highScores(File(dir, Stores.HIGH_SCORES_FILE), scope))

    @Test
    fun startsEmpty() =
        runTest {
            assertEquals(emptyList(), repository().entries.first())
        }

    @Test
    fun updateNormalizesOnWrite() =
        runTest {
            val repository = repository()
            repository.update {
                it +
                    listOf(
                        HighScore(5, Difficulty.LEVEL_1, date),
                        HighScore(0, Difficulty.LEVEL_2, date),
                        HighScore(9, Difficulty.LEVEL_3, date),
                    )
            }
            assertEquals(listOf(9, 5), repository.entries.first().map { it.score })
        }
}
