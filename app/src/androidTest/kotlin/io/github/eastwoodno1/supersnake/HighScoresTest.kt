package io.github.eastwoodno1.supersnake

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.eastwoodno1.supersnake.domain.Difficulty
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RunWith(AndroidJUnit4::class)
class HighScoresTest {
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = UiTest.chain(composeRule)

    @Test
    fun highScoresListsSavedEntries() {
        runBlocking {
            UiTest.graph().recordHighScore(30, Difficulty.LEVEL_2)
            UiTest.graph().recordHighScore(90, Difficulty.ASSIST)
        }
        composeRule.onNodeWithText("High scores").performClick()
        val date = LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        composeRule.waitUntil(TIMEOUT_MILLIS) { composeRule.onAllNodes(hasText("2.")).fetchSemanticsNodes().isNotEmpty() }
        val first = composeRule.onNode(hasText("1.") and hasText("90") and hasText("A") and hasText(date))
        val second = composeRule.onNode(hasText("2.") and hasText("30") and hasText("2") and hasText(date))
        first.assertIsDisplayed()
        second.assertIsDisplayed()
        assertTrue(first.getUnclippedBoundsInRoot().top < second.getUnclippedBoundsInRoot().top)
    }

    @Test
    fun highScoresEmptyState() {
        composeRule.onNodeWithText("High scores").performClick()
        composeRule.onNodeWithText("No scores yet").assertIsDisplayed()
    }

    private companion object {
        const val TIMEOUT_MILLIS = 10_000L
    }
}
