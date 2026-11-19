package io.github.eastwoodno1.supersnake.domain

class SoundBank(
    private val generate: (Sound) -> ShortArray = { Synth.pcm(it) },
) {
    private val samples: Map<Sound, Lazy<ShortArray>> = Sound.entries.associateWith { sound -> lazy { generate(sound) } }

    fun pcm(sound: Sound): ShortArray = samples.getValue(sound).value
}
