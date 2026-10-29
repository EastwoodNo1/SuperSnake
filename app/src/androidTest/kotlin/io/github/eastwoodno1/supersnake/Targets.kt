package io.github.eastwoodno1.supersnake

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.ComposeTestRule
import org.junit.Assert.assertTrue

object Targets {
    const val MIN_DP = 48f

    fun assertAtLeast48dp(
        rule: ComposeTestRule,
        density: Float,
        where: String,
    ) {
        val nodes = rule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.OnClick)).fetchSemanticsNodes()
        assertTrue("$where has no clickable node", nodes.isNotEmpty())
        nodes.forEach { node ->
            val width = node.touchBoundsInRoot.width / density
            val height = node.touchBoundsInRoot.height / density
            assertTrue("$where ${node.id}: ${width}dp x ${height}dp", width >= MIN_DP - TOLERANCE && height >= MIN_DP - TOLERANCE)
        }
    }

    private const val TOLERANCE = 0.01f
}
