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

    private fun leaveGame() =
        composeRule.runOnUiThread {
            repeat(2) { composeRule.activity.onBackPressedDispatcher.onBackPressed() }
        }

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
        leaveGame()
        composeRule.waitUntil(GAME_TIMEOUT_MILLIS) { composeRule.onAllNodesWithText("Play").fetchSemanticsNodes().isNotEmpty() }
        assertEquals(0, composeRule.onAllNodesWithTag("board").fetchSemanticsNodes().size)
    }

    @Test
    fun playOpensTheGame() {
        composeRule.onNodeWithText("Play").performClick()
        composeRule.waitUntil(GAME_TIMEOUT_MILLIS) { composeRule.onAllNodesWithTag("board").fetchSemanticsNodes().isNotEmpty() }
    }

    @Test
    fun playAgainAfterLeavingShowsTheBoard() {
        repeat(2) {
            composeRule.onNodeWithText("Play").performClick()
            composeRule.waitUntil(GAME_TIMEOUT_MILLIS) { composeRule.onAllNodesWithTag("board").fetchSemanticsNodes().isNotEmpty() }
            leaveGame()
            composeRule.waitUntil(GAME_TIMEOUT_MILLIS) { composeRule.onAllNodesWithText("Play").fetchSemanticsNodes().isNotEmpty() }
        }
    }

    @Test
    fun backRightAfterPlayLeavesWithoutKillingTheProcess() {
        composeRule.onNodeWithText("Play").performClick()
        composeRule.waitUntil(GAME_TIMEOUT_MILLIS) { composeRule.onAllNodesWithTag("board").fetchSemanticsNodes().isNotEmpty() }
        leaveGame()
        composeRule.waitUntil(GAME_TIMEOUT_MILLIS) { composeRule.onAllNodesWithText("Play").fetchSemanticsNodes().isNotEmpty() }
        Thread.sleep(SETTLE_MILLIS)
        composeRule.onNodeWithText("Play").assertIsDisplayed()
    }

    private companion object {
        const val SETTLE_MILLIS = 6_000L
        const val FAST_TAP_GAP_MILLIS = 30L
        const val GAME_TIMEOUT_MILLIS = 60_000L
    }
}
