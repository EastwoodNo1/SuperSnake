package io.github.eastwoodno1.supersnake.data

import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.HighScore
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import kotlin.test.assertEquals

class HighScoresSerializerTest {
    private suspend fun read(bytes: ByteArray) = HighScoresSerializer.readFrom(bytes.inputStream())

    private suspend fun read(text: String) = read(text.toByteArray())

    @Test
    fun highScoresRoundTrip() =
        runTest {
            val entries =
                listOf(
                    HighScore(90, Difficulty.LEVEL_9, LocalDate.of(2026, 10, 28)),
                    HighScore(40, Difficulty.ASSIST, LocalDate.of(2026, 1, 2)),
                    HighScore(12, Difficulty.LEVEL_5, LocalDate.of(2025, 12, 31)),
                )
            val out = ByteArrayOutputStream()
            HighScoresSerializer.writeTo(entries, out)
            assertEquals(entries, read(out.toByteArray()))
        }

    @Test
    fun highScoresAreWrittenAsPipeLines() =
        runTest {
            val out = ByteArrayOutputStream()
            HighScoresSerializer.writeTo(listOf(HighScore(30, Difficulty.LEVEL_2, LocalDate.of(2026, 10, 28))), out)
            assertEquals("30|LEVEL_2|2026-10-28\n", out.toString(Charsets.UTF_8))
        }

    @Test
    fun highScoresDropBadLines() =
        runTest {
            val text = "30|LEVEL_2|2026-10-28\nnope\n5|LEVEL_X|2026-10-28\n7|ASSIST|2026-13-40\n|\n-4|LEVEL_1|2026-10-28\n"
            assertEquals(listOf(HighScore(30, Difficulty.LEVEL_2, LocalDate.of(2026, 10, 28))), read(text))
        }

    @Test
    fun highScoresDropNonNumericScores() =
        runTest {
            assertEquals(emptyList(), read("many|LEVEL_1|2026-10-28\n1|LEVEL_1|2026-10-28|extra\n"))
        }

    @Test
    fun highScoresReadSortedAndCapped() =
        runTest {
            val text = (1..12).joinToString("\n") { "$it|LEVEL_1|2026-10-28" }
            assertEquals((12 downTo 3).toList(), read(text).map { it.score })
        }

    @Test
    fun randomBytesReadAsEmpty() =
        runTest {
            assertEquals(emptyList(), read(byteArrayOf(0, -1, 19, -128, 10, 61, -2)))
        }

    @Test
    fun defaultValueIsEmpty() {
        assertEquals(emptyList(), HighScoresSerializer.defaultValue)
    }
}
