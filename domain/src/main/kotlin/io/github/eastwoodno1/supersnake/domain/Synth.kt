package io.github.eastwoodno1.supersnake.domain

object Synth {
    const val SAMPLE_RATE = 44_100
    private const val AMPLITUDE = 8192
    private const val RAMP_MILLIS = 5

    fun pcm(
        sound: Sound,
        sampleRate: Int = SAMPLE_RATE,
    ): ShortArray {
        if (sampleRate <= 0) return ShortArray(0)
        val ramp = maxOf(1, RAMP_MILLIS * sampleRate / 1000)
        return sound.notes.flatMap { note -> note(note, sampleRate, ramp) }.toShortArray()
    }

    private fun note(
        note: Note,
        sampleRate: Int,
        ramp: Int,
    ): List<Short> {
        val length = note.durationMillis * sampleRate / 1000
        return List(length) { i ->
            val level = minOf(i, length - 1 - i, ramp)
            val sign = if ((2L * i * note.frequencyHz / sampleRate) % 2 == 0L) 1 else -1
            (sign * AMPLITUDE * level / ramp).toShort()
        }
    }
}
