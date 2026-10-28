package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GameInputTest {
    @Test
    fun keysMapToInput() {
        assertEquals(
            listOf(
                GameInput.Turn(Direction.UP),
                GameInput.Turn(Direction.DOWN),
                GameInput.Turn(Direction.LEFT),
                GameInput.Turn(Direction.RIGHT),
                GameInput.TogglePause,
            ),
            GameKey.entries.map { it.input },
        )
    }
}
