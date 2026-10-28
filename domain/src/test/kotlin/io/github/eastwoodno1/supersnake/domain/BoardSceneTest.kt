package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BoardSceneTest {
    @Test
    fun c23SceneGlides() {
        val frame = sceneFrame(snake = listOf(Cell(11, 10)), previousSnake = listOf(Cell(10, 10)), progress = 0.5f)
        assertEquals(listOf(Point(10.5f, 10f)), BoardScene.of(frame, null, 0, false).snake)
    }

    @Test
    fun c21CrashMarksHead() {
        val lost = sceneFrame(snake = listOf(Cell(3, 4), Cell(2, 4)), status = GameStatus.LOST)
        assertEquals(Point(3f, 4f), BoardScene.of(lost, null, 0, false).crash)
        assertNull(BoardScene.of(sceneFrame(status = GameStatus.RUNNING), null, 0, false).crash)
    }

    @Test
    fun emptySceneHasNothingToDraw() {
        val scene = BoardScene.EMPTY
        assertEquals(emptyList(), scene.snake)
        assertEquals(emptyList(), scene.particles)
        assertNull(scene.orb)
        assertNull(scene.crash)
        assertEquals(1f, scene.orbBrightness)
    }

    @Test
    fun orbIsTheCellAsAPoint() = assertEquals(Point(5f, 5f), BoardScene.of(sceneFrame(), null, 0, false).orb)

    @Test
    fun noOrbNoPoint() = assertNull(BoardScene.of(sceneFrame(orb = null), null, 0, false).orb)

    @Test
    fun orbBrightnessFollowsThePulse() =
        assertEquals(Pulse.brightness(300, false), BoardScene.of(sceneFrame(), null, 300, false).orbBrightness)

    @Test
    fun noBurstNoParticles() = assertTrue(BoardScene.of(sceneFrame(), null, 100, false).particles.isEmpty())

    @Test
    fun liveBurstEmitsParticles() {
        val scene = BoardScene.of(sceneFrame(), Burst(Cell(5, 5), 0), 100, false)
        assertEquals(Particles.burst(Cell(5, 5), 0, 100, false), scene.particles)
        assertEquals(Particles.COUNT, scene.particles.size)
    }

    @Test
    fun reduceMotionStillOrbNoParticles() {
        val scene = BoardScene.of(sceneFrame(), Burst(Cell(5, 5), 0), 100, true)
        assertEquals(1f, scene.orbBrightness)
        assertEquals(emptyList(), scene.particles)
    }
}
