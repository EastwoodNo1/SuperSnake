package io.github.eastwoodno1.supersnake

import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = UiTest.chain(composeRule)

    private fun back() = composeRule.runOnUiThread { composeRule.activity.onBackPressedDispatcher.onBackPressed() }

    private fun awaitText(text: String) =
        composeRule.waitUntil(TIMEOUT_MILLIS) { composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() }

    private fun labelled(node: SemanticsNode): Boolean {
        val text =
            node.config
                .getOrNull(SemanticsProperties.Text)
                .orEmpty()
                .joinToString("")
        val description =
            node.config
                .getOrNull(SemanticsProperties.ContentDescription)
                .orEmpty()
                .joinToString("")
        return text.isNotEmpty() || description.isNotEmpty()
    }

    private fun assertEveryClickableIsLabelled() {
        val nodes = composeRule.onAllNodes(hasClickAction()).fetchSemanticsNodes()
        assertTrue("no clickable nodes", nodes.isNotEmpty())
        nodes.forEach { assertTrue("unlabelled: ${it.config}", labelled(it)) }
    }

    @Test
    fun backFromSettingsReturnsToMenu() {
        composeRule.onNodeWithText("Settings").performClick()
        awaitText("Sound")
        back()
        awaitText("Play")
        assertTrue(composeRule.onAllNodesWithText("Sound").fetchSemanticsNodes().isEmpty())
    }

    @Test
    fun backFromHighScoresReturnsToMenu() {
        composeRule.onNodeWithText("High scores").performClick()
        awaitText("No scores yet")
        back()
        awaitText("Play")
    }

    @Test
    fun everyControlHasALabel() {
        awaitText("Play")
        assertEveryClickableIsLabelled()
        composeRule.onNodeWithText("Settings").performClick()
        awaitText("Sound")
        assertEveryClickableIsLabelled()
        back()
        awaitText("High scores")
        composeRule.onNodeWithText("High scores").performClick()
        awaitText("No scores yet")
        back()
        awaitText("Play")
    }

    private companion object {
        const val TIMEOUT_MILLIS = 10_000L
    }
}
