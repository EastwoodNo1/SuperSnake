package io.github.eastwoodno1.supersnake.data

import androidx.datastore.core.Serializer
import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.HighScore
import io.github.eastwoodno1.supersnake.domain.HighScoreTable
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.DateTimeParseException

object HighScoresSerializer : Serializer<List<HighScore>> {
    override val defaultValue: List<HighScore> = emptyList()

    override suspend fun readFrom(input: InputStream): List<HighScore> =
        HighScoreTable.normalize(
            String(input.readBytes(), Charsets.UTF_8)
                .lines()
                .mapNotNull(::parse),
        )

    override suspend fun writeTo(
        t: List<HighScore>,
        output: OutputStream,
    ) {
        val text = t.joinToString("") { "${it.score}|${it.difficulty.name}|${it.date}\n" }
        output.write(text.toByteArray(Charsets.UTF_8))
    }

    private fun parse(line: String): HighScore? {
        val parts = line.split('|')
        if (parts.size != 3) return null
        val score = parts[0].toIntOrNull() ?: return null
        val difficulty = Difficulty.entries.firstOrNull { it.name == parts[1] } ?: return null
        val date =
            try {
                LocalDate.parse(parts[2])
            } catch (_: DateTimeParseException) {
                return null
            }
        return HighScore(score, difficulty, date)
    }
}
