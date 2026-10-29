package io.github.eastwoodno1.supersnake

import android.app.Activity
import androidx.core.view.ViewCompat

fun Activity.barsVisible(type: Int): Boolean = checkNotNull(ViewCompat.getRootWindowInsets(window.decorView)).isVisible(type)
