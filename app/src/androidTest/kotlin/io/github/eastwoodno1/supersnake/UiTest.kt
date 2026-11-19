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
                    private var previousScale: String? = null
                    private var previousConfirmations: String? = null

                    override fun before() {
                        previousScale = setting("global", SCALE)
                        previousConfirmations = setting("secure", CONFIRMATIONS)
                        shell("settings put secure $CONFIRMATIONS confirmed")
                        setScale(if (reducedMotion) "0" else "1")
                        runBlocking {
                            graph().settingsRepository.update { Settings() }
                            graph().highScoreRepository.update { emptyList() }
                        }
                    }

                    override fun after() {
                        restore("global", SCALE, previousScale)
                        restore("secure", CONFIRMATIONS, previousConfirmations)
                    }
                },
            ).around(activityRule)

    fun animationsOn() = setScale("1")

    fun animationsOff() = setScale("0")

    fun animatorScale(): String? = setting("global", SCALE)

    fun setAnimatorScale(value: String) = setScale(value)

    fun restoreAnimatorScale(previous: String?) = restore("global", SCALE, previous)

    fun setting(
        namespace: String,
        key: String,
    ): String? = shell("settings get $namespace $key").trim().takeUnless { it.isEmpty() || it == "null" }

    fun restore(
        namespace: String,
        key: String,
        previous: String?,
    ) {
        shell(if (previous == null) "settings delete $namespace $key" else "settings put $namespace $key $previous")
    }

    fun put(
        namespace: String,
        key: String,
        value: String,
    ) {
        shell("settings put $namespace $key $value")
    }

    private fun setScale(value: String) {
        put("global", SCALE, value)
    }

    private const val SCALE = "animator_duration_scale"
    private const val CONFIRMATIONS = "immersive_mode_confirmations"

    private fun shell(command: String): String {
        val descriptor = InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(command)
        return ParcelFileDescriptor.AutoCloseInputStream(descriptor).use { String(it.readBytes()) }
    }
}
