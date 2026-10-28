package io.github.eastwoodno1.supersnake.domain

data class Burst(
    val cell: Cell,
    val startMillis: Long,
) {
    companion object {
        fun next(
            previous: Burst?,
            frame: GameFrame,
            nowMillis: Long,
        ): Burst? = if (GameEvent.ORB_EATEN in frame.events) Burst(frame.state.snake.first(), nowMillis) else previous
    }
}

data class BoardScene(
    val snake: List<Point>,
    val orb: Point?,
    val orbBrightness: Float,
    val particles: List<Particle>,
    val crash: Point?,
) {
    companion object {
        fun of(
            frame: GameFrame,
            burst: Burst?,
            nowMillis: Long,
            reduceMotion: Boolean,
        ): BoardScene {
            val state = frame.state
            return BoardScene(
                snake = SnakeMotion.interpolate(frame.previousSnake, state.snake, frame.progress),
                orb = state.orb?.let(::point),
                orbBrightness = Pulse.brightness(nowMillis, reduceMotion),
                particles = if (burst == null) emptyList() else Particles.burst(burst.cell, burst.startMillis, nowMillis, reduceMotion),
                crash = if (state.status == GameStatus.LOST) point(state.snake.first()) else null,
            )
        }

        private fun point(cell: Cell) = Point(cell.x.toFloat(), cell.y.toFloat())
    }
}

object Motion {
    fun reduced(animatorDurationScale: Float): Boolean = animatorDurationScale == 0f
}
