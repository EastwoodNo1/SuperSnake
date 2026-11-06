package io.github.eastwoodno1.supersnake

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isOff
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsTest {
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = UiTest.chain(composeRule)

    @Test
    fun settingsPersistAcrossRecreate() {
        composeRule.onNodeWithText("Settings").performClick()
        composeRule.onNodeWithContentDescription("Difficulty 5").performClick()
        composeRule.onNodeWithText("Sound").performClick()
        awaitDifficulty5AndSoundOff()
        composeRule.activityRule.scenario.recreate()
        awaitDifficulty5AndSoundOff()
    }

    @Test
    fun controlSchemeCanBeChosen() {
        composeRule.onNodeWithText("Settings").performClick()
        composeRule.onNodeWithText("D-pad").performClick()
        composeRule.onNodeWithText("D-pad").assertIsSelected()
    }

    private fun awaitDifficulty5AndSoundOff() {
        composeRule.waitUntil(TIMEOUT_MILLIS) {
            composeRule.onAllNodes(hasContentDescription("Difficulty 5") and isSelected()).fetchSemanticsNodes().isNotEmpty() &&
                composeRule.onAllNodes(hasText("Sound") and isOff()).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private companion object {
        const val TIMEOUT_MILLIS = 10_000L
    }
}
