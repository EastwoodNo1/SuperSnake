package io.github.eastwoodno1.supersnake

import android.app.UiAutomation
import android.content.res.Configuration
import android.os.ParcelFileDescriptor
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
import androidx.test.platform.app.InstrumentationRegistry
import io.github.eastwoodno1.supersnake.domain.Cell
import io.github.eastwoodno1.supersnake.domain.Direction
import io.github.eastwoodno1.supersnake.domain.GameInput
import io.github.eastwoodno1.supersnake.domain.GameStatus
import io.github.eastwoodno1.supersnake.domain.NeonPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class GameScreenTest {
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain =
        RuleChain
            .outerRule(
                object : ExternalResource() {
                    override fun before() {
                        shell("settings put secure immersive_mode_confirmations confirmed")
                    }
                },
            ).around(composeRule)

    private fun shell(command: String) {
        val descriptor = InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(command)
        ParcelFileDescriptor.AutoCloseInputStream(descriptor).use { it.readBytes() }
    }

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
        pumping: Boolean = true,
        condition: () -> Boolean,
    ) {
        val deadline = SystemClock.elapsedRealtime() + timeoutMillis
        while (!condition()) {
            assertTrue("timed out", SystemClock.elapsedRealtime() < deadline)
            if (pumping) pump(10) else Thread.sleep(10)
        }
    }

    private fun pump(millis: Long) {
        composeRule.waitForIdle()
        Thread.sleep(millis)
    }

    private fun play(): GameViewModel {
        composeRule.onNodeWithText("Play").performClick()
        awaitUntil(COLD_START_MILLIS) { composeRule.onAllNodesWithTag("board").fetchSemanticsNodes().isNotEmpty() }
        return viewModel()
    }

    private fun awaitGo(model: GameViewModel) = awaitUntil { model.frame.value.state.status == GameStatus.RUNNING }

    private fun awaitPixel(
        cell: () -> Cell,
        expected: Int,
    ): Int {
        var pixel = 0
        awaitUntil(SETTLE_MILLIS) {
            val shot = BoardPixels.capture(composeRule.activity)
            pixel = shot.at(cell())
            closeTo(pixel, expected)
        }
        return pixel
    }

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
        awaitUntil(COLD_START_MILLIS) { composeRule.onAllNodesWithText("3").fetchSemanticsNodes().isNotEmpty() }
    }

    @Test
    fun boardDrawsTheSnake() {
        val model = play()
        awaitGo(model)
        composeRule.runOnUiThread { model.pause() }
        awaitPixel({ Cell(10, 10) }, NeonPalette.SNAKE)
        awaitPixel({ Cell(0, 0) }, NeonPalette.BACKGROUND)
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
        val snake = model.frame.value.state.snake
        assertEquals(snake[1].x, snake[0].x)
        assertEquals(snake[1].y - 1, snake[0].y)
        awaitPixel({ snake[0] }, NeonPalette.SNAKE)
    }

    @Test
    fun rotationPausesAndKeepsTheGame() {
        val model = play()
        awaitGo(model)
        val before = model.frame.value.state
        val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
        val landscape = composeRule.activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        try {
            automation.setRotation(if (landscape) UiAutomation.ROTATION_FREEZE_0 else UiAutomation.ROTATION_FREEZE_90)
            awaitUntil(pumping = false) { model.frame.value.state.status == GameStatus.PAUSED }
        } finally {
            automation.setRotation(UiAutomation.ROTATION_UNFREEZE)
        }
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
        const val COLD_START_MILLIS = 60_000L
        const val SETTLE_MILLIS = 15_000L
        const val WINDOW_MILLIS = 2_000L
        const val EXPECTED_MOVES = 8
    }
}
