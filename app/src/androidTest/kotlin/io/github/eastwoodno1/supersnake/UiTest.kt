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
                        shell("settings put global animator_duration_scale 0")
                        runBlocking {
                            graph().settingsRepository.update { Settings() }
                            graph().highScoreRepository.update { emptyList() }
                        }
                    }

                    override fun after() = animationsOn()
                },
            ).around(activityRule)

    fun animationsOn() = shell("settings put global animator_duration_scale 1")

    fun shell(command: String) {
        val descriptor = InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(command)
        ParcelFileDescriptor.AutoCloseInputStream(descriptor).use { it.readBytes() }
    }
}
