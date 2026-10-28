package io.github.eastwoodno1.supersnake.domain

class FixedRandom(
    vararg values: Int,
) : RandomSource {
    private val remaining = values.toMutableList()

    override fun nextInt(bound: Int): Int = if (remaining.isEmpty()) error("unexpected random draw") else remaining.removeAt(0)
}
