package io.github.eastwoodno1.supersnake.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class HighScores(
    repository: HighScoreRepository,
) {
    val top: Flow<List<HighScore>> = repository.entries.catch<List<HighScore>> { if (it is IOException) emit(emptyList()) else throw it }

    val best: Flow<Int> = top.map { it.firstOrNull()?.score ?: 0 }
}
