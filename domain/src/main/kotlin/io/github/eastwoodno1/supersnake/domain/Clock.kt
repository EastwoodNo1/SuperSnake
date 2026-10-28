package io.github.eastwoodno1.supersnake.domain

import java.time.LocalDate

interface Clock {
    fun nowMillis(): Long

    fun today(): LocalDate
}
