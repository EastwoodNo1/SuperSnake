package io.github.eastwoodno1.supersnake

import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.eastwoodno1.supersnake.board.SceneHolder
import io.github.eastwoodno1.supersnake.domain.BoardScene
import io.github.eastwoodno1.supersnake.domain.Burst
import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.GameFrame
import io.github.eastwoodno1.supersnake.domain.GameInput
import io.github.eastwoodno1.supersnake.domain.GameSession
import io.github.eastwoodno1.supersnake.domain.Motion
import io.github.eastwoodno1.supersnake.domain.SwipeControl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

class GameViewModel(
    private val graph: AppGraph,
) : ViewModel() {
    private val sessionClock = HoldableClock(graph.clock)

    val scene = SceneHolder().also { it.onFirstFrame = sessionClock::release }

    private var session = GameSession(Difficulty.LEVEL_1, sessionClock, graph.random, graph.recordHighScore)
    private var burst: Burst? = null
    private var reduceMotion = false
    private var swipe = SwipeControl(DEFAULT_THRESHOLD_PX)
    private val state = MutableStateFlow(session.frame)

    val frame: StateFlow<GameFrame> = state

    suspend fun newGame() {
        val difficulty =
            graph.settings.settings
                .first()
                .difficulty
        val scale = Settings.Global.getFloat(graph.context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
        reduceMotion = Motion.reduced(scale)
        burst = null
        sessionClock.hold()
        session = GameSession(difficulty, sessionClock, graph.random, graph.recordHighScore)
        consume(session.frame)
    }

    suspend fun advance() = consume(session.advance())

    fun input(input: GameInput) = consume(session.input(input))

    fun pause() = consume(session.pause())

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
        val now = graph.clock.nowMillis()
        burst = Burst.next(burst, frame, now)
        scene.scene = BoardScene.of(frame, burst, now, reduceMotion)
        state.value = frame
    }

    companion object {
        private const val DEFAULT_THRESHOLD_PX = 64f

        val Factory =
            viewModelFactory {
                initializer { GameViewModel((checkNotNull(this[APPLICATION_KEY]) as SuperSnakeApp).graph) }
            }
    }
}
