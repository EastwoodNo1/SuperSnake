package io.github.eastwoodno1.supersnake.domain

import kotlin.math.max
import kotlin.math.min

data class BoardFrame(
    val left: Float,
    val top: Float,
    val cellSize: Float,
)

object BoardLayout {
    fun fit(
        width: Float,
        height: Float,
    ): BoardFrame {
        val side = max(0f, min(width, height))
        return BoardFrame(
            left = (max(0f, width) - side) / 2,
            top = (max(0f, height) - side) / 2,
            cellSize = side / Board.SIZE,
        )
    }
}
