package io.github.eastwoodno1.supersnake.domain

import kotlin.random.Random

fun interface RandomSource {
    fun nextInt(bound: Int): Int
}

class KotlinRandomSource(
    private val random: Random,
) : RandomSource {
    override fun nextInt(bound: Int): Int = random.nextInt(bound)
}
