package io.github.eastwoodno1.supersnake.domain

import java.time.LocalDate

class FakeClock(
    var now: Long = 0,
    var date: LocalDate = LocalDate.of(2026, 10, 28),
) : Clock {
    override fun nowMillis(): Long = now

    override fun today(): LocalDate = date
}
