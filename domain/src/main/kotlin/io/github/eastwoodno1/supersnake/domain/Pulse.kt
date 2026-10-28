package io.github.eastwoodno1.supersnake.domain

import kotlin.math.PI
import kotlin.math.sin

object Pulse {
    const val PERIOD_MILLIS = 1200L

    fun brightness(
        nowMillis: Long,
        reduceMotion: Boolean,
    ): Float =
        if (reduceMotion) {
            1f
        } else {
            (0.8 + 0.2 * sin(2 * PI * nowMillis / PERIOD_MILLIS)).toFloat()
        }
}
