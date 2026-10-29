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

    fun chain(
        activityRule: TestRule,
        reducedMotion: Boolean = true,
    ): RuleChain =
        RuleChain
            .outerRule(
                object : ExternalResource() {
                    private var previousScale = "1"

                    override fun before() {
                        previousScale = shell("settings get global animator_duration_scale").trim()
                        shell("settings put secure immersive_mode_confirmations confirmed")
                        setScale(if (reducedMotion) "0" else "1")
                        runBlocking {
                            graph().settingsRepository.update { Settings() }
                            graph().highScoreRepository.update { emptyList() }
                        }
                    }

                    override fun after() = setScale(previousScale)
                },
            ).around(activityRule)

    fun animationsOn() = setScale("1")

    private fun setScale(value: String) {
        shell("settings put global animator_duration_scale $value")
    }

    private fun shell(command: String): String {
        val descriptor = InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(command)
        return ParcelFileDescriptor.AutoCloseInputStream(descriptor).use { String(it.readBytes()) }
    }
}
