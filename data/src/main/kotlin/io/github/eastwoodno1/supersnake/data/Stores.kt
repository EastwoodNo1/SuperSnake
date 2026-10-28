package io.github.eastwoodno1.supersnake.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import io.github.eastwoodno1.supersnake.domain.HighScore
import io.github.eastwoodno1.supersnake.domain.Settings
import kotlinx.coroutines.CoroutineScope
import java.io.File

object Stores {
    const val SETTINGS_FILE = "settings.txt"
    const val HIGH_SCORES_FILE = "high_scores.txt"

    fun settings(
        file: File,
        scope: CoroutineScope,
    ): DataStore<Settings> =
        DataStoreFactory.create(
            serializer = SettingsSerializer,
            scope = scope,
            produceFile = { file },
        )

    fun highScores(
        file: File,
        scope: CoroutineScope,
    ): DataStore<List<HighScore>> =
        DataStoreFactory.create(
            serializer = HighScoresSerializer,
            scope = scope,
            produceFile = { file },
        )
}
