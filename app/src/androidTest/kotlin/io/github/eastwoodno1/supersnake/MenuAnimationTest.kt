package io.github.eastwoodno1.supersnake

import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MenuAnimationTest {
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = UiTest.chain(composeRule, reducedMotion = false)

    @Test
    fun animatedIconShowsAndMovesWhenMotionIsNotReduced() {
        composeRule.mainClock.autoAdvance = false
        composeRule.onNodeWithTag("menu_icon_animated").assertIsDisplayed()
        composeRule.onNodeWithTag("menu_icon_static").assertDoesNotExist()
        val before = composeRule.onNodeWithTag("menu_icon_animated").captureToImage().asAndroidBitmap()
        composeRule.mainClock.advanceTimeBy(FRAME_GAP_MILLIS)
        val after = composeRule.onNodeWithTag("menu_icon_animated").captureToImage().asAndroidBitmap()
        assertFalse(before.sameAs(after))
    }

    private companion object {
        const val FRAME_GAP_MILLIS = 900L
    }
}
