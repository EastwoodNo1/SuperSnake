package io.github.eastwoodno1.supersnake

import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.eastwoodno1.supersnake.board.SceneHolder
import io.github.eastwoodno1.supersnake.domain.BackAction
import io.github.eastwoodno1.supersnake.domain.BoardScene
import io.github.eastwoodno1.supersnake.domain.Burst
import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.Feedback
import io.github.eastwoodno1.supersnake.domain.GameBack
import io.github.eastwoodno1.supersnake.domain.GameFrame
import io.github.eastwoodno1.supersnake.domain.GameInput
import io.github.eastwoodno1.supersnake.domain.GameSession
import io.github.eastwoodno1.supersnake.domain.HoldableClock
import io.github.eastwoodno1.supersnake.domain.Motion
import io.github.eastwoodno1.supersnake.domain.Overlay
import io.github.eastwoodno1.supersnake.domain.Spoken
import io.github.eastwoodno1.supersnake.domain.SwipeControl
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import io.github.eastwoodno1.supersnake.domain.Settings as AppSettings

class GameViewModel(
    private val graph: AppGraph,
) : ViewModel() {
    private val sessionClock = HoldableClock(graph.clock)

    val scene = SceneHolder().also { it.onFrame = { sessionClock.release() } }

    private var session = GameSession(Difficulty.LEVEL_1, sessionClock, graph.random, graph.recordHighScore)
    var live = false
        private set

    private var burst: Burst? = null
    private var pendingFeedback: Job = Job()
    private var reduceMotion = false
    private var swipe = SwipeControl(DEFAULT_THRESHOLD_PX)
    private var deliveredSequence = 0L
    private val state = MutableStateFlow(session.frame)

    private val overlayState = MutableStateFlow(Overlay.of(session.frame))
    private val spokenState = MutableStateFlow<Spoken?>(null)
    private val feedbackEvents = MutableSharedFlow<Feedback>(extraBufferCapacity = FEEDBACK_BUFFER)

    val frame: StateFlow<GameFrame> = state
    val overlay: StateFlow<Overlay> = overlayState
    val spoken: StateFlow<Spoken?> = spokenState
    val feedback: SharedFlow<Feedback> = feedbackEvents
    val settings: StateFlow<AppSettings> = graph.settings.settings.stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())
    val best: StateFlow<Int> = graph.highScores.best.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), 0)

    suspend fun newGame() {
        val difficulty =
            graph.settings.settings
                .first()
                .difficulty
        val scale = Settings.Global.getFloat(graph.context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
        reduceMotion = Motion.reduced(scale)
        burst = null
        live = true
        sessionClock.hold()
        session = GameSession(difficulty, sessionClock, graph.random, graph.recordHighScore)
        val first = session.frame
        render(first)
        val feedback = Feedback.of(first, settings.value)
        pendingFeedback.cancel()
        pendingFeedback =
            viewModelScope.launch {
                feedbackEvents.subscriptionCount.first { it > 0 }
                feedbackEvents.emit(feedback)
            }
    }

    suspend fun advance() = consume(session.advance())

    fun input(input: GameInput) = consume(session.input(input))

    fun pause() = consume(session.pause())

    fun back(): BackAction {
        val action = GameBack.action(state.value.state.status)
        pause()
        return action
    }

    fun key(code: Int): Boolean = KEY_BINDINGS.press(code, ::input)

    fun undelivered(spoken: Spoken?): Spoken? = Spoken.undelivered(spoken, deliveredSequence)

    fun delivered(spoken: Spoken?) {
        deliveredSequence = Spoken.delivered(spoken, deliveredSequence)
    }

    fun swipeThreshold(px: Float) {
        swipe = SwipeControl(px)
    }

    fun swipeDown(
        id: Int,
        x: Float,
        y: Float,
    ) = swipe.down(id, x, y)

    fun swipeMove(
        id: Int,
        x: Float,
        y: Float,
    ) = swipe.move(id, x, y).forEach(::input)

    fun swipeUp(id: Int) = swipe.up(id)

    private fun consume(frame: GameFrame) {
        render(frame)
        feedbackEvents.tryEmit(Feedback.of(frame, settings.value))
    }

    private fun render(frame: GameFrame) {
        val now = graph.clock.nowMillis()
        burst = Burst.next(burst, frame, now)
        scene.scene = BoardScene.of(frame, burst, now, reduceMotion)
        spokenState.value = Spoken.next(spokenState.value, frame.announcements)
        overlayState.value = Overlay.of(frame)
        state.value = frame
    }

    companion object {
        private const val DEFAULT_THRESHOLD_PX = 64f
        private const val FEEDBACK_BUFFER = 64
        private const val STOP_TIMEOUT_MILLIS = 5_000L

        val Factory =
            viewModelFactory {
                initializer { GameViewModel((checkNotNull(this[APPLICATION_KEY]) as SuperSnakeApp).graph) }
            }
    }
}
