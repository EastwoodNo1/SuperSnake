package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PulseTest {
    @Test
    fun pulseRangeAndPeriod() {
        assertEquals(0.8f, Pulse.brightness(0, false))
        assertEquals(1.0f, Pulse.brightness(300, false), 1e-4f)
        assertEquals(0.6f, Pulse.brightness(900, false), 1e-4f)
        assertEquals(Pulse.brightness(0, false), Pulse.brightness(1200, false), 1e-4f)
    }

    @Test
    fun pulseStaticWithReduceMotion() = assertEquals(1f, Pulse.brightness(300, true))
}
