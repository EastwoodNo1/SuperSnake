package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SynthTest {
    @Test
    fun b8LengthMatchesNotes() {
        assertEquals(360, Synth.pcm(Sound.CRASH, 1000).size)
    }

    @Test
    fun b8StartsSilentAndPeaksAtAmplitude() {
        val pcm = Synth.pcm(Sound.GO)
        assertEquals(0, pcm[0].toInt())
        assertEquals(8192, pcm.max().toInt())
        assertEquals(-8192, pcm.min().toInt())
    }

    @Test
    fun b8SquareWaveFlipsAtFrequency() {
        val pcm = Synth.pcm(Sound.COUNT, 8800)
        val middle = pcm.slice(100..600)
        val signs = middle.map { it.toInt() > 0 }
        val flipPoints = (0 until signs.size - 1).filter { signs[it] != signs[it + 1] }
        assertTrue(flipPoints.size > 10)
        assertTrue(flipPoints.zipWithNext().all { (a, b) -> b - a == 10 })
    }

    @Test
    fun b8NoSampleRateNoSound() {
        assertTrue(Synth.pcm(Sound.EAT, 0).isEmpty())
        assertTrue(Synth.pcm(Sound.EAT, -1).isEmpty())
    }

    @Test
    fun b8EverySoundHasNotes() {
        Sound.entries.forEach { sound ->
            assertTrue(sound.notes.isNotEmpty())
            assertTrue(sound.notes.all { it.frequencyHz in 1..19_999 && it.durationMillis > 0 })
        }
    }

    @Test
    fun b8NotesAreConcatenatedWithTheirOwnEnvelope() {
        val pcm = Synth.pcm(Sound.WIN, 1000)
        assertEquals(400, pcm.size)
        assertEquals(0, pcm[100].toInt())
    }
}
