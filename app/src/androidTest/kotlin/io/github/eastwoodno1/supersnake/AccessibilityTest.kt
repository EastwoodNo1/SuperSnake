package io.github.eastwoodno1.supersnake

import android.os.ParcelFileDescriptor
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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
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
    private var previousScale = "1"

    @Before
    fun readScale() {
        previousScale = shell("settings get global animator_duration_scale").trim()
        shell("settings put global animator_duration_scale 0")
    }

    @After
    fun restoreScale() {
        shell("settings put global animator_duration_scale $previousScale")
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
        val visited = mutableListOf<String>()
        repeat(items.size + 3) {
            shell("input keyevent KEYCODE_TAB")
            composeRule.waitForIdle()
            visited += items.filter { composeRule.onAllNodes(hasText(it) and isFocused()).fetchSemanticsNodes().isNotEmpty() }
        }
        assertEquals(items.toSet(), visited.toSet())
        visited.zipWithNext().forEach { (from, to) -> assertEquals(items[(items.indexOf(from) + 1) % items.size], to) }
        items.forEach { item ->
            composeRule.onNodeWithText(item).requestFocus()
            composeRule.onNodeWithText(item).assertIsFocused()
        }
    }

    private fun capture(orb: Cell): Int {
        var pixel = 0
        driver.awaitUntil(GameDriver.SETTLE_MILLIS) {
            runCatching { BoardPixels.capture(composeRule.activity).at(orb) }.onSuccess { pixel = it }.isSuccess
        }
        return pixel
    }

    @Test
    fun reduceMotionStopsAnimation() {
        composeRule.onNodeWithText("Play").performClick()
        driver.awaitUntil(GameDriver.COLD_START_MILLIS) { composeRule.onAllNodesWithText("3").fetchSemanticsNodes().isNotEmpty() }
        val model = driver.viewModel()
        assertEquals(GameStatus.COUNTDOWN, model.frame.value.state.status)
        val orb = checkNotNull(model.frame.value.state.orb)
        val first = capture(orb)
        Thread.sleep(SHOT_GAP_MILLIS)
        assertEquals(GameStatus.COUNTDOWN, model.frame.value.state.status)
        val second = capture(orb)
        assertEquals(first, second)
    }

    private companion object {
        const val SHOT_GAP_MILLIS = 600L
    }
}
