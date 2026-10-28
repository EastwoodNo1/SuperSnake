package io.github.eastwoodno1.supersnake.domain

data class ScoreResult(
    val rank: Int?,
    val isNewBest: Boolean,
)

class RecordHighScore(
    private val repository: HighScoreRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(
        score: Int,
        difficulty: Difficulty,
    ): ScoreResult {
        val entry = HighScore(score, difficulty, clock.today())
        var result = ScoreResult(null, false)
        repository.update { table ->
            val placement = HighScoreTable.insert(table, entry)
            val previousBest = table.firstOrNull()?.score ?: 0
            result = ScoreResult(placement.rank, score > previousBest)
            placement.table
        }
        return result
    }
}
