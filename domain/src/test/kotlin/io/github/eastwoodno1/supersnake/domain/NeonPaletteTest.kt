package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NeonPaletteTest {
    private fun assertRatio(
        expected: Double,
        minimum: Double,
        a: Int,
        b: Int,
    ) {
        val actual = Contrast.ratio(a, b)
        assertTrue(actual >= minimum, "ratio $actual is below $minimum")
        assertEquals(expected, actual, 0.01)
    }

    @Test
    fun contrastWhiteOnBlackIs21() = assertEquals(21.0, Contrast.ratio(0xFFFFFFFF.toInt(), 0xFF000000.toInt()), 1e-9)

    @Test
    fun contrastIsSymmetric() =
        assertEquals(
            Contrast.ratio(NeonPalette.SNAKE, NeonPalette.ORB),
            Contrast.ratio(NeonPalette.ORB, NeonPalette.SNAKE),
        )

    @Test
    fun contrastIgnoresAlpha() = assertEquals(Contrast.luminance(0xFF5EF2FF.toInt()), Contrast.luminance(0x005EF2FF))

    @Test
    fun textMeetsAA() {
        assertRatio(18.92, 4.5, NeonPalette.TEXT, NeonPalette.BACKGROUND)
        assertRatio(16.08, 4.5, NeonPalette.TEXT, NeonPalette.SURFACE)
        assertRatio(14.88, 4.5, NeonPalette.SNAKE, NeonPalette.BACKGROUND)
        assertRatio(6.62, 4.5, NeonPalette.ON_PRIMARY, NeonPalette.ORB)
    }

    @Test
    fun b10BoardShapesDifferInBrightness() {
        listOf(NeonPalette.SNAKE, NeonPalette.ORB, NeonPalette.WALL).forEach {
            assertTrue(Contrast.ratio(it, NeonPalette.BACKGROUND) >= 3.0)
        }
        assertRatio(2.12, 1.5, NeonPalette.SNAKE, NeonPalette.ORB)
        assertRatio(1.92, 1.5, NeonPalette.ORB, NeonPalette.WALL)
        assertRatio(4.07, 1.5, NeonPalette.SNAKE, NeonPalette.WALL)
    }
}
