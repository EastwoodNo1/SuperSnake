package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RandomSourceTest {
    @Test
    fun kotlinRandomSourceStaysInBound() {
        val source = KotlinRandomSource(Random(7))
        repeat(1000) { assertTrue(source.nextInt(5) in 0 until 5) }
        assertEquals(0, source.nextInt(1))
    }
}
