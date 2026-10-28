package io.github.eastwoodno1.supersnake.domain

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class Particle(
    val x: Float,
    val y: Float,
    val alpha: Float,
)

object Particles {
    const val COUNT = 12
    const val LIFETIME_MILLIS = 400L
    const val TRAVEL_CELLS = 1.2f

    fun burst(
        origin: Cell,
        startMillis: Long,
        nowMillis: Long,
        reduceMotion: Boolean,
    ): List<Particle> {
        val elapsed = nowMillis - startMillis
        if (reduceMotion || elapsed < 0 || elapsed >= LIFETIME_MILLIS) return emptyList()
        val t = elapsed.toFloat() / LIFETIME_MILLIS
        val distance = TRAVEL_CELLS * (1 - (1 - t) * (1 - t))
        return List(COUNT) { i ->
            val angle = 2 * PI * i / COUNT
            Particle(
                x = origin.x + 0.5f + (cos(angle) * distance).toFloat(),
                y = origin.y + 0.5f + (sin(angle) * distance).toFloat(),
                alpha = 1 - t,
            )
        }
    }
}
