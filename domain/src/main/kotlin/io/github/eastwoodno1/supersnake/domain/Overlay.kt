package io.github.eastwoodno1.supersnake.domain

enum class Overlay {
    NONE,
    COUNTDOWN,
    PAUSED,
    GAME_OVER,
    WON,
    ;

    companion object {
        fun of(frame: GameFrame): Overlay =
            when (frame.state.status) {
                GameStatus.COUNTDOWN -> COUNTDOWN
                GameStatus.RUNNING -> NONE
                GameStatus.PAUSED -> PAUSED
                GameStatus.LOST -> GAME_OVER
                GameStatus.WON -> WON
            }
    }
}

enum class BackAction { PAUSE, LEAVE }

object GameBack {
    fun action(status: GameStatus): BackAction =
        when (status) {
            GameStatus.COUNTDOWN, GameStatus.RUNNING -> BackAction.PAUSE
            GameStatus.PAUSED, GameStatus.LOST, GameStatus.WON -> BackAction.LEAVE
        }
}
