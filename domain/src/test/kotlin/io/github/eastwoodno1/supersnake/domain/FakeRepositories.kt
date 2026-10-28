package io.github.eastwoodno1.supersnake.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeHighScoreRepository(
    initial: List<HighScore> = emptyList(),
) : HighScoreRepository {
    private val state = MutableStateFlow(initial)

    override val entries: Flow<List<HighScore>> = state

    val current: List<HighScore> get() = state.value

    override suspend fun update(transform: (List<HighScore>) -> List<HighScore>) {
        state.update(transform)
    }
}

class FakeSettingsRepository(
    initial: Settings = Settings(),
) : SettingsRepository {
    private val state = MutableStateFlow(initial)

    override val settings: Flow<Settings> = state

    override suspend fun update(transform: (Settings) -> Settings) {
        state.update(transform)
    }
}
