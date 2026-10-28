package io.github.eastwoodno1.supersnake.domain

import kotlinx.coroutines.flow.Flow

interface HighScoreRepository {
    val entries: Flow<List<HighScore>>

    suspend fun update(transform: (List<HighScore>) -> List<HighScore>)
}

interface SettingsRepository {
    val settings: Flow<Settings>

    suspend fun update(transform: (Settings) -> Settings)
}
