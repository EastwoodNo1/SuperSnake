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
        val notes = sound.notes
        val lengths = IntArray(notes.size) { notes[it].durationMillis * sampleRate / 1000 }
        val pcm = ShortArray(lengths.sum())
        var offset = 0
        notes.forEachIndexed { index, note ->
            fill(pcm, offset, lengths[index], note, sampleRate, ramp)
            offset += lengths[index]
        }
        return pcm
    }

    private fun fill(
        pcm: ShortArray,
        offset: Int,
        length: Int,
        note: Note,
        sampleRate: Int,
        ramp: Int,
    ) {
        for (i in 0 until length) {
            val level = minOf(i, length - 1 - i, ramp)
            val sign = if ((2L * i * note.frequencyHz / sampleRate) % 2 == 0L) 1 else -1
            pcm[offset + i] = (sign * AMPLITUDE * level / ramp).toShort()
        }
    }
}
