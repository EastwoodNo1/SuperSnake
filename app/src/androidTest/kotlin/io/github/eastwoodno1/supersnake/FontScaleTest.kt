package io.github.eastwoodno1.supersnake

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.FontScale
import androidx.compose.ui.test.ForcedSize
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.then
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.Overlay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FontScaleTest {
    private val composeRule = createComposeRule()

    @get:Rule
    val rules: RuleChain = UiTest.chain(composeRule)

    private var density = 1f

    private fun show(
        scrolls: Boolean = true,
        buttons: Boolean = true,
        content: @Composable () -> Unit,
    ) {
        composeRule.setContent {
            DeviceConfigurationOverride(
                DeviceConfigurationOverride.FontScale(2f) then DeviceConfigurationOverride.ForcedSize(DpSize(800.dp, 360.dp)),
            ) {
                density = LocalDensity.current.density
                NeonTheme { content() }
            }
        }
        composeRule.waitForIdle()
        assertNothingClips(scrolls)
        assertButtonsReachable(scrolls, buttons)
    }

    private fun assertNothingClips(scrolls: Boolean) {
        val root = composeRule.onRoot().fetchSemanticsNode().boundsInRoot
        val texts = composeRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.GetTextLayoutResult)).fetchSemanticsNodes()
        assertTrue("no text nodes", texts.isNotEmpty())
        texts.forEach { node ->
            val results = mutableListOf<TextLayoutResult>()
            node.config
                .getOrNull(SemanticsActions.GetTextLayoutResult)
                ?.action
                ?.invoke(results)
            assertTrue("no layout for ${node.id}", results.isNotEmpty())
            results.forEach { assertFalse("text clips: ${it.layoutInput.text} ${describe(it)}", clipped(it)) }
            val bounds = node.boundsInRoot
            val sideways = bounds.left >= root.left && bounds.right <= root.right
            val vertically = scrolls || (bounds.top >= root.top && bounds.bottom <= root.bottom)
            assertTrue("text leaves the screen: ${node.id} $bounds in $root", sideways && vertically)
        }
    }

    private fun clipped(result: TextLayoutResult): Boolean {
        val widest = (0 until result.lineCount).maxOf { result.getLineRight(it) }
        val tooTall = result.multiParagraph.height > result.size.height + TOLERANCE_PX
        val tooWide = widest > result.size.width + TOLERANCE_PX || widest > result.layoutInput.constraints.maxWidth + TOLERANCE_PX
        return tooTall || tooWide
    }

    private fun describe(result: TextLayoutResult) =
        "text ${result.multiParagraph.width}x${result.multiParagraph.height} lines=${result.lineCount} box=${result.size}"

    private fun assertButtonsReachable(
        scrolls: Boolean,
        buttons: Boolean,
    ) {
        if (!buttons) return
        val clickable = SemanticsMatcher.keyIsDefined(SemanticsActions.OnClick)
        val count = composeRule.onAllNodes(clickable).fetchSemanticsNodes().size
        assertTrue("no buttons", count > 0)
        (0 until count).forEach { index ->
            val button = composeRule.onAllNodes(clickable)[index]
            if (scrolls) button.performScrollTo()
            button.assertIsDisplayed()
        }
        Targets.assertAtLeast48dp(composeRule, density, "font scale 2")
    }

    private fun actions() = OverlayActions({}, {}, {}, {})

    @Test
    fun fontScale200NothingClips() {
        val model = MenuViewModel(UiTest.graph())
        show { MenuScreen(model, {}, {}, {}) }
    }

    @Test
    fun fontScale200SettingsNothingClips() {
        val model = SettingsViewModel(UiTest.graph())
        show { SettingsScreen(model) }
    }

    @Test
    fun fontScale200HighScoresNothingClips() {
        runBlocking {
            UiTest.graph().recordHighScore(90, Difficulty.ASSIST)
            UiTest.graph().recordHighScore(30, Difficulty.LEVEL_2)
        }
        val model = HighScoresViewModel(UiTest.graph())
        show(buttons = false) { HighScoresScreen(model) }
    }

    @Test
    fun fontScale200PausedNothingClips() {
        show { Overlays(Overlay.PAUSED, null, 12, 90, null, actions()) }
    }

    @Test
    fun fontScale200GameOverNothingClips() {
        show { Overlays(Overlay.GAME_OVER, null, 12, 90, null, actions()) }
    }

    @Test
    fun fontScale200LandscapeSideColumnNothingClips() {
        show {
            Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                Column(Modifier.verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                    Hud(120, "A", {}, Modifier, true)
                    DPad({})
                }
            }
        }
    }

    @Test
    fun fontScale200HudAndDpadNothingClips() {
        show(scrolls = false) {
            Row {
                Hud(120, "A", {}, Modifier.weight(1f))
                DPad({})
            }
        }
    }

    private companion object {
        const val TOLERANCE_PX = 0.5f
    }
}
