package io.github.eastwoodno1.supersnake

import android.view.KeyEvent
import io.github.eastwoodno1.supersnake.domain.GameKey
import io.github.eastwoodno1.supersnake.domain.KeyBindings

val KEY_BINDINGS =
    KeyBindings(
        mapOf(
            KeyEvent.KEYCODE_DPAD_UP to GameKey.UP,
            KeyEvent.KEYCODE_DPAD_DOWN to GameKey.DOWN,
            KeyEvent.KEYCODE_DPAD_LEFT to GameKey.LEFT,
            KeyEvent.KEYCODE_DPAD_RIGHT to GameKey.RIGHT,
            KeyEvent.KEYCODE_W to GameKey.UP,
            KeyEvent.KEYCODE_S to GameKey.DOWN,
            KeyEvent.KEYCODE_A to GameKey.LEFT,
            KeyEvent.KEYCODE_D to GameKey.RIGHT,
            KeyEvent.KEYCODE_SPACE to GameKey.PAUSE,
            KeyEvent.KEYCODE_P to GameKey.PAUSE,
            KeyEvent.KEYCODE_ESCAPE to GameKey.PAUSE,
            KeyEvent.KEYCODE_BUTTON_START to GameKey.PAUSE,
        ),
    )
