package io.github.eastwoodno1.supersnake

import android.app.Activity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun Activity.barsVisible(type: Int): Boolean? = ViewCompat.getRootWindowInsets(window.decorView)?.isVisible(type)

fun Activity.allBarsVisible(): Boolean =
    barsVisible(WindowInsetsCompat.Type.statusBars()) == true && barsVisible(WindowInsetsCompat.Type.navigationBars()) == true

fun Activity.allBarsHidden(): Boolean =
    barsVisible(WindowInsetsCompat.Type.statusBars()) == false && barsVisible(WindowInsetsCompat.Type.navigationBars()) == false

fun Activity.barsSummary(): String =
    "status=${barsVisible(WindowInsetsCompat.Type.statusBars())} navigation=${barsVisible(WindowInsetsCompat.Type.navigationBars())}"
