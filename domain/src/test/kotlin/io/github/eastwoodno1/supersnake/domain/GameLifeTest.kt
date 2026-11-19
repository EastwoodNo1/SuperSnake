package io.github.eastwoodno1.supersnake.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class GameLifeTest {
    @Test
    fun liveWhileThePlayerIsInAnUnfinishedGame() {
        listOf(GameStatus.COUNTDOWN, GameStatus.RUNNING, GameStatus.PAUSED).forEach {
            assertEquals(true, GameLife.live(it, inGame = true), it.name)
        }
    }

    @Test
    fun notLiveOnceTheGameEnded() {
        listOf(GameStatus.LOST, GameStatus.WON).forEach {
            assertEquals(false, GameLife.live(it, inGame = true), it.name)
        }
    }

    @Test
    fun notLiveAfterTheGameWasLeftOrBeforeItStarted() {
        GameStatus.entries.forEach {
            assertEquals(false, GameLife.live(it, inGame = false), it.name)
        }
    }
}
