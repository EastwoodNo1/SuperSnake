package io.github.eastwoodno1.supersnake.domain

enum class Phrase { COUNTDOWN, GO, PAUSED, ORB_MILESTONE, GAME_OVER, WON, NEW_HIGH_SCORE }

data class Line(
    val phrase: Phrase,
    val value: Int?,
)

data class Spoken(
    val lines: List<Line>,
    val sequence: Long,
) {
    companion object {
        fun next(
            previous: Spoken?,
            announcements: List<Announcement>,
        ): Spoken? =
            if (announcements.isEmpty()) {
                previous
            } else {
                Spoken(announcements.map(::line), (previous?.sequence ?: 0) + 1)
            }

        fun undelivered(
            spoken: Spoken?,
            delivered: Long,
        ): Spoken? = spoken?.takeIf { it.sequence > delivered }

        fun delivered(
            spoken: Spoken?,
            previous: Long,
        ): Long = maxOf(spoken?.sequence ?: previous, previous)

        private fun line(announcement: Announcement): Line =
            when (announcement) {
                is Announcement.Countdown -> Line(Phrase.COUNTDOWN, announcement.number)
                Announcement.Go -> Line(Phrase.GO, null)
                Announcement.Paused -> Line(Phrase.PAUSED, null)
                is Announcement.OrbMilestone -> Line(Phrase.ORB_MILESTONE, announcement.orbs)
                is Announcement.GameOver -> Line(Phrase.GAME_OVER, announcement.score)
                is Announcement.Won -> Line(Phrase.WON, announcement.score)
                is Announcement.NewHighScore -> Line(Phrase.NEW_HIGH_SCORE, announcement.score)
            }
    }
}
