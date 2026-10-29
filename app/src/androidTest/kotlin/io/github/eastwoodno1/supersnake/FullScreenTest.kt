package io.github.eastwoodno1.supersnake

import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
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

    private fun barsVisible() =
        composeRule.activity.barsVisible(WindowInsetsCompat.Type.statusBars()) ||
            composeRule.activity.barsVisible(WindowInsetsCompat.Type.navigationBars())

    private fun assertBars(
        visible: Boolean,
        where: String,
    ) {
        driver.awaitUntil(BARS_SETTLE_MILLIS, { "$where: bars visible=${barsVisible()}, expected $visible" }) { barsVisible() == visible }
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
    fun barsStayHiddenAfterTheActivityIsRecreatedOnTheGame() {
        driver.play()
        assertBars(false, "game before recreate")
        composeRule.activityRule.scenario.recreate()
        composeRule.waitForIdle()
        assertBars(false, "game after recreate")
    }

    @Test
    fun barsStayHiddenAfterTheAppReturnsFromTheBackground() {
        driver.play()
        assertBars(false, "game before background")
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        composeRule.waitForIdle()
        assertBars(false, "game after background")
    }

    private companion object {
        const val BARS_SETTLE_MILLIS = 5_000L
    }
}
