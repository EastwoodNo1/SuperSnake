package io.github.eastwoodno1.supersnake

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.eastwoodno1.supersnake.domain.HighScore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HighScoresViewModel(
    graph: AppGraph,
) : ViewModel() {
    val scores: StateFlow<List<HighScore>> =
        graph.highScores.top.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), emptyList())

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
