package io.github.eastwoodno1.supersnake

import android.os.ParcelFileDescriptor
import android.os.SystemClock
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.requestFocus
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.eastwoodno1.supersnake.domain.Cell
import io.github.eastwoodno1.supersnake.domain.ControlScheme
import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.GameStatus
import io.github.eastwoodno1.supersnake.domain.Overlay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccessibilityTest {
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = UiTest.chain(composeRule)

    private val driver = GameDriver(composeRule)

    private fun withScale(
        value: String,
        block: () -> Unit,
    ) {
        val previous = shell("settings get global animator_duration_scale").trim()
        shell("settings put global animator_duration_scale $value")
        try {
            block()
        } finally {
            shell("settings put global animator_duration_scale $previous")
        }
    }

    private fun shell(command: String): String {
        val descriptor = InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(command)
        return ParcelFileDescriptor.AutoCloseInputStream(descriptor).use { String(it.readBytes()) }
    }

    private fun density() = composeRule.activity.resources.displayMetrics.density

    private fun assertTargets(where: String) = Targets.assertAtLeast48dp(composeRule, density(), where)

    @Test
    fun touchTargetsAtLeast48dp() {
        assertTargets("menu")
        composeRule.onNodeWithText("Settings").performClick()
        driver.awaitText("Haptics")
        assertTargets("settings")
        driver.back()
        driver.awaitText("Play")
        composeRule.onNodeWithText("High scores").performClick()
        driver.awaitText("No scores yet")
        driver.back()
        driver.awaitText("Play")
        runBlocking { UiTest.graph().settings.setControlScheme(ControlScheme.DPAD) }
        val model = driver.play()
        driver.awaitStatus(model, GameStatus.RUNNING)
        driver.awaitUntil { composeRule.onAllNodesWithContentDescription("Up").fetchSemanticsNodes().isNotEmpty() }
        assertTargets("hud and d-pad")
        composeRule.onNodeWithContentDescription("Pause").performClick()
        driver.awaitText("Resume")
        assertTargets("pause overlay")
        composeRule.onNodeWithText("Main menu").performClick()
        driver.awaitText("Play")
        runBlocking { UiTest.graph().settings.setDifficulty(Difficulty.LEVEL_9) }
        val over = driver.play()
        driver.awaitOverlay(over, Overlay.GAME_OVER)
        assertTargets("game over overlay")
    }

    @Test
    fun dpadButtonsExposeExactlyOneLabel() {
        runBlocking { UiTest.graph().settings.setControlScheme(ControlScheme.DPAD) }
        val model = driver.play()
        driver.awaitStatus(model, GameStatus.RUNNING)
        driver.awaitUntil { composeRule.onAllNodesWithContentDescription("Up").fetchSemanticsNodes().isNotEmpty() }
        listOf("Up", "Down", "Left", "Right").forEach { label ->
            val config = composeRule.onNodeWithContentDescription(label).fetchSemanticsNode().config
            assertEquals(listOf(label), config.getOrNull(SemanticsProperties.ContentDescription))
            assertNull("$label also exposes text", config.getOrNull(SemanticsProperties.Text))
        }
    }

    @Test
    fun keyboardReachesEveryMenuItem() {
        val items = listOf("Play", "High scores", "Settings")
        var focused: String? = null
        repeat(MAX_TABS_TO_ENTER) {
            if (focused == null) {
                shell("input keyevent KEYCODE_TAB")
                focused = awaitFocus(items, null)
            }
        }
        val first = checkNotNull(focused) { "Tab never focused a menu item" }
        val visited = mutableListOf(first)
        repeat(items.size - 1) {
            shell("input keyevent KEYCODE_TAB")
            visited += checkNotNull(awaitFocus(items, visited.last())) { "Tab left ${visited.last()} without reaching another item" }
        }
        assertEquals(items.toSet(), visited.toSet())
        visited.zipWithNext().forEach { (from, to) -> assertEquals(items[(items.indexOf(from) + 1) % items.size], to) }
    }

    private fun awaitFocus(
        items: List<String>,
        previous: String?,
    ): String? {
        val deadline = SystemClock.elapsedRealtime() + FOCUS_TIMEOUT_MILLIS
        while (SystemClock.elapsedRealtime() < deadline) {
            composeRule.waitForIdle()
            val now = items.firstOrNull { composeRule.onAllNodes(hasText(it) and isFocused()).fetchSemanticsNodes().isNotEmpty() }
            if (now != null && now != previous) return now
            Thread.sleep(GameDriver.POLL_MILLIS)
        }
        return null
    }

    private fun capture(orb: Cell): Int {
        var pixel = 0
        driver.awaitUntil(GameDriver.SETTLE_MILLIS) {
            runCatching { BoardPixels.capture(composeRule.activity).at(orb) }.onSuccess { pixel = it }.isSuccess
        }
        return pixel
    }

    private fun startCountdown(): GameViewModel {
        composeRule.onNodeWithText("Play").performClick()
        driver.awaitUntil(GameDriver.COLD_START_MILLIS) { composeRule.onAllNodesWithText("3").fetchSemanticsNodes().isNotEmpty() }
        return driver.viewModel()
    }

    @Test
    fun reduceMotionStopsAnimation() {
        withScale("0") {
            val model = startCountdown()
            assertEquals(GameStatus.COUNTDOWN, model.frame.value.state.status)
            val orb = checkNotNull(model.frame.value.state.orb)
            val first = capture(orb)
            Thread.sleep(SHOT_GAP_MILLIS)
            assertEquals(GameStatus.COUNTDOWN, model.frame.value.state.status)
            val second = capture(orb)
            assertEquals(first, second)
        }
    }

    @Test
    fun boardPixelsChangeWhenSomethingMoves() {
        withScale("1") {
            val model = startCountdown()
            driver.awaitStatus(model, GameStatus.RUNNING)
            val tail =
                model.frame.value.state.snake
                    .last()
            val before = capture(tail)
            driver.awaitUntil {
                model.frame.value.state.snake
                    .last() != tail
            }
            driver.awaitUntil(GameDriver.SETTLE_MILLIS) { capture(tail) != before }
        }
    }

    private companion object {
        const val SHOT_GAP_MILLIS = 600L
        const val FOCUS_TIMEOUT_MILLIS = 2_000L
        const val MAX_TABS_TO_ENTER = 4
    }
}
