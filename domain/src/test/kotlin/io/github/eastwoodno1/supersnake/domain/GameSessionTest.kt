package io.github.eastwoodno1.supersnake.domain

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameSessionTest {
    private val clock = FakeClock(now = 0)
    private val repository = FakeHighScoreRepository()
    private val record = RecordHighScore(repository, clock)

    private fun session(vararg draws: Int) = GameSession(Difficulty.LEVEL_1, clock, FixedRandom(*draws), record)

    private fun internalSession(state: GameState) = GameSession(state, clock, FixedRandom(), record)

    private suspend fun GameSession.skipCountdown() {
        clock.now += 3000
        advance()
    }

    private fun state(
        orbsEaten: Int = 0,
        orb: Cell? = Cell(11, 10),
        status: GameStatus = GameStatus.RUNNING,
        snake: List<Cell> = listOf(Cell(10, 10), Cell(9, 10), Cell(8, 10)),
        direction: Direction = Direction.RIGHT,
    ) = GameState(snake, direction, emptyList(), orb, 0, orbsEaten, Difficulty.LEVEL_1, status)

    private suspend fun GameSession.runUntilLost(): GameFrame {
        while (frame.state.status != GameStatus.LOST) {
            clock.now += 250
            advance()
        }
        return frame
    }

    @Test
    fun c13StartsCountingDown() {
        val frame = session(0).frame
        assertEquals(GameStatus.COUNTDOWN, frame.state.status)
        assertEquals(3, frame.countdown)
        assertEquals(listOf<Announcement>(Announcement.Countdown(3)), frame.announcements)
        assertEquals(frame.state.snake, frame.previousSnake)
    }

    @Test
    fun c13c25CountsDownThenGo() =
        runTest {
            val session = session(0)
            val expected =
                listOf(
                    999L to emptyList<Announcement>(),
                    1000L to listOf(Announcement.Countdown(2)),
                    2000L to listOf(Announcement.Countdown(1)),
                    2999L to emptyList(),
                    3000L to listOf(Announcement.Go),
                )
            expected.forEach { (now, announcements) ->
                clock.now = now
                assertEquals(announcements, session.advance().announcements, "at $now")
            }
            assertEquals(GameStatus.RUNNING, session.frame.state.status)
            assertNull(session.frame.countdown)
            assertEquals(
                Cell(10, 10),
                session.frame.state.snake
                    .first(),
            )
        }

    @Test
    fun c13FirstStepOneIntervalAfterGo() =
        runTest {
            val session = session(0)
            session.skipCountdown()
            clock.now = 3249
            assertEquals(
                Cell(10, 10),
                session
                    .advance()
                    .state.snake
                    .first(),
            )
            clock.now = 3250
            val frame = session.advance()
            assertEquals(Cell(11, 10), frame.state.snake.first())
            assertEquals(Cell(10, 10), frame.previousSnake.first())
        }

    @Test
    fun c14TurnDuringCountdownUsedFirst() =
        runTest {
            val session = session(0)
            session.input(GameInput.Turn(Direction.UP))
            session.skipCountdown()
            clock.now = 3250
            assertEquals(
                Cell(10, 9),
                session
                    .advance()
                    .state.snake
                    .first(),
            )
        }

    @Test
    fun stallDuringCountdownJumpsToGo() =
        runTest {
            val session = session(0)
            clock.now = 3500
            val frame = session.advance()
            assertEquals(listOf<Announcement>(Announcement.Go), frame.announcements)
            assertEquals(GameStatus.RUNNING, frame.state.status)
        }

    @Test
    fun negativeElapsedShowsThree() =
        runTest {
            val session = session(0)
            clock.now = -500
            val frame = session.advance()
            assertEquals(3, frame.countdown)
            assertEquals(emptyList(), frame.announcements)
        }

    @Test
    fun c23ProgressBetweenSteps() =
        runTest {
            val session = session(0)
            session.skipCountdown()
            clock.now = 3125
            assertEquals(0.5f, session.advance().progress)
        }

    @Test
    fun c20PauseDuringCountdownRestartsIt() =
        runTest {
            val session = session(0)
            session.input(GameInput.Turn(Direction.UP))
            clock.now = 1500
            val paused = session.pause()
            assertEquals(GameStatus.PAUSED, paused.state.status)
            assertEquals(listOf<Announcement>(Announcement.Paused), paused.announcements)
            assertEquals(emptyList(), paused.state.pendingTurns)
            clock.now = 9000
            val resumed = session.input(GameInput.TogglePause)
            assertEquals(GameStatus.COUNTDOWN, resumed.state.status)
            assertEquals(listOf<Announcement>(Announcement.Countdown(3)), resumed.announcements)
            clock.now = 11999
            assertEquals(GameStatus.COUNTDOWN, session.advance().state.status)
            clock.now = 12000
            assertEquals(listOf<Announcement>(Announcement.Go), session.advance().announcements)
        }

    @Test
    fun c20ResumeRunsCountdownBeforeMoving() =
        runTest {
            val session = session(0)
            session.skipCountdown()
            session.pause()
            clock.now = 5000
            session.input(GameInput.TogglePause)
            clock.now = 7999
            assertEquals(
                Cell(10, 10),
                session
                    .advance()
                    .state.snake
                    .first(),
            )
            clock.now = 8000
            assertEquals(listOf<Announcement>(Announcement.Go), session.advance().announcements)
            clock.now = 8250
            assertEquals(
                Cell(11, 10),
                session
                    .advance()
                    .state.snake
                    .first(),
            )
        }

    @Test
    fun pausedFrameIsStill() =
        runTest {
            val session = session(0)
            session.skipCountdown()
            clock.now = 3125
            session.advance()
            val frame = session.pause()
            assertEquals(frame.state.snake, frame.previousSnake)
            assertEquals(0f, frame.progress)
            clock.now = 4000
            assertEquals(0f, session.advance().progress)
        }

    @Test
    fun pauseIsIdempotent() {
        val session = session(0)
        session.pause()
        assertEquals(emptyList(), session.pause().announcements)
        assertEquals(emptyList(), session.input(GameInput.Turn(Direction.UP)).state.pendingTurns)
    }

    @Test
    fun everyTenthOrbIsAnnounced() =
        runTest {
            listOf(9 to listOf<Announcement>(Announcement.OrbMilestone(10)), 10 to emptyList<Announcement>()).forEach { (eaten, expected) ->
                val session = GameSession(state(orbsEaten = eaten), clock, FixedRandom(0), record)
                clock.now += 250
                val frame = session.advance()
                assertEquals(listOf(GameEvent.ORB_EATEN), frame.events)
                assertEquals(expected, frame.announcements)
            }
        }

    @Test
    fun internalRunningStartStepsOneIntervalLater() =
        runTest {
            val session = internalSession(state(orb = Cell(0, 0)))
            assertEquals(emptyList(), session.frame.announcements)
            assertNull(session.frame.countdown)
            clock.now = 249
            assertEquals(
                Cell(10, 10),
                session
                    .advance()
                    .state.snake
                    .first(),
            )
            clock.now = 250
            assertEquals(
                Cell(11, 10),
                session
                    .advance()
                    .state.snake
                    .first(),
            )
        }

    @Test
    fun c22GameOverSavesAndAnnounces() =
        runTest {
            val session = session(208, 0)
            session.skipCountdown()
            val frame = session.runUntilLost()
            assertEquals(GameStatus.LOST, frame.state.status)
            assertEquals(listOf(Announcement.GameOver(1), Announcement.NewHighScore(1)), frame.announcements)
            assertEquals(ScoreResult(1, true), frame.result)
            assertEquals(1, repository.current.size)
            assertEquals(1, repository.current.first().score)
        }

    @Test
    fun c21CrashFrameDoesNotGlide() =
        runTest {
            val session = session(208, 0)
            session.skipCountdown()
            val frame = session.runUntilLost()
            assertEquals(frame.state.snake, frame.previousSnake)
            assertEquals(1f, frame.progress)
        }

    @Test
    fun c22ZeroScoreNotSaved() =
        runTest {
            val session = session(0)
            session.skipCountdown()
            val frame = session.runUntilLost()
            assertEquals(listOf<Announcement>(Announcement.GameOver(0)), frame.announcements)
            assertEquals(ScoreResult(null, false), frame.result)
            assertTrue(repository.current.isEmpty())
        }

    @Test
    fun c9c22WinSavesAndAnnounces() =
        runTest {
            val session = internalSession(state(snake = almostFullSnake(), direction = Direction.LEFT, orb = Cell(0, 19)))
            clock.now = 250
            val frame = session.advance()
            assertEquals(GameStatus.WON, frame.state.status)
            assertEquals(frame.state.snake, frame.previousSnake)
            assertEquals(1f, frame.progress)
            val score = frame.state.score
            assertEquals(listOf(Announcement.Won(score), Announcement.NewHighScore(score)), frame.announcements)
            assertEquals(1, repository.current.size)
            clock.now = 500
            assertEquals(frame.state, session.advance().state)
            assertEquals(emptyList(), session.frame.announcements)
        }

    @Test
    fun c20TogglePauseFromRunningPauses() =
        runTest {
            val session = session(0)
            session.skipCountdown()
            val frame = session.input(GameInput.TogglePause)
            assertEquals(GameStatus.PAUSED, frame.state.status)
            assertEquals(listOf<Announcement>(Announcement.Paused), frame.announcements)
        }

    @Test
    fun afterTheEndNothingChanges() =
        runTest {
            val session = session(0)
            session.skipCountdown()
            val lost = session.runUntilLost()
            clock.now += 250
            listOf(session.advance(), session.input(GameInput.Turn(Direction.UP)), session.pause()).forEach { frame ->
                assertEquals(lost.state, frame.state)
                assertEquals(emptyList(), frame.events)
                assertEquals(emptyList(), frame.announcements)
                assertEquals(lost.result, frame.result)
            }
        }

    private class GatedRepository(
        private val gate: CompletableDeferred<Unit>? = null,
        private val failure: IOException? = null,
    ) : HighScoreRepository {
        private val delegate = FakeHighScoreRepository()
        var updates = 0
            private set

        override val entries = delegate.entries

        override suspend fun update(transform: (List<HighScore>) -> List<HighScore>) {
            updates++
            failure?.let { throw it }
            gate?.await()
            delegate.update(transform)
        }
    }

    private fun sessionOver(repository: HighScoreRepository) =
        GameSession(Difficulty.LEVEL_1, clock, FixedRandom(208, 0), RecordHighScore(repository, clock))

    @Test
    fun b2SkippedNumberIsNotAnnounced() =
        runTest {
            val session = session(0)
            clock.now = 2500
            val frame = session.advance()
            assertEquals(listOf<Announcement>(Announcement.Countdown(1)), frame.announcements)
            assertEquals(1, frame.countdown)
        }

    @Test
    fun c22SaveFailureStillEndsGame() =
        runTest {
            val repository = GatedRepository(failure = IOException("disk"))
            val session = sessionOver(repository)
            session.skipCountdown()
            val frame = session.runUntilLost()
            assertEquals(GameStatus.LOST, frame.state.status)
            assertEquals(listOf<Announcement>(Announcement.GameOver(1)), frame.announcements)
            assertEquals(ScoreResult(null, false), frame.result)
        }

    @Test
    fun c22EndedBeforeSaveCompletes() =
        runTest {
            val gate = CompletableDeferred<Unit>()
            val repository = GatedRepository(gate)
            val session = sessionOver(repository)
            session.skipCountdown()
            val saving =
                async(start = CoroutineStart.UNDISPATCHED) {
                    while (session.frame.state.status == GameStatus.RUNNING) {
                        clock.now += 250
                        session.advance()
                    }
                }
            val ended = session.frame
            assertEquals(GameStatus.LOST, ended.state.status)
            assertEquals(1, repository.updates)
            assertNull(ended.result)
            assertEquals(ended.state, session.pause().state)
            assertEquals(ended.state, session.input(GameInput.Turn(Direction.UP)).state)
            gate.complete(Unit)
            saving.await()
            assertEquals(ScoreResult(1, true), session.frame.result)
            assertEquals(listOf(Announcement.GameOver(1), Announcement.NewHighScore(1)), session.frame.announcements)
            assertEquals(1, repository.updates)
        }

    @Test
    fun c22CancelledSaveDoesNotStepAgain() =
        runTest {
            val gate = CompletableDeferred<Unit>()
            val repository = GatedRepository(gate)
            val session = sessionOver(repository)
            session.skipCountdown()
            val saving =
                async(start = CoroutineStart.UNDISPATCHED) {
                    while (true) {
                        clock.now += 250
                        session.advance()
                    }
                }
            saving.cancel()
            val ended = session.frame
            assertEquals(GameStatus.LOST, ended.state.status)
            clock.now += 250
            assertEquals(ended.state, session.advance().state)
            assertEquals(1, repository.updates)
        }
}
