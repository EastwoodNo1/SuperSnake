package io.github.eastwoodno1.supersnake

import android.app.UiAutomation
import android.content.res.Configuration
import android.graphics.Bitmap
import android.view.Surface
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.eastwoodno1.supersnake.domain.ControlScheme
import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.GameStatus
import io.github.eastwoodno1.supersnake.domain.Overlay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class LayoutScreenshotTest {
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = UiTest.chain(composeRule)

    private val driver = GameDriver(composeRule)
    private val automation = InstrumentationRegistry.getInstrumentation().uiAutomation

    private fun landscape() = composeRule.activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    private fun shoot(name: String) {
        val directory = InstrumentationRegistry.getArguments().getString("additionalTestOutputDir")
        checkNotNull(directory) { "additionalTestOutputDir is absent" }
        val label = if (landscape()) "landscape" else "portrait"
        val file = File(directory).apply { mkdirs() }.resolve("$label-$name.png")
        val bitmap = checkNotNull(automation.takeScreenshot()) { "no screenshot" }
        file.outputStream().use { assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, it)) }
    }

    private fun rotate() {
        val wasLandscape = landscape()
        val rotation = composeRule.activity.display.rotation
        val upright = rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180
        automation.setRotation(if (upright) UiAutomation.ROTATION_FREEZE_90 else UiAutomation.ROTATION_FREEZE_0)
        driver.awaitUntil { landscape() != wasLandscape }
        driver.awaitText("Play")
    }

    private fun pass() {
        shoot("menu")
        val model = driver.play()
        driver.awaitStatus(model, GameStatus.RUNNING)
        shoot("game")
        assertDpadBesideOrBelowBoard()
        composeRule.onNodeWithContentDescription("Pause").performClick()
        driver.awaitText("Resume")
        shoot("pause")
        composeRule.onNodeWithText("Main menu").performClick()
        driver.awaitText("Play")
        runBlocking { UiTest.graph().settings.setDifficulty(Difficulty.LEVEL_9) }
        val over = driver.play()
        driver.awaitOverlay(over, Overlay.GAME_OVER)
        shoot("game-over")
        composeRule.onNodeWithText("Main menu").performClick()
        driver.awaitText("Play")
        runBlocking { UiTest.graph().settings.setDifficulty(Difficulty.LEVEL_1) }
    }

    private fun assertDpadBesideOrBelowBoard() {
        val board = composeRule.onNodeWithTag("board").fetchSemanticsNode().boundsInRoot
        val up = composeRule.onNodeWithContentDescription("Up").fetchSemanticsNode().boundsInRoot
        val left = composeRule.onNodeWithContentDescription("Left").fetchSemanticsNode().boundsInRoot
        val right = composeRule.onNodeWithContentDescription("Right").fetchSemanticsNode().boundsInRoot
        val down = composeRule.onNodeWithContentDescription("Down").fetchSemanticsNode().boundsInRoot
        val place = if (landscape()) left.left >= board.right else up.top >= board.bottom
        assertTrue("d-pad overlaps the board: $board $up", place)
        assertTrue("d-pad leaves the screen", listOf(up, down, left, right).all { it.left >= 0f && it.top >= 0f })
        assertTrue("d-pad shape", left.right <= right.left && up.bottom <= down.top)
    }

    @Test
    fun portraitAndLandscapeScreenshots() {
        runBlocking { UiTest.graph().settings.setControlScheme(ControlScheme.DPAD) }
        try {
            pass()
            rotate()
            pass()
        } finally {
            automation.setRotation(UiAutomation.ROTATION_UNFREEZE)
        }
    }

    private companion object {
        const val PNG_QUALITY = 100
    }
}
