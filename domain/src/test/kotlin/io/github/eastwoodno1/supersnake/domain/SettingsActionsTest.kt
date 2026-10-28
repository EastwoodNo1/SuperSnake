package io.github.eastwoodno1.supersnake.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SettingsActionsTest {
    @Test
    fun settingsActionsUpdateOneField() =
        runTest {
            val actions = SettingsActions(FakeSettingsRepository())
            assertEquals(Settings(), actions.settings.first())
            actions.setDifficulty(Difficulty.LEVEL_5)
            assertEquals(Settings(difficulty = Difficulty.LEVEL_5), actions.settings.first())
            actions.setSound(false)
            assertEquals(Settings(difficulty = Difficulty.LEVEL_5, soundOn = false), actions.settings.first())
            actions.setHaptics(false)
            assertEquals(
                Settings(difficulty = Difficulty.LEVEL_5, soundOn = false, hapticsOn = false),
                actions.settings.first(),
            )
            actions.setControlScheme(ControlScheme.DPAD)
            assertEquals(
                Settings(Difficulty.LEVEL_5, soundOn = false, hapticsOn = false, controlScheme = ControlScheme.DPAD),
                actions.settings.first(),
            )
        }
}
