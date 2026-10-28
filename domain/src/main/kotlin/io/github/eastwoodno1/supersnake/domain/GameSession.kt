package io.github.eastwoodno1.supersnake.domain

import java.io.IOException

data class GameFrame(
    val state: GameState,
    val previousSnake: List<Cell>,
    val progress: Float,
    val countdown: Int?,
    val events: List<GameEvent>,
    val announcements: List<Announcement>,
    val result: ScoreResult?,
)

class GameSession internal constructor(
    initial: GameState,
    private val clock: Clock,
    private val random: RandomSource,
    private val recordHighScore: RecordHighScore,
) {
    constructor(
        difficulty: Difficulty,
        clock: Clock,
        random: RandomSource,
        recordHighScore: RecordHighScore,
    ) : this(GameRules.newGame(difficulty, random), clock, random, recordHighScore)

    private var countdownStartMillis = clock.nowMillis()
    private var announcedNumber = COUNTDOWN_FROM
    private var timer = TickTimer(initial.difficulty.tickMillis, clock.nowMillis())

    var frame: GameFrame =
        GameFrame(
            state = initial,
            previousSnake = initial.snake,
            progress = 0f,
            countdown = if (initial.status == GameStatus.COUNTDOWN) COUNTDOWN_FROM else null,
            events = emptyList(),
            announcements = if (initial.status == GameStatus.COUNTDOWN) listOf(Announcement.Countdown(COUNTDOWN_FROM)) else emptyList(),
            result = null,
        )
        private set

    fun input(input: GameInput): GameFrame {
        frame =
            when {
                input is GameInput.Turn -> quiet(Turns.enqueue(frame.state, input.direction))
                frame.state.status == GameStatus.PAUSED -> resumed()
                else -> paused()
            }
        return frame
    }

    fun pause(): GameFrame {
        frame = paused()
        return frame
    }

    suspend fun advance(): GameFrame {
        val now = clock.nowMillis()
        when (frame.state.status) {
            GameStatus.COUNTDOWN -> frame = counting(now)
            GameStatus.RUNNING -> stepping(now)
            GameStatus.PAUSED, GameStatus.LOST, GameStatus.WON -> frame = quiet(frame.state)
        }
        return frame
    }

    private fun quiet(state: GameState): GameFrame = frame.copy(state = state, events = emptyList(), announcements = emptyList())

    private fun paused(): GameFrame {
        val status = frame.state.status
        if (status != GameStatus.COUNTDOWN && status != GameStatus.RUNNING) return quiet(frame.state)
        val state = GameRules.pause(frame.state)
        return quiet(state).copy(previousSnake = state.snake, progress = 0f, countdown = null, announcements = listOf(Announcement.Paused))
    }

    private fun resumed(): GameFrame {
        countdownStartMillis = clock.nowMillis()
        announcedNumber = COUNTDOWN_FROM
        return quiet(GameRules.resume(frame.state)).copy(
            countdown = COUNTDOWN_FROM,
            announcements = listOf(Announcement.Countdown(COUNTDOWN_FROM)),
        )
    }

    private fun counting(now: Long): GameFrame {
        val elapsed = (now - countdownStartMillis).coerceAtLeast(0)
        if (elapsed >= COUNTDOWN_FROM * COUNTDOWN_STEP_MILLIS) {
            timer = TickTimer(timer.intervalMillis, now)
            return quiet(GameRules.go(frame.state)).copy(countdown = null, announcements = listOf(Announcement.Go))
        }
        val number = COUNTDOWN_FROM - (elapsed / COUNTDOWN_STEP_MILLIS).toInt()
        val announcements = if (number == announcedNumber) emptyList() else listOf(Announcement.Countdown(number))
        announcedNumber = number
        return quiet(frame.state).copy(countdown = number, announcements = announcements)
    }

    private suspend fun stepping(now: Long) {
        val advance = timer.advance(now)
        timer = advance.timer
        if (!advance.ticked) {
            frame = quiet(frame.state).copy(progress = timer.progress(now))
            return
        }
        val result = GameRules.tick(frame.state, random)
        val state = result.state
        val ended = state.status == GameStatus.LOST || state.status == GameStatus.WON
        val announcements = announcementsFor(result)
        val next =
            GameFrame(
                state = state,
                previousSnake = if (ended) state.snake else frame.state.snake,
                progress = if (ended) 1f else timer.progress(now),
                countdown = null,
                events = result.events,
                announcements = announcements,
                result = null,
            )
        frame = next
        if (ended) frame = saved(next)
    }

    private suspend fun saved(ended: GameFrame): GameFrame {
        val state = ended.state
        val score =
            try {
                recordHighScore(state.score, state.difficulty)
            } catch (_: IOException) {
                ScoreResult(null, false)
            }
        val extra = if (score.isNewBest) listOf(Announcement.NewHighScore(state.score)) else emptyList()
        return ended.copy(announcements = ended.announcements + extra, result = score)
    }

    private fun announcementsFor(result: TickResult): List<Announcement> {
        val state = result.state
        val announcements = mutableListOf<Announcement>()
        result.events.forEach { event ->
            when (event) {
                GameEvent.ORB_EATEN -> {
                    if (state.orbsEaten % MILESTONE_ORBS == 0) announcements += Announcement.OrbMilestone(state.orbsEaten)
                }

                GameEvent.LOST -> {
                    announcements += Announcement.GameOver(state.score)
                }

                GameEvent.WON -> {
                    announcements += Announcement.Won(state.score)
                }
            }
        }
        return announcements
    }

    companion object {
        const val COUNTDOWN_FROM = 3
        const val COUNTDOWN_STEP_MILLIS = 1000L
        private const val MILESTONE_ORBS = 10
    }
}
