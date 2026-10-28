package io.github.eastwoodno1.supersnake

import android.content.Context
import io.github.eastwoodno1.supersnake.data.DataStoreHighScoreRepository
import io.github.eastwoodno1.supersnake.data.DataStoreSettingsRepository
import io.github.eastwoodno1.supersnake.data.Stores
import io.github.eastwoodno1.supersnake.domain.Clock
import io.github.eastwoodno1.supersnake.domain.HighScores
import io.github.eastwoodno1.supersnake.domain.KotlinRandomSource
import io.github.eastwoodno1.supersnake.domain.RandomSource
import io.github.eastwoodno1.supersnake.domain.RecordHighScore
import io.github.eastwoodno1.supersnake.domain.SettingsActions
import kotlinx.coroutines.CoroutineScope
import java.io.File
import kotlin.random.Random

class AppGraph(
    context: Context,
    scope: CoroutineScope,
) {
    val context: Context = context.applicationContext
    val clock: Clock = AndroidClock()
    val random: RandomSource = KotlinRandomSource(Random.Default)

    private val settingsRepository =
        DataStoreSettingsRepository(Stores.settings(File(context.filesDir, "datastore/" + Stores.SETTINGS_FILE), scope))
    private val highScoreRepository =
        DataStoreHighScoreRepository(Stores.highScores(File(context.filesDir, "datastore/" + Stores.HIGH_SCORES_FILE), scope))

    val settings: SettingsActions = SettingsActions(settingsRepository)
    val highScores: HighScores = HighScores(highScoreRepository)
    val recordHighScore: RecordHighScore = RecordHighScore(highScoreRepository, clock)
}
