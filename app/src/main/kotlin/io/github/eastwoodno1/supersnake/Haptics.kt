package io.github.eastwoodno1.supersnake

import android.view.HapticFeedbackConstants
import io.github.eastwoodno1.supersnake.domain.Haptic

val HAPTICS: Map<Haptic, Int> =
    mapOf(
        Haptic.TICK to HapticFeedbackConstants.CLOCK_TICK,
        Haptic.REJECT to HapticFeedbackConstants.REJECT,
        Haptic.CONFIRM to HapticFeedbackConstants.CONFIRM,
    )
