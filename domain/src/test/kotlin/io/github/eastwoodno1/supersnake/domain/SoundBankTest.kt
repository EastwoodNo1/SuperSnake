package io.github.eastwoodno1.supersnake.domain

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertSame

class SoundBankTest {
    @Test
    fun nothingIsGeneratedUntilAskedFor() {
        var calls = 0
        SoundBank {
            calls++
            ShortArray(0)
        }
        assertEquals(0, calls)
    }

    @Test
    fun eachSoundIsGeneratedOnceNoMatterHowOftenItIsRead() {
        val calls = mutableListOf<Sound>()
        val bank =
            SoundBank { sound ->
                calls += sound
                ShortArray(1)
            }
        repeat(3) { Sound.entries.forEach { bank.pcm(it) } }
        assertEquals(Sound.entries.toList(), calls)
    }

    @Test
    fun repeatedReadsReturnTheSameArray() {
        val bank = SoundBank()
        assertSame(bank.pcm(Sound.EAT), bank.pcm(Sound.EAT))
    }

    @Test
    fun theSamplesAreTheSynthSamples() {
        val bank = SoundBank()
        Sound.entries.forEach { assertContentEquals(Synth.pcm(it), bank.pcm(it)) }
    }
}
