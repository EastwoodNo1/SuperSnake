package io.github.eastwoodno1.supersnake.domain

import kotlin.math.pow

object NeonPalette {
    const val BACKGROUND = 0xFF07061A.toInt()
    const val SURFACE = 0xFF1B1446.toInt()
    const val SNAKE = 0xFF5EF2FF.toInt()
    const val ORB = 0xFFFF4FD8.toInt()
    const val WALL = 0xFF2266CC.toInt()
    const val TEXT = 0xFFEAFCFF.toInt()
    const val ON_PRIMARY = 0xFF1A0A24.toInt()
}

object Contrast {
    private const val LINEAR_THRESHOLD = 0.04045
    private const val LINEAR_DIVISOR = 12.92
    private const val GAMMA_OFFSET = 0.055
    private const val GAMMA_SCALE = 1.055
    private const val GAMMA = 2.4
    private const val FLARE = 0.05

    fun luminance(argb: Int): Double {
        val r = linear((argb shr 16) and 0xFF)
        val g = linear((argb shr 8) and 0xFF)
        val b = linear(argb and 0xFF)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    fun ratio(
        a: Int,
        b: Int,
    ): Double {
        val la = luminance(a)
        val lb = luminance(b)
        return (maxOf(la, lb) + FLARE) / (minOf(la, lb) + FLARE)
    }

    private fun linear(channel: Int): Double {
        val c = channel / 255.0
        return if (c <= LINEAR_THRESHOLD) c / LINEAR_DIVISOR else ((c + GAMMA_OFFSET) / GAMMA_SCALE).pow(GAMMA)
    }
}
