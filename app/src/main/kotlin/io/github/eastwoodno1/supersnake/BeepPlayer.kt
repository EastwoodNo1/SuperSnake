package io.github.eastwoodno1.supersnake

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import io.github.eastwoodno1.supersnake.domain.Sound
import io.github.eastwoodno1.supersnake.domain.SoundBank
import io.github.eastwoodno1.supersnake.domain.Synth

class BeepPlayer(
    private val bank: SoundBank,
) {
    private val tracks = mutableMapOf<Sound, AudioTrack>()

    fun play(sound: Sound) {
        val track = tracks[sound] ?: build(sound)?.also { tracks[sound] = it } ?: return
        try {
            track.stop()
            track.setPlaybackHeadPosition(0)
            track.play()
        } catch (_: IllegalStateException) {
            tracks.remove(sound)?.release()
        }
    }

    fun release() {
        tracks.values.forEach(AudioTrack::release)
        tracks.clear()
    }

    private fun build(sound: Sound): AudioTrack? {
        val pcm = bank.pcm(sound)
        val track =
            try {
                AudioTrack
                    .Builder()
                    .setAudioAttributes(
                        AudioAttributes
                            .Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build(),
                    ).setAudioFormat(
                        AudioFormat
                            .Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(Synth.SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build(),
                    ).setBufferSizeInBytes(pcm.size * Short.SIZE_BYTES)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()
            } catch (_: UnsupportedOperationException) {
                return null
            } catch (_: IllegalStateException) {
                return null
            }
        track.write(pcm, 0, pcm.size)
        return track
    }
}
