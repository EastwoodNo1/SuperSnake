package io.github.eastwoodno1.supersnake

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MenuTest {
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = UiTest.chain(composeRule)

    @Test
    fun menuShowsItemsAndBest() {
        composeRule.onNodeWithText("Play").assertIsDisplayed()
        composeRule.onNodeWithText("High scores").assertIsDisplayed()
        composeRule.onNodeWithText("Settings").assertIsDisplayed()
        composeRule.onNodeWithText("Best: 0").assertIsDisplayed()
    }

    @Test
    fun doubleTapPlayStartsOneGame() {
        composeRule.onNodeWithText("Play").performTouchInput {
            click()
            advanceEventTime(FAST_TAP_GAP_MILLIS)
            click()
        }
        composeRule.waitUntil(GAME_TIMEOUT_MILLIS) { composeRule.onAllNodesWithTag("board").fetchSemanticsNodes().isNotEmpty() }
        composeRule.runOnUiThread { composeRule.activity.onBackPressedDispatcher.onBackPressed() }
        composeRule.waitUntil(GAME_TIMEOUT_MILLIS) { composeRule.onAllNodesWithText("Play").fetchSemanticsNodes().isNotEmpty() }
        assertEquals(0, composeRule.onAllNodesWithTag("board").fetchSemanticsNodes().size)
    }

    @Test
    fun playOpensTheGame() {
        composeRule.onNodeWithText("Play").performClick()
        composeRule.waitUntil(GAME_TIMEOUT_MILLIS) { composeRule.onAllNodesWithTag("board").fetchSemanticsNodes().isNotEmpty() }
    }

    private companion object {
        const val FAST_TAP_GAP_MILLIS = 30L
        const val GAME_TIMEOUT_MILLIS = 60_000L
    }
}
