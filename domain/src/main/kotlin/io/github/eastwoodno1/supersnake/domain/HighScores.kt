package io.github.eastwoodno1.supersnake.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HighScores(
    repository: HighScoreRepository,
) {
    val top: Flow<List<HighScore>> = repository.entries

    val best: Flow<Int> = top.map { it.firstOrNull()?.score ?: 0 }
}
