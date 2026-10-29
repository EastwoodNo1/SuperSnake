package io.github.eastwoodno1.supersnake

import android.os.ParcelFileDescriptor
import androidx.test.platform.app.InstrumentationRegistry
import io.github.eastwoodno1.supersnake.domain.Settings
import kotlinx.coroutines.runBlocking
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.rules.TestRule

object UiTest {
    fun graph(): AppGraph = (InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as SuperSnakeApp).graph

    fun chain(activityRule: TestRule): RuleChain =
        RuleChain
            .outerRule(
                object : ExternalResource() {
                    override fun before() {
                        shell("settings put secure immersive_mode_confirmations confirmed")
                        runBlocking {
                            graph().settingsRepository.update { Settings() }
                            graph().highScoreRepository.update { emptyList() }
                        }
                    }
                },
            ).around(activityRule)

    private fun shell(command: String) {
        val descriptor = InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(command)
        ParcelFileDescriptor.AutoCloseInputStream(descriptor).use { it.readBytes() }
    }
}
