package io.github.eastwoodno1.supersnake

import android.os.SystemClock
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.test.ext.junit.rules.ActivityScenarioRule
import io.github.eastwoodno1.supersnake.domain.Cell
import io.github.eastwoodno1.supersnake.domain.GameStatus
import io.github.eastwoodno1.supersnake.domain.Overlay
import org.junit.Assert.assertTrue
import kotlin.math.abs

class GameDriver(
    private val rule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
) {
    fun viewModel(): GameViewModel {
        lateinit var model: GameViewModel
        rule.runOnUiThread {
            val graph = (rule.activity.application as SuperSnakeApp).graph
            val factory = viewModelFactory { initializer { GameViewModel(graph) } }
            model = ViewModelProvider(rule.activity, factory)[GameViewModel::class.java]
        }
        return model
    }

    fun awaitUntil(
        timeoutMillis: Long = TIMEOUT_MILLIS,
        condition: () -> Boolean,
    ) {
        val deadline = SystemClock.elapsedRealtime() + timeoutMillis
        while (!condition()) {
            assertTrue("timed out", SystemClock.elapsedRealtime() < deadline)
            rule.waitForIdle()
            Thread.sleep(POLL_MILLIS)
        }
    }

    fun awaitText(
        text: String,
        substring: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ) = awaitUntil(timeoutMillis) { rule.onAllNodesWithText(text, substring = substring).fetchSemanticsNodes().isNotEmpty() }

    fun play(): GameViewModel {
        UiTest.animationsOn()
        rule.onNodeWithText("Play").performClick()
        awaitUntil(COLD_START_MILLIS) { rule.onAllNodesWithTag("board").fetchSemanticsNodes().isNotEmpty() }
        return viewModel()
    }

    fun awaitStatus(
        model: GameViewModel,
        status: GameStatus,
    ) = awaitUntil { model.frame.value.state.status == status }

    fun awaitOverlay(
        model: GameViewModel,
        overlay: Overlay,
    ) = awaitUntil { model.overlay.value == overlay }

    fun back() = rule.runOnUiThread { rule.activity.onBackPressedDispatcher.onBackPressed() }

    fun awaitPixel(
        cell: () -> Cell,
        expected: Int,
    ) = awaitUntil(SETTLE_MILLIS) {
        val pixel = BoardPixels.capture(rule.activity).at(cell())
        listOf(16, 8, 0).all { shift -> abs(((pixel shr shift) and 0xFF) - ((expected shr shift) and 0xFF)) <= TOLERANCE }
    }

    companion object {
        const val TIMEOUT_MILLIS = 20_000L
        const val COLD_START_MILLIS = 60_000L
        const val SETTLE_MILLIS = 15_000L
        const val POLL_MILLIS = 10L
        const val TOLERANCE = 40
    }
}
