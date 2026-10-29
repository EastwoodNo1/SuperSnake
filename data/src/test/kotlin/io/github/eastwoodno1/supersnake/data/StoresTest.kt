package io.github.eastwoodno1.supersnake.data

import io.github.eastwoodno1.supersnake.domain.ControlScheme
import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.HighScore
import io.github.eastwoodno1.supersnake.domain.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.job
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.time.LocalDate
import kotlin.test.assertEquals

class StoresTest {
    @TempDir
    lateinit var dir: File

    private val scopes = mutableListOf<CoroutineScope>()

    private fun newScope() = CoroutineScope(Dispatchers.IO + Job()).also { scopes += it }

    @AfterEach
    fun cancelScopes() {
        scopes.forEach { it.cancel() }
    }

    private val entry = HighScore(42, Difficulty.LEVEL_4, LocalDate.of(2026, 10, 28))

    @Test
    fun settingsPersistAcrossStores() =
        runTest {
            val file = File(dir, Stores.SETTINGS_FILE)
            val first = newScope()
            Stores.settings(file, first).updateData { Settings(Difficulty.LEVEL_3) }
            first.coroutineContext.job.cancelAndJoin()
            assertEquals(Settings(Difficulty.LEVEL_3), Stores.settings(file, newScope()).data.first())
        }

    @Test
    fun highScoresPersistAcrossStores() =
        runTest {
            val file = File(dir, Stores.HIGH_SCORES_FILE)
            val first = newScope()
            Stores.highScores(file, first).updateData { listOf(entry) }
            first.coroutineContext.job.cancelAndJoin()
            assertEquals(listOf(entry), Stores.highScores(file, newScope()).data.first())
        }

    @Test
    fun corruptFileOnDiskStillWorks() =
        runTest {
            val settingsFile = File(dir, Stores.SETTINGS_FILE)
            val scoresFile = File(dir, Stores.HIGH_SCORES_FILE)
            val noise = byteArrayOf(0, -1, 19, -128, 10, 61, -2)
            settingsFile.writeBytes(noise)
            scoresFile.writeBytes(noise)
            val settings = Stores.settings(settingsFile, newScope())
            val scores = Stores.highScores(scoresFile, newScope())
            assertEquals(Settings(), settings.data.first())
            assertEquals(emptyList(), scores.data.first())
            settings.updateData { it.copy(controlScheme = ControlScheme.DPAD) }
            scores.updateData { listOf(entry) }
            assertEquals(Settings(controlScheme = ControlScheme.DPAD), settings.data.first())
            assertEquals(listOf(entry), scores.data.first())
        }

    @Test
    fun fileNamesAreStable() {
        assertEquals("settings.txt", Stores.SETTINGS_FILE)
        assertEquals("high_scores.txt", Stores.HIGH_SCORES_FILE)
    }
}
