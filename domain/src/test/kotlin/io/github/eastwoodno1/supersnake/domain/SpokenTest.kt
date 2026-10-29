package io.github.eastwoodno1.supersnake.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class SpokenTest {
    @Test
    fun c25SpokenLinesInOrder() {
        val spoken = Spoken.next(null, listOf(Announcement.GameOver(3), Announcement.NewHighScore(3)))
        assertEquals(listOf(Line(Phrase.GAME_OVER, 3), Line(Phrase.NEW_HIGH_SCORE, 3)), spoken?.lines)
        assertEquals(1L, spoken?.sequence)
    }

    @Test
    fun quietFrameKeepsPrevious() {
        val spoken = Spoken.next(null, listOf(Announcement.Go))
        assertSame(spoken, Spoken.next(spoken, emptyList()))
        assertNull(Spoken.next(null, emptyList()))
    }

    @Test
    fun sameTextIsANewAnnouncement() {
        val first = Spoken.next(null, listOf(Announcement.Paused))
        val second = Spoken.next(first, listOf(Announcement.Paused))
        assertEquals(1L, first?.sequence)
        assertEquals(2L, second?.sequence)
    }

    @Test
    fun everyAnnouncementHasALine() {
        fun line(announcement: Announcement) = Spoken.next(null, listOf(announcement))?.lines?.single()
        assertEquals(Line(Phrase.COUNTDOWN, 3), line(Announcement.Countdown(3)))
        assertEquals(Line(Phrase.GO, null), line(Announcement.Go))
        assertEquals(Line(Phrase.PAUSED, null), line(Announcement.Paused))
        assertEquals(Line(Phrase.ORB_MILESTONE, 10), line(Announcement.OrbMilestone(10)))
        assertEquals(Line(Phrase.GAME_OVER, 4), line(Announcement.GameOver(4)))
        assertEquals(Line(Phrase.WON, 5), line(Announcement.Won(5)))
        assertEquals(Line(Phrase.NEW_HIGH_SCORE, 6), line(Announcement.NewHighScore(6)))
    }

    @Test
    fun spokenNewerThanDeliveredIsUndelivered() {
        val spoken = Spoken.next(null, listOf(Announcement.Go))
        assertSame(spoken, Spoken.undelivered(spoken, 0L))
    }

    @Test
    fun spokenAlreadyDeliveredIsNotRepeated() {
        val spoken = Spoken.next(null, listOf(Announcement.Go))
        assertNull(Spoken.undelivered(spoken, 1L))
        assertNull(Spoken.undelivered(spoken, 2L))
        assertNull(Spoken.undelivered(null, 0L))
    }

    @Test
    fun deliveringAdvancesTheSequence() {
        val spoken = Spoken.next(null, listOf(Announcement.Go))
        assertEquals(1L, Spoken.delivered(spoken, 0L))
        assertEquals(3L, Spoken.delivered(null, 3L))
    }
}
