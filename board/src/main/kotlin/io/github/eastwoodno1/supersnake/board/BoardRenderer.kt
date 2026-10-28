package io.github.eastwoodno1.supersnake.board

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import io.github.eastwoodno1.supersnake.domain.Board
import io.github.eastwoodno1.supersnake.domain.BoardFrame
import io.github.eastwoodno1.supersnake.domain.BoardLayout
import io.github.eastwoodno1.supersnake.domain.NeonPalette
import io.github.eastwoodno1.supersnake.domain.Point

class BoardRenderer(
    private val holder: SceneHolder,
) : ApplicationAdapter() {
    private lateinit var shapes: ShapeRenderer
    private var frame = BoardFrame(0f, 0f, 0f)
    private val color = Color()

    override fun create() {
        shapes = ShapeRenderer()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        frame = BoardLayout.fit(width.toFloat(), height.toFloat())
        shapes.projectionMatrix.setToOrtho(0f, width.toFloat(), height.toFloat(), 0f, 0f, 1f)
    }

    override fun render() {
        val scene = holder.scene
        Color.argb8888ToColor(color, NeonPalette.BACKGROUND)
        Gdx.gl.glClearColor(color.r, color.g, color.b, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        shapes.begin(ShapeRenderer.ShapeType.Filled)
        drawWall()
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
        GLOW.forEach { (scale, alpha) ->
            scene.snake.forEach { square(it, SNAKE_SIZE * scale, NeonPalette.SNAKE, alpha) }
            scene.orb?.let { disc(it, ORB_RADIUS * scene.orbBrightness * scale, NeonPalette.ORB, alpha) }
        }
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        scene.orb?.let { disc(it, ORB_RADIUS * scene.orbBrightness, NeonPalette.ORB, 1f) }
        scene.snake.forEachIndexed { index, point ->
            square(point, SNAKE_SIZE, NeonPalette.SNAKE, 1f, if (index == 0) HEAD_LIFT else 0f)
        }
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
        scene.particles.forEach { disc(Point(it.x - HALF, it.y - HALF), PARTICLE_RADIUS, NeonPalette.ORB, it.alpha) }
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        scene.crash?.let { cross(it) }
        shapes.end()
    }

    override fun dispose() {
        shapes.dispose()
    }

    private fun tint(
        argb: Int,
        alpha: Float,
        lift: Float = 0f,
    ) {
        Color.argb8888ToColor(color, argb)
        color.lerp(Color.WHITE, lift)
        shapes.setColor(color.r, color.g, color.b, alpha)
    }

    private fun drawWall() {
        val side = frame.cellSize * Board.SIZE
        val thickness = frame.cellSize * WALL_THICKNESS
        tint(NeonPalette.WALL, 1f)
        shapes.rect(frame.left, frame.top, side, thickness)
        shapes.rect(frame.left, frame.top + side - thickness, side, thickness)
        shapes.rect(frame.left, frame.top, thickness, side)
        shapes.rect(frame.left + side - thickness, frame.top, thickness, side)
    }

    private fun square(
        at: Point,
        size: Float,
        argb: Int,
        alpha: Float,
        lift: Float = 0f,
    ) {
        tint(argb, alpha, lift)
        val half = frame.cellSize * size / 2
        val radius = half * ROUNDING
        val cx = frame.left + (at.x + HALF) * frame.cellSize
        val cy = frame.top + (at.y + HALF) * frame.cellSize
        shapes.rect(cx - half + radius, cy - half, 2 * (half - radius), 2 * half)
        shapes.rect(cx - half, cy - half + radius, 2 * half, 2 * (half - radius))
        shapes.circle(cx - half + radius, cy - half + radius, radius)
        shapes.circle(cx + half - radius, cy - half + radius, radius)
        shapes.circle(cx - half + radius, cy + half - radius, radius)
        shapes.circle(cx + half - radius, cy + half - radius, radius)
    }

    private fun disc(
        at: Point,
        radius: Float,
        argb: Int,
        alpha: Float,
    ) {
        tint(argb, alpha)
        shapes.circle(
            frame.left + (at.x + HALF) * frame.cellSize,
            frame.top + (at.y + HALF) * frame.cellSize,
            frame.cellSize * radius,
        )
    }

    private fun cross(at: Point) {
        tint(NeonPalette.TEXT, 1f)
        val cx = frame.left + (at.x + HALF) * frame.cellSize
        val cy = frame.top + (at.y + HALF) * frame.cellSize
        val reach = frame.cellSize * CROSS_REACH
        shapes.rectLine(cx - reach, cy - reach, cx + reach, cy + reach, frame.cellSize * CROSS_WIDTH)
        shapes.rectLine(cx - reach, cy + reach, cx + reach, cy - reach, frame.cellSize * CROSS_WIDTH)
    }

    private companion object {
        val GLOW = listOf(1.6f to 0.15f, 1.3f to 0.35f)
        const val SNAKE_SIZE = 0.8f
        const val ORB_RADIUS = 0.35f
        const val PARTICLE_RADIUS = 0.08f
        const val HEAD_LIFT = 0.2f
        const val HALF = 0.5f
        const val ROUNDING = 0.4f
        const val WALL_THICKNESS = 0.12f
        const val CROSS_REACH = 0.45f
        const val CROSS_WIDTH = 0.12f
    }
}
