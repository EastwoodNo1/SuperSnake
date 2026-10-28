package io.github.eastwoodno1.supersnake.data

import io.github.eastwoodno1.supersnake.domain.Clock
import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.RecordHighScore
import io.github.eastwoodno1.supersnake.domain.ScoreResult
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

class RecordHighScoreOverStoreTest {
    @TempDir
    lateinit var dir: File

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private val clock =
        object : Clock {
            override fun nowMillis(): Long = 0

            override fun today(): LocalDate = LocalDate.of(2026, 10, 28)
        }

    @AfterEach
    fun cancelScope() {
        scope.cancel()
    }

    @Test
    fun recordsIntoHandEditedUnsortedFile() =
        runTest {
            val file = File(dir, Stores.HIGH_SCORES_FILE)
            file.writeText("20|LEVEL_1|2026-10-01\n90|LEVEL_1|2026-10-01\n50|LEVEL_1|2026-10-01\n")
            val repository = DataStoreHighScoreRepository(Stores.highScores(file, scope))
            val record = RecordHighScore(repository, clock)
            assertEquals(ScoreResult(2, false), record(70, Difficulty.LEVEL_1))
            assertEquals(ScoreResult(1, true), record(95, Difficulty.LEVEL_1))
            assertEquals(listOf(95, 90, 70, 50, 20), repository.entries.first().map { it.score })
        }
}
