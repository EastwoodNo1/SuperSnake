package io.github.eastwoodno1.supersnake.domain

enum class ControlScheme { SWIPE, DPAD }

data class Settings(
    val difficulty: Difficulty = Difficulty.LEVEL_1,
    val soundOn: Boolean = true,
    val hapticsOn: Boolean = true,
    val controlScheme: ControlScheme = ControlScheme.SWIPE,
)
