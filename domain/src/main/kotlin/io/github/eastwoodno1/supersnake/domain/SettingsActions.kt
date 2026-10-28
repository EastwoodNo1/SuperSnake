package io.github.eastwoodno1.supersnake.domain

import kotlinx.coroutines.flow.Flow

class SettingsActions(
    private val repository: SettingsRepository,
) {
    val settings: Flow<Settings> = repository.settings

    suspend fun setDifficulty(value: Difficulty) = repository.update { it.copy(difficulty = value) }

    suspend fun setSound(on: Boolean) = repository.update { it.copy(soundOn = on) }

    suspend fun setHaptics(on: Boolean) = repository.update { it.copy(hapticsOn = on) }

    suspend fun setControlScheme(value: ControlScheme) = repository.update { it.copy(controlScheme = value) }
}
