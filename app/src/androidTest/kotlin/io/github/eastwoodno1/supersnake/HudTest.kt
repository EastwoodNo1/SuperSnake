package io.github.eastwoodno1.supersnake

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.GameStatus
import io.github.eastwoodno1.supersnake.domain.Overlay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HudTest {
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = UiTest.chain(composeRule)

    private val driver = GameDriver(composeRule)

    private fun setDifficulty(difficulty: Difficulty) = runBlocking { UiTest.graph().settings.setDifficulty(difficulty) }

    @Test
    fun hudShowsScoreAndLevel() {
        driver.play()
        driver.awaitText("Score 0")
        driver.awaitText("Level 1")
        composeRule.onNodeWithText("Score 0").assertIsDisplayed()
        composeRule.onNodeWithText("Level 1").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Pause").assertIsDisplayed()
    }

    @Test
    fun pauseButtonShowsOverlay() {
        val model = driver.play()
        driver.awaitStatus(model, GameStatus.RUNNING)
        composeRule.onNodeWithContentDescription("Pause").performClick()
        driver.awaitText("Resume")
        composeRule.onNodeWithText("Resume").assertIsDisplayed()
        composeRule.onNodeWithText("Restart").assertIsDisplayed()
        composeRule.onNodeWithText("Main menu").assertIsDisplayed()
        composeRule.onNodeWithText("Resume").performClick()
        driver.awaitText("3")
        assertEquals(GameStatus.COUNTDOWN, model.frame.value.state.status)
        assertEquals(3, model.frame.value.countdown)
    }

    @Test
    fun restartStartsNewGame() {
        val model = driver.play()
        driver.awaitStatus(model, GameStatus.RUNNING)
        composeRule.onNodeWithContentDescription("Pause").performClick()
        driver.awaitText("Restart")
        composeRule.onNodeWithText("Restart").performClick()
        driver.awaitText("3")
        driver.awaitStatus(model, GameStatus.COUNTDOWN)
        assertEquals(0, model.frame.value.state.score)
        composeRule.onNodeWithText("Score 0").assertIsDisplayed()
    }

    @Test
    fun gameOverShowsScoreAndBest() {
        setDifficulty(Difficulty.LEVEL_9)
        val model = driver.play()
        driver.awaitOverlay(model, Overlay.GAME_OVER)
        driver.awaitText("Final score", substring = true)
        driver.awaitText("Best", substring = true)
        composeRule.onNodeWithText("Play again").assertIsDisplayed()
        composeRule.onNodeWithText("Main menu").assertIsDisplayed()
    }

    @Test
    fun difficultyChangeWaitsForNextGame() {
        val model = driver.play()
        driver.awaitStatus(model, GameStatus.RUNNING)
        setDifficulty(Difficulty.LEVEL_9)
        composeRule.onNodeWithText("Level 1").assertIsDisplayed()
        driver.awaitOverlay(model, Overlay.GAME_OVER)
        composeRule.onNodeWithText("Level 1").assertIsDisplayed()
        composeRule.onNodeWithText("Play again").performClick()
        driver.awaitText("Level 9")
        assertEquals(Difficulty.LEVEL_9, model.frame.value.state.difficulty)
    }
}
