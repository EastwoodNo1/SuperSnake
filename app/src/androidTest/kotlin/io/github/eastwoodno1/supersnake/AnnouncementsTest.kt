package io.github.eastwoodno1.supersnake

import android.os.SystemClock
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.Feedback
import io.github.eastwoodno1.supersnake.domain.Overlay
import io.github.eastwoodno1.supersnake.domain.Sound
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import java.util.concurrent.CopyOnWriteArrayList

@RunWith(AndroidJUnit4::class)
class AnnouncementsTest {
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = UiTest.chain(composeRule)

    private val driver = GameDriver(composeRule)

    private val polite =
        SemanticsMatcher("polite live region") {
            it.config.getOrNull(SemanticsProperties.LiveRegion) == LiveRegionMode.Polite
        }

    private fun liveTexts(): List<String> =
        composeRule
            .onAllNodes(polite)
            .fetchSemanticsNodes()
            .map {
                it.config
                    .getOrNull(SemanticsProperties.Text)
                    .orEmpty()
                    .joinToString("")
            }

    private fun collectUntil(
        timeoutMillis: Long,
        done: (List<String>) -> Boolean,
    ): List<String> {
        val seen = mutableListOf<String>()
        val deadline = SystemClock.elapsedRealtime() + timeoutMillis
        while (!done(seen)) {
            assertTrue("timed out, saw $seen", SystemClock.elapsedRealtime() < deadline)
            liveTexts().filter { it.isNotEmpty() && it != seen.lastOrNull() }.forEach { seen += it }
            Thread.sleep(GameDriver.POLL_MILLIS)
        }
        return seen
    }

    @Test
    fun c25CountdownAnnounced() {
        driver.play()
        val seen = collectUntil(GameDriver.TIMEOUT_MILLIS) { it.containsAll(listOf("3", "2", "1", "Go")) }
        assertEquals(listOf("3", "2", "1", "Go"), seen.take(4))
    }

    @Test
    fun gameOverAnnounced() {
        runBlocking { UiTest.graph().settings.setDifficulty(Difficulty.LEVEL_9) }
        val model = driver.play()
        driver.awaitOverlay(model, Overlay.GAME_OVER)
        driver.awaitUntil { liveTexts().any { it.startsWith("Game over. Score") } }
    }

    @Test
    fun countdownPlaysCountAndGoSounds() {
        val seen = CopyOnWriteArrayList<Feedback>()
        val scope = CoroutineScope(Dispatchers.Main)
        val model = driver.play()
        scope.launch { model.feedback.collect { seen += it } }
        driver.awaitUntil { seen.flatMap { it.sounds }.contains(Sound.GO) }
        scope.cancel()
        assertTrue(seen.flatMap { it.sounds }.contains(Sound.COUNT))
    }
}
