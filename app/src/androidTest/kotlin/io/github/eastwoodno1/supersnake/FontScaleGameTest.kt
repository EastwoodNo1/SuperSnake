package io.github.eastwoodno1.supersnake

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.eastwoodno1.supersnake.domain.ControlScheme
import io.github.eastwoodno1.supersnake.domain.GameStatus
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FontScaleGameTest {
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = UiTest.chain(composeRule)

    private val driver = GameDriver(composeRule)

    private fun activityFontScale(): Float {
        var scale = 0f
        composeRule.activityRule.scenario.onActivity { scale = it.resources.configuration.fontScale }
        return scale
    }

    @Test
    fun fontScale200OnTheRealGameScreen() {
        runBlocking { UiTest.graph().settings.setControlScheme(ControlScheme.DPAD) }
        val previous = UiTest.setting("system", "font_scale")
        try {
            val model = driver.play()
            driver.awaitStatus(model, GameStatus.RUNNING)
            UiTest.put("system", "font_scale", "2.0")
            driver.awaitUntil(SCALE_MILLIS, { "the activity never saw font scale 2: ${activityFontScale()}" }) {
                activityFontScale() >=
                    1.99f
            }
            driver.awaitUntil { composeRule.onAllNodesWithContentDescription("Pause").fetchSemanticsNodes().isNotEmpty() }
            composeRule.onNodeWithContentDescription("Pause").performClick()
            driver.awaitText("Resume")
            val density = composeRule.activity.resources.displayMetrics.density
            Targets.assertAtLeast48dp(composeRule, density, "game screen at font scale 2")
            listOf("Resume", "Restart", "Main menu").forEach { composeRule.onNodeWithText(it).assertIsDisplayed() }
        } finally {
            UiTest.restore("system", "font_scale", previous)
        }
    }

    private companion object {
        const val SCALE_MILLIS = 15_000L
    }
}
