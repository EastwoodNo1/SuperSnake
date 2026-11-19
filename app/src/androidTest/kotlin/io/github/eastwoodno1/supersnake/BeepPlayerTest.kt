package io.github.eastwoodno1.supersnake

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.eastwoodno1.supersnake.domain.Sound
import io.github.eastwoodno1.supersnake.domain.SoundBank
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BeepPlayerTest {
    private val bank = SoundBank()

    @Test
    fun abandonedPlayersHoldNoAudioTracks() {
        repeat(ABANDONED_PLAYERS) { BeepPlayer(bank) }
    }

    @Test
    fun exhaustedAudioResourcesNeverThrow() {
        val players = List(ABANDONED_PLAYERS) { BeepPlayer(bank) }
        try {
            players.forEach { player -> Sound.entries.forEach(player::play) }
        } finally {
            players.forEach(BeepPlayer::release)
        }
    }

    private companion object {
        const val ABANDONED_PLAYERS = 60
    }
}
