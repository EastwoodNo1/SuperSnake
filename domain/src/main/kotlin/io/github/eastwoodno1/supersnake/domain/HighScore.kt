package io.github.eastwoodno1.supersnake.domain

import java.time.LocalDate

data class HighScore(
    val score: Int,
    val difficulty: Difficulty,
    val date: LocalDate,
)

data class Placement(
    val table: List<HighScore>,
    val rank: Int?,
)

object HighScoreTable {
    const val CAPACITY = 10

    fun insert(
        table: List<HighScore>,
        entry: HighScore,
    ): Placement {
        val result = normalize(table + entry)
        val index = result.indexOfFirst { it === entry }
        return Placement(result, if (index >= 0) index + 1 else null)
    }

    fun normalize(entries: List<HighScore>): List<HighScore> =
        entries
            .filter { it.score > 0 }
            .sortedByDescending { it.score }
            .take(CAPACITY)
}
