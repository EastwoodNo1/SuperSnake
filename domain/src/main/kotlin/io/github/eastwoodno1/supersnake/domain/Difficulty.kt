package io.github.eastwoodno1.supersnake.domain

enum class Difficulty(
    val label: String,
) {
    ASSIST("A"),
    LEVEL_1("1"),
    LEVEL_2("2"),
    LEVEL_3("3"),
    LEVEL_4("4"),
    LEVEL_5("5"),
    LEVEL_6("6"),
    LEVEL_7("7"),
    LEVEL_8("8"),
    LEVEL_9("9"),
    ;

    val tickMillis: Long
        get() = if (this == ASSIST) ASSIST_TICK_MILLIS else LEVEL_1_TICK_MILLIS - (45L * (ordinal - 1) + 1) / 2

    val pointsPerOrb: Int
        get() = if (this == ASSIST) 1 else ordinal

    private companion object {
        const val ASSIST_TICK_MILLIS = 400L
        const val LEVEL_1_TICK_MILLIS = 250L
    }
}
