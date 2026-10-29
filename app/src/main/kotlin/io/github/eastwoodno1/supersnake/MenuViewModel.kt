package io.github.eastwoodno1.supersnake

import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.eastwoodno1.supersnake.domain.Motion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MenuViewModel(
    graph: AppGraph,
) : ViewModel() {
    val best: StateFlow<Int> = graph.highScores.best.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), 0)

    val reducedMotion: Boolean =
        Motion.reduced(Settings.Global.getFloat(graph.context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f))

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
