package io.github.eastwoodno1.supersnake

import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.GameInput
import io.github.eastwoodno1.supersnake.domain.GameStatus
import io.github.eastwoodno1.supersnake.domain.Overlay
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RestartTest {
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = UiTest.chain(composeRule)

    private val driver = GameDriver(composeRule)

    @Test
    fun restartReachesRunning() {
        val model = driver.play()
        driver.awaitStatus(model, GameStatus.RUNNING)
        composeRule.runOnUiThread { model.input(GameInput.TogglePause) }
        driver.awaitText("Resume")
        composeRule.onNodeWithText("Restart").performClick()
        driver.awaitStatus(model, GameStatus.RUNNING)
    }

    @Test
    fun playAgainReachesRunning() {
        runBlocking { UiTest.graph().settings.setDifficulty(Difficulty.LEVEL_9) }
        val model = driver.play()
        driver.awaitOverlay(model, Overlay.GAME_OVER)
        composeRule.onNodeWithText("Play again").performClick()
        driver.awaitStatus(model, GameStatus.RUNNING)
    }
}
