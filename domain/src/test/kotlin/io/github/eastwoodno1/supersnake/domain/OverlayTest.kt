package io.github.eastwoodno1.supersnake.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class OverlayTest {
    private fun frame(status: GameStatus): GameFrame {
        val clock = FakeClock(now = 0)
        val session = GameSession(Difficulty.LEVEL_1, clock, FixedRandom(0), RecordHighScore(FakeHighScoreRepository(), clock))
        val state = session.frame.state
        return session.frame.copy(state = state.copy(status = status))
    }

    @Test
    fun overlayForEachStatus() {
        assertEquals(Overlay.COUNTDOWN, Overlay.of(frame(GameStatus.COUNTDOWN)))
        assertEquals(Overlay.NONE, Overlay.of(frame(GameStatus.RUNNING)))
        assertEquals(Overlay.PAUSED, Overlay.of(frame(GameStatus.PAUSED)))
        assertEquals(Overlay.GAME_OVER, Overlay.of(frame(GameStatus.LOST)))
        assertEquals(Overlay.WON, Overlay.of(frame(GameStatus.WON)))
    }

    @Test
    fun b7BackPausesWhilePlaying() {
        assertEquals(BackAction.PAUSE, GameBack.action(GameStatus.COUNTDOWN))
        assertEquals(BackAction.PAUSE, GameBack.action(GameStatus.RUNNING))
    }

    @Test
    fun b7BackLeavesWhenStopped() {
        assertEquals(BackAction.LEAVE, GameBack.action(GameStatus.PAUSED))
        assertEquals(BackAction.LEAVE, GameBack.action(GameStatus.LOST))
        assertEquals(BackAction.LEAVE, GameBack.action(GameStatus.WON))
    }
}
