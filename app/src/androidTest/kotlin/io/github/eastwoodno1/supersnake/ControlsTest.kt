package io.github.eastwoodno1.supersnake

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.eastwoodno1.supersnake.domain.ControlScheme
import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.Direction
import io.github.eastwoodno1.supersnake.domain.GameStatus
import io.github.eastwoodno1.supersnake.domain.NeonPalette
import io.github.eastwoodno1.supersnake.domain.Overlay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ControlsTest {
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = UiTest.chain(composeRule)

    private val driver = GameDriver(composeRule)

    private fun dpadButtons() = composeRule.onAllNodesWithContentDescription("Up").fetchSemanticsNodes().size

    private fun awaitMenu() = driver.awaitText("Play")

    private fun assertBoardGone() = assertEquals(0, composeRule.onAllNodesWithTag("board").fetchSemanticsNodes().size)

    @Test
    fun dpadShownOnlyForDpadScheme() {
        val model = driver.play()
        driver.awaitStatus(model, GameStatus.RUNNING)
        assertEquals(0, dpadButtons())
        runBlocking { UiTest.graph().settings.setControlScheme(ControlScheme.DPAD) }
        driver.awaitUntil { dpadButtons() == 1 }
        listOf("Up", "Down", "Left", "Right").forEach { composeRule.onNodeWithContentDescription(it).assertIsDisplayed() }
        runBlocking { UiTest.graph().settings.setControlScheme(ControlScheme.SWIPE) }
        driver.awaitUntil { dpadButtons() == 0 }
    }

    @Test
    fun dpadUpTurns() {
        runBlocking { UiTest.graph().settings.setControlScheme(ControlScheme.DPAD) }
        val model = driver.play()
        driver.awaitStatus(model, GameStatus.RUNNING)
        driver.awaitUntil { dpadButtons() == 1 }
        composeRule.onNodeWithContentDescription("Up").performClick()
        driver.awaitUntil { model.frame.value.state.direction == Direction.UP }
        composeRule.runOnUiThread { model.pause() }
        val snake = model.frame.value.state.snake
        assertEquals(snake[1].x, snake[0].x)
        assertEquals(snake[1].y - 1, snake[0].y)
        driver.awaitPixel({ snake[0] }, NeonPalette.SNAKE)
    }

    @Test
    fun keyboardArrowTurns() {
        val model = driver.play()
        driver.awaitStatus(model, GameStatus.RUNNING)
        composeRule.onNodeWithTag("game").performKeyInput { pressKey(Key.DirectionUp) }
        driver.awaitUntil { model.frame.value.state.direction == Direction.UP }
    }

    @Test
    fun spacePauses() {
        val model = driver.play()
        driver.awaitStatus(model, GameStatus.RUNNING)
        composeRule.onNodeWithTag("game").performKeyInput { pressKey(Key.Spacebar) }
        driver.awaitStatus(model, GameStatus.PAUSED)
        driver.awaitText("Resume")
        composeRule.onNodeWithTag("game").performKeyInput { pressKey(Key.Spacebar) }
        driver.awaitStatus(model, GameStatus.COUNTDOWN)
    }

    @Test
    fun backPausesThenLeaves() {
        val model = driver.play()
        driver.awaitStatus(model, GameStatus.RUNNING)
        driver.back()
        driver.awaitStatus(model, GameStatus.PAUSED)
        composeRule.onNodeWithText("Resume").assertIsDisplayed()
        driver.back()
        awaitMenu()
        assertBoardGone()
    }

    @Test
    fun pauseMainMenuLeaves() {
        val model = driver.play()
        driver.awaitStatus(model, GameStatus.RUNNING)
        driver.back()
        driver.awaitText("Main menu")
        composeRule.onNodeWithText("Main menu").performClick()
        awaitMenu()
        assertBoardGone()
    }

    @Test
    fun gameOverMainMenuLeaves() {
        runBlocking { UiTest.graph().settings.setDifficulty(Difficulty.LEVEL_9) }
        val model = driver.play()
        driver.awaitOverlay(model, Overlay.GAME_OVER)
        composeRule.onNodeWithText("Main menu").performClick()
        awaitMenu()
        assertBoardGone()
    }

    @Test
    fun backFromGameOverLeaves() {
        runBlocking { UiTest.graph().settings.setDifficulty(Difficulty.LEVEL_9) }
        val model = driver.play()
        driver.awaitOverlay(model, Overlay.GAME_OVER)
        driver.back()
        awaitMenu()
        assertBoardGone()
    }
}
