package io.github.eastwoodno1.supersnake

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.eastwoodno1.supersnake.domain.ControlScheme
import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.Settings
import io.github.eastwoodno1.supersnake.domain.SettingsActions
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    graph: AppGraph,
) : ViewModel() {
    private val actions: SettingsActions = graph.settings

    val settings: StateFlow<Settings> =
        actions.settings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), Settings())

    fun setDifficulty(value: Difficulty) = viewModelScope.launch { actions.setDifficulty(value) }

    fun setSound(on: Boolean) = viewModelScope.launch { actions.setSound(on) }

    fun setHaptics(on: Boolean) = viewModelScope.launch { actions.setHaptics(on) }

    fun setControlScheme(value: ControlScheme) = viewModelScope.launch { actions.setControlScheme(value) }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
