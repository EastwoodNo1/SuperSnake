package io.github.eastwoodno1.supersnake.domain

data class TickAdvance(
    val ticked: Boolean,
    val timer: TickTimer,
)

data class TickTimer(
    val intervalMillis: Long,
    val lastTickMillis: Long,
) {
    fun advance(nowMillis: Long): TickAdvance {
        val elapsed = nowMillis - lastTickMillis
        return when {
            elapsed < intervalMillis -> TickAdvance(false, this)
            elapsed < 2 * intervalMillis -> TickAdvance(true, copy(lastTickMillis = lastTickMillis + intervalMillis))
            else -> TickAdvance(true, copy(lastTickMillis = nowMillis))
        }
    }

    fun progress(nowMillis: Long): Float = ((nowMillis - lastTickMillis).toFloat() / intervalMillis).coerceIn(0f, 1f)
}
