package io.github.eastwoodno1.supersnake

import android.os.SystemClock
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.Overlay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FullScreenTest {
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = UiTest.chain(composeRule)

    private val driver = GameDriver(composeRule)

    private val scenario get() = composeRule.activityRule.scenario

    private fun assertBars(
        visible: Boolean,
        where: String,
    ) {
        val expected = if (visible) "both bars visible" else "both bars hidden"
        driver.awaitUntil(BARS_SETTLE_MILLIS, { "$where: expected $expected, found ${composeRule.activity.barsSummary()}" }) {
            if (visible) composeRule.activity.allBarsVisible() else composeRule.activity.allBarsHidden()
        }
    }

    private fun showBars() =
        composeRule.runOnUiThread {
            val window = composeRule.activity.window
            WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.systemBars())
        }

    private fun shell(command: String) {
        InstrumentationRegistry
            .getInstrumentation()
            .uiAutomation
            .executeShellCommand(command)
            .close()
    }

    @Test
    fun menuHighScoresAndSettingsShowBothBars() {
        driver.awaitText("Play")
        assertBars(true, "menu")
        composeRule.onNodeWithText("Settings").performScrollTo().performClick()
        driver.awaitText("Sound")
        assertBars(true, "settings")
        driver.back()
        driver.awaitText("Play")
        composeRule.onNodeWithText("High scores").performScrollTo().performClick()
        driver.awaitText("No scores yet")
        assertBars(true, "high scores")
    }

    @Test
    fun leavingTheGameShowsTheBarsAgain() {
        driver.awaitText("Play")
        assertBars(true, "menu")
        driver.play()
        assertBars(false, "game")
        driver.back()
        driver.awaitText("Resume")
        composeRule.onNodeWithText("Main menu").performClick()
        driver.awaitText("Play")
        assertBars(true, "menu after leaving")
    }

    @Test
    fun backOnThePauseOverlayShowsTheBarsAgain() {
        val model = driver.play()
        assertBars(false, "game")
        driver.back()
        driver.awaitOverlay(model, Overlay.PAUSED)
        assertBars(false, "pause overlay")
        driver.back()
        driver.awaitText("Play")
        assertBars(true, "menu after Back on pause")
    }

    @Test
    fun gameOverMainMenuShowsTheBarsAgain() {
        runBlocking { UiTest.graph().settings.setDifficulty(Difficulty.LEVEL_9) }
        val model = driver.play()
        driver.awaitOverlay(model, Overlay.GAME_OVER)
        assertBars(false, "game over")
        composeRule.onNodeWithText("Main menu").performClick()
        driver.awaitText("Play")
        assertBars(true, "menu after game over")
    }

    @Test
    fun barsStayHiddenAfterTheActivityIsRecreatedOnTheGame() {
        driver.play()
        assertBars(false, "game before recreate")
        scenario.recreate()
        composeRule.waitForIdle()
        assertBars(false, "game after recreate")
    }

    @Test
    fun barsAreHiddenAgainWhenTheAppReturnsFromTheBackgroundWithThemShown() {
        driver.play()
        assertBars(false, "game before background")
        scenario.moveToState(Lifecycle.State.CREATED)
        showBars()
        scenario.moveToState(Lifecycle.State.RESUMED)
        composeRule.waitForIdle()
        assertBars(false, "game after returning from the background")
    }

    @Test
    fun barsAreHiddenAgainWhenWindowFocusReturnsWithThemShown() {
        driver.play()
        assertBars(false, "game before the shade")
        shell("cmd statusbar expand-notifications")
        driver.awaitUntil(BARS_SETTLE_MILLIS, { "the window kept focus under the notification shade" }) {
            !composeRule.activity.hasWindowFocus()
        }
        showBars()
        shell("cmd statusbar collapse")
        driver.awaitUntil(BARS_SETTLE_MILLIS, { "the window did not regain focus" }) { composeRule.activity.hasWindowFocus() }
        val deadline = SystemClock.elapsedRealtime() + BARS_SETTLE_MILLIS
        while (!composeRule.activity.allBarsHidden() && SystemClock.elapsedRealtime() < deadline) Thread.sleep(GameDriver.POLL_MILLIS)
        assertTrue("game after the shade: ${composeRule.activity.barsSummary()}", composeRule.activity.allBarsHidden())
    }

    private companion object {
        const val BARS_SETTLE_MILLIS = 5_000L
    }
}
