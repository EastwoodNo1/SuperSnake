package io.github.eastwoodno1.supersnake

import io.github.eastwoodno1.supersnake.domain.Clock
import java.time.LocalDate

class AndroidClock : Clock {
    override fun nowMillis(): Long = android.os.SystemClock.elapsedRealtime()

    override fun today(): LocalDate = LocalDate.now()
}
