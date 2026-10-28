package io.github.eastwoodno1.supersnake.data

import io.github.eastwoodno1.supersnake.domain.ControlScheme
import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.Settings
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

class SettingsSerializerTest {
    private suspend fun read(bytes: ByteArray) = SettingsSerializer.readFrom(bytes.inputStream())

    private suspend fun read(text: String) = read(text.toByteArray())

    @Test
    fun settingsRoundTrip() =
        runTest {
            val settings = Settings(Difficulty.LEVEL_7, soundOn = false, hapticsOn = false, controlScheme = ControlScheme.DPAD)
            val out = ByteArrayOutputStream()
            SettingsSerializer.writeTo(settings, out)
            assertEquals(settings, read(out.toByteArray()))
        }

    @Test
    fun settingsAreWrittenAsKeyValueLines() =
        runTest {
            val out = ByteArrayOutputStream()
            SettingsSerializer.writeTo(Settings(Difficulty.ASSIST, soundOn = false), out)
            assertEquals(
                "difficulty=ASSIST\nsound=false\nhaptics=true\ncontrol_scheme=SWIPE\n",
                out.toString(Charsets.UTF_8),
            )
        }

    @Test
    fun settingsEmptyIsDefault() =
        runTest {
            assertEquals(Settings(), read(""))
        }

    @Test
    fun settingsFallBackFieldByField() =
        runTest {
            val text = "difficulty=LEVEL_42\nsound=maybe\nhaptics=false\ncontrol_scheme=JOYSTICK\nnoise\nextra=1\n"
            assertEquals(Settings(hapticsOn = false), read(text))
        }

    @Test
    fun settingsKeepDefaultForUnparsableBooleans() =
        runTest {
            assertEquals(Settings(soundOn = false), read("sound=false\nhaptics=perhaps\n"))
        }

    @Test
    fun settingsReadWindowsLineEndings() =
        runTest {
            assertEquals(Settings(Difficulty.LEVEL_2), read("difficulty=LEVEL_2\r\nsound=true\r\n"))
        }

    @Test
    fun randomBytesReadAsDefaults() =
        runTest {
            assertEquals(Settings(), read(byteArrayOf(0, -1, 19, -128, 10, 61, -2)))
        }

    @Test
    fun defaultValueIsDefaultSettings() {
        assertEquals(Settings(), SettingsSerializer.defaultValue)
    }
}
