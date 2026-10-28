package io.github.eastwoodno1.supersnake.domain

import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class HoldableClockTest {
    private val base = FakeClock(now = 1_000)
    private val clock = HoldableClock(base)

    @Test
    fun followsBaseWhenNeverHeld() {
        base.now = 1_750
        assertEquals(1_750, clock.nowMillis())
    }

    @Test
    fun timeIsFrozenWhileHeld() {
        base.now = 2_000
        clock.hold()
        base.now = 9_000
        assertEquals(2_000, clock.nowMillis())
    }

    @Test
    fun releaseResumesFromHeldTimeWithoutJump() {
        base.now = 2_000
        clock.hold()
        base.now = 9_000
        clock.release()
        assertEquals(2_000, clock.nowMillis())
        base.now = 9_500
        assertEquals(2_500, clock.nowMillis())
    }

    @Test
    fun secondHoldKeepsTheFirstHeldTime() {
        base.now = 2_000
        clock.hold()
        base.now = 3_000
        clock.hold()
        assertEquals(2_000, clock.nowMillis())
        base.now = 4_000
        clock.release()
        assertEquals(2_000, clock.nowMillis())
    }

    @Test
    fun releaseWithoutHoldChangesNothing() {
        base.now = 2_000
        clock.release()
        assertEquals(2_000, clock.nowMillis())
        base.now = 2_300
        clock.release()
        assertEquals(2_300, clock.nowMillis())
    }

    @Test
    fun repeatedCyclesAccumulateShift() {
        base.now = 2_000
        clock.hold()
        base.now = 3_000
        clock.release()
        assertEquals(2_000, clock.nowMillis())
        base.now = 3_400
        assertEquals(2_400, clock.nowMillis())
        clock.hold()
        base.now = 5_000
        assertEquals(2_400, clock.nowMillis())
        clock.release()
        assertEquals(2_400, clock.nowMillis())
        base.now = 5_100
        assertEquals(2_500, clock.nowMillis())
    }

    @Test
    fun todayDelegatesToBase() {
        base.date = LocalDate.of(2027, 1, 2)
        clock.hold()
        assertEquals(LocalDate.of(2027, 1, 2), clock.today())
    }
}
