package io.github.eastwoodno1.supersnake.domain

import java.time.LocalDate

class HoldableClock(
    private val base: Clock,
) : Clock {
    private var heldAt: Long? = null
    private var shift = 0L

    @Synchronized
    fun hold() {
        heldAt = heldAt ?: (base.nowMillis() - shift)
    }

    @Synchronized
    fun release() {
        heldAt?.let { shift = base.nowMillis() - it }
        heldAt = null
    }

    @Synchronized
    override fun nowMillis(): Long = heldAt ?: (base.nowMillis() - shift)

    override fun today(): LocalDate = base.today()
}
