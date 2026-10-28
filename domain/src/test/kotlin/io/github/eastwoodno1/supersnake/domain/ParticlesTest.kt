package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import kotlin.math.hypot
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ParticlesTest {
    @Test
    fun burstStartsAtCentre() {
        val burst = Particles.burst(Cell(4, 4), 0, 0, false)
        assertEquals(12, burst.size)
        burst.forEach { assertEquals(Particle(4.5f, 4.5f, 1f), it) }
    }

    @Test
    fun burstEasesOut() {
        val burst = Particles.burst(Cell(4, 4), 0, 200, false)
        burst.forEach {
            assertEquals(0.9f, hypot(it.x - 4.5f, it.y - 4.5f), 1e-4f)
            assertEquals(0.5f, it.alpha)
        }
        assertEquals(5.4f, burst.first().x, 1e-4f)
        assertEquals(4.5f, burst.first().y, 1e-4f)
        assertTrue(burst.map { it.x to it.y }.distinct().size == 12)
    }

    @Test
    fun burstEndsAndRespectsReduceMotion() {
        assertEquals(emptyList(), Particles.burst(Cell(4, 4), 0, 400, false))
        assertEquals(emptyList(), Particles.burst(Cell(4, 4), 0, -1, false))
        assertEquals(emptyList(), Particles.burst(Cell(4, 4), 0, 100, true))
    }
}
