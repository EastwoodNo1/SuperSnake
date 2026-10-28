package io.github.eastwoodno1.supersnake.data

import androidx.datastore.core.Serializer
import io.github.eastwoodno1.supersnake.domain.ControlScheme
import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.Settings
import java.io.InputStream
import java.io.OutputStream

object SettingsSerializer : Serializer<Settings> {
    override val defaultValue = Settings()

    override suspend fun readFrom(input: InputStream): Settings {
        val values =
            String(input.readBytes(), Charsets.UTF_8)
                .lines()
                .filter { '=' in it }
                .associate { it.substringBefore('=') to it.substringAfter('=') }
        return Settings(
            difficulty = Difficulty.entries.firstOrNull { it.name == values["difficulty"] } ?: defaultValue.difficulty,
            soundOn = values["sound"]?.toBooleanStrictOrNull() ?: defaultValue.soundOn,
            hapticsOn = values["haptics"]?.toBooleanStrictOrNull() ?: defaultValue.hapticsOn,
            controlScheme =
                ControlScheme.entries.firstOrNull { it.name == values["control_scheme"] } ?: defaultValue.controlScheme,
        )
    }

    override suspend fun writeTo(
        t: Settings,
        output: OutputStream,
    ) {
        val text =
            "difficulty=${t.difficulty.name}\n" +
                "sound=${t.soundOn}\n" +
                "haptics=${t.hapticsOn}\n" +
                "control_scheme=${t.controlScheme.name}\n"
        output.write(text.toByteArray(Charsets.UTF_8))
    }
}
