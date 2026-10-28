package io.github.eastwoodno1.supersnake.domain

sealed interface Announcement {
    data class Countdown(
        val number: Int,
    ) : Announcement

    data object Go : Announcement

    data object Paused : Announcement

    data class OrbMilestone(
        val orbs: Int,
    ) : Announcement

    data class GameOver(
        val score: Int,
    ) : Announcement

    data class Won(
        val score: Int,
    ) : Announcement

    data class NewHighScore(
        val score: Int,
    ) : Announcement
}
