package io.github.eastwoodno1.supersnake

import android.content.pm.ActivityInfo
import android.os.SystemClock
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.center
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.eastwoodno1.supersnake.domain.Cell
import io.github.eastwoodno1.supersnake.domain.Direction
import io.github.eastwoodno1.supersnake.domain.GameInput
import io.github.eastwoodno1.supersnake.domain.GameStatus
import io.github.eastwoodno1.supersnake.domain.NeonPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class GameScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun viewModel(): GameViewModel {
        lateinit var model: GameViewModel
        composeRule.runOnUiThread {
            val activity = composeRule.activity
            val graph = (activity.application as SuperSnakeApp).graph
            val factory = viewModelFactory { initializer { GameViewModel(graph) } }
            model = ViewModelProvider(activity, factory)[GameViewModel::class.java]
        }
        return model
    }

    private fun awaitUntil(
        timeoutMillis: Long = 10_000,
        condition: () -> Boolean,
    ) {
        val deadline = SystemClock.elapsedRealtime() + timeoutMillis
        while (!condition()) {
            assertTrue("timed out", SystemClock.elapsedRealtime() < deadline)
            pump(10)
        }
    }

    private fun pump(millis: Long) {
        composeRule.waitForIdle()
        Thread.sleep(millis)
    }

    private fun pumpFor(millis: Long) {
        val deadline = SystemClock.elapsedRealtime() + millis
        while (SystemClock.elapsedRealtime() < deadline) pump(10)
    }

    private fun play(): GameViewModel {
        composeRule.onNodeWithText("Play").performClick()
        awaitUntil { composeRule.onAllNodesWithTag("board").fetchSemanticsNodes().isNotEmpty() }
        return viewModel()
    }

    private fun awaitGo(model: GameViewModel) = awaitUntil { model.frame.value.state.status == GameStatus.RUNNING }

    private fun closeTo(
        actual: Int,
        expected: Int,
    ): Boolean =
        listOf(16, 8, 0).all { shift ->
            abs(((actual shr shift) and 0xFF) - ((expected shr shift) and 0xFF)) <= TOLERANCE
        }

    @Test
    fun c13PlayShowsCountdown() {
        composeRule.onNodeWithText("Play").performClick()
        awaitUntil(2_000) { composeRule.onAllNodesWithText("3").fetchSemanticsNodes().isNotEmpty() }
        composeRule.onNodeWithText("3").assertExists()
    }

    @Test
    fun boardDrawsTheSnake() {
        val model = play()
        pumpFor(3_500)
        composeRule.runOnUiThread { model.pause() }
        pumpFor(500)
        val pixel = BoardPixels.capture(composeRule.activity).at(Cell(10, 10))
        assertTrue("pixel ${Integer.toHexString(pixel)}", closeTo(pixel, NeonPalette.SNAKE))
    }

    @Test
    fun c24SwipeUpTurns() {
        val model = play()
        awaitGo(model)
        composeRule.onNodeWithTag("board").performTouchInput {
            val start = center
            down(start)
            moveTo(start + Offset(0f, -48.dp.toPx()))
            up()
        }
        awaitUntil { model.frame.value.state.direction == Direction.UP }
        composeRule.runOnUiThread { model.pause() }
        pumpFor(500)
        val snake = model.frame.value.state.snake
        assertEquals(snake[1].x, snake[0].x)
        assertEquals(snake[1].y - 1, snake[0].y)
        val pixel = BoardPixels.capture(composeRule.activity).at(snake[0])
        assertTrue("pixel ${Integer.toHexString(pixel)}", closeTo(pixel, NeonPalette.SNAKE))
    }

    @Test
    fun rotationPausesAndKeepsTheGame() {
        val model = play()
        awaitGo(model)
        val before = model.frame.value.state
        composeRule.runOnUiThread { composeRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE }
        awaitUntil { model.frame.value.state.status == GameStatus.PAUSED }
        val after = model.frame.value.state
        assertEquals(before.score, after.score)
        assertEquals(before.snake.size, after.snake.size)
        assertEquals(before.orb, after.orb)
    }

    @Test
    fun backgroundPausesTheGame() {
        val model = play()
        awaitGo(model)
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        assertEquals(GameStatus.PAUSED, model.frame.value.state.status)
    }

    @Test
    fun oneLoopAfterRecreate() {
        val model = play()
        composeRule.activityRule.scenario.recreate()
        awaitUntil { model.frame.value.state.status == GameStatus.PAUSED }
        composeRule.runOnUiThread { model.input(GameInput.TogglePause) }
        awaitGo(model)
        var moves = 0
        var head =
            model.frame.value.state.snake
                .first()
        val end = SystemClock.elapsedRealtime() + WINDOW_MILLIS
        while (SystemClock.elapsedRealtime() < end) {
            val now =
                model.frame.value.state.snake
                    .first()
            if (now != head) {
                moves++
                head = now
            }
            pump(5)
        }
        assertTrue("moves $moves", moves in EXPECTED_MOVES - 1..EXPECTED_MOVES + 1)
    }

    private companion object {
        const val TOLERANCE = 40
        const val WINDOW_MILLIS = 2_000L
        const val EXPECTED_MOVES = 8
    }
}
