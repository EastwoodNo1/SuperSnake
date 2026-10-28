package io.github.eastwoodno1.supersnake.domain

enum class Screen { MENU, GAME, HIGH_SCORES, SETTINGS }

data class BackStack(
    val screens: List<Screen> = listOf(Screen.MENU),
) {
    val top: Screen
        get() = screens.last()

    val canPop: Boolean
        get() = screens.size > 1

    fun push(screen: Screen): BackStack =
        when (screen) {
            top -> this
            Screen.MENU -> home()
            else -> BackStack(screens + screen)
        }

    fun pop(): BackStack = if (canPop) BackStack(screens.dropLast(1)) else this

    fun home(): BackStack = BackStack()
}
