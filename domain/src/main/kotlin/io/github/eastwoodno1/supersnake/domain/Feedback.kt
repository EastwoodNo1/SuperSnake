package io.github.eastwoodno1.supersnake.domain

data class Note(
    val frequencyHz: Int,
    val durationMillis: Int,
)

enum class Sound(
    val notes: List<Note>,
) {
    COUNT(listOf(Note(440, 80))),
    GO(listOf(Note(880, 150))),
    EAT(listOf(Note(1320, 40))),
    CRASH(listOf(Note(220, 120), Note(110, 240))),
    WIN(listOf(Note(523, 100), Note(659, 100), Note(784, 100), Note(1047, 100))),
}

enum class Haptic { TICK, REJECT, CONFIRM }

data class Feedback(
    val sounds: List<Sound>,
    val haptics: List<Haptic>,
) {
    companion object {
        fun of(
            frame: GameFrame,
            settings: Settings,
        ): Feedback {
            val sounds = frame.announcements.mapNotNull(::soundOf) + frame.events.map(::soundOf)
            val haptics = frame.events.map(::hapticOf)
            return Feedback(
                sounds = if (settings.soundOn) sounds else emptyList(),
                haptics = if (settings.hapticsOn) haptics else emptyList(),
            )
        }

        private fun soundOf(announcement: Announcement): Sound? =
            when (announcement) {
                is Announcement.Countdown -> Sound.COUNT
                Announcement.Go -> Sound.GO
                else -> null
            }

        private fun soundOf(event: GameEvent): Sound =
            when (event) {
                GameEvent.ORB_EATEN -> Sound.EAT
                GameEvent.LOST -> Sound.CRASH
                GameEvent.WON -> Sound.WIN
            }

        private fun hapticOf(event: GameEvent): Haptic =
            when (event) {
                GameEvent.ORB_EATEN -> Haptic.TICK
                GameEvent.LOST -> Haptic.REJECT
                GameEvent.WON -> Haptic.CONFIRM
            }
    }
}
