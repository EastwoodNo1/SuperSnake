package io.github.eastwoodno1.supersnake.domain

class KeyBindings(
    private val keys: Map<Int, GameKey>,
) {
    fun press(
        code: Int,
        send: (GameInput) -> Unit,
    ): Boolean = keys[code]?.let { send(it.input) } != null
}
