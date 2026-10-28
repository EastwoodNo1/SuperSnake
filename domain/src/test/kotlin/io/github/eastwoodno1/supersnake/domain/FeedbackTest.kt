package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FeedbackTest {
    private val state =
        GameState(
            snake = listOf(Cell(10, 10), Cell(9, 10), Cell(8, 10)),
            direction = Direction.RIGHT,
            pendingTurns = emptyList(),
            orb = Cell(11, 10),
            score = 0,
            orbsEaten = 0,
            difficulty = Difficulty.LEVEL_1,
            status = GameStatus.RUNNING,
        )

    private fun frame(
        events: List<GameEvent> = emptyList(),
        announcements: List<Announcement> = emptyList(),
    ) = GameFrame(state, state.snake, 0f, null, events, announcements, null)

    @Test
    fun b9CountdownBeeps() {
        val feedback = Feedback.of(frame(announcements = listOf(Announcement.Countdown(2))), Settings())
        assertEquals(listOf(Sound.COUNT), feedback.sounds)
        assertEquals(emptyList(), feedback.haptics)
    }

    @Test
    fun b9EatTicks() {
        val feedback = Feedback.of(frame(events = listOf(GameEvent.ORB_EATEN)), Settings())
        assertEquals(Feedback(listOf(Sound.EAT), listOf(Haptic.TICK)), feedback)
    }

    @Test
    fun b9CrashRejects() {
        val feedback = Feedback.of(frame(events = listOf(GameEvent.LOST)), Settings())
        assertEquals(Feedback(listOf(Sound.CRASH), listOf(Haptic.REJECT)), feedback)
    }

    @Test
    fun b9WinConfirms() {
        val feedback = Feedback.of(frame(events = listOf(GameEvent.WON)), Settings())
        assertEquals(Feedback(listOf(Sound.WIN), listOf(Haptic.CONFIRM)), feedback)
    }

    @Test
    fun b9GoAndEatInOneFrame() {
        val feedback =
            Feedback.of(
                frame(events = listOf(GameEvent.ORB_EATEN), announcements = listOf(Announcement.Go)),
                Settings(),
            )
        assertEquals(Feedback(listOf(Sound.GO, Sound.EAT), listOf(Haptic.TICK)), feedback)
    }

    @Test
    fun b9SilentAnnouncementsPlayNothing() {
        val announcements =
            listOf(
                Announcement.Paused,
                Announcement.OrbMilestone(10),
                Announcement.GameOver(5),
                Announcement.Won(5),
                Announcement.NewHighScore(5),
            )
        assertEquals(Feedback(emptyList(), emptyList()), Feedback.of(frame(announcements = announcements), Settings()))
    }

    @Test
    fun soundOffSilencesEverything() {
        val feedback =
            Feedback.of(
                frame(events = listOf(GameEvent.ORB_EATEN), announcements = listOf(Announcement.Go)),
                Settings(soundOn = false),
            )
        assertEquals(Feedback(emptyList(), listOf(Haptic.TICK)), feedback)
    }

    @Test
    fun hapticsOffStopsVibration() {
        val feedback =
            Feedback.of(
                frame(events = listOf(GameEvent.ORB_EATEN), announcements = listOf(Announcement.Go)),
                Settings(hapticsOn = false),
            )
        assertEquals(Feedback(listOf(Sound.GO, Sound.EAT), emptyList()), feedback)
    }

    @Test
    fun quietFrameGivesNothing() {
        assertEquals(Feedback(emptyList(), emptyList()), Feedback.of(frame(), Settings()))
    }
}
