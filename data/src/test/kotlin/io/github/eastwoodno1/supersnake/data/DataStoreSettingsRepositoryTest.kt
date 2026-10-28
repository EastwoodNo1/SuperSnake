package io.github.eastwoodno1.supersnake.data

import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.Settings
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
import kotlin.test.assertEquals

class DataStoreSettingsRepositoryTest {
    @TempDir
    lateinit var dir: File

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    @AfterEach
    fun cancelScope() {
        scope.cancel()
    }

    @Test
    fun updateTransformsAndEmitsTheStoredSettings() =
        runTest {
            val repository = DataStoreSettingsRepository(Stores.settings(File(dir, Stores.SETTINGS_FILE), scope))
            assertEquals(Settings(), repository.settings.first())
            repository.update { it.copy(difficulty = Difficulty.LEVEL_6, soundOn = false) }
            assertEquals(Settings(Difficulty.LEVEL_6, soundOn = false), repository.settings.first())
        }
}
