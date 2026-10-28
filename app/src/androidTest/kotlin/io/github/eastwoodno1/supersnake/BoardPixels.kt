package io.github.eastwoodno1.supersnake

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Rect
import android.opengl.GLSurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.test.platform.app.InstrumentationRegistry
import io.github.eastwoodno1.supersnake.domain.BoardLayout
import io.github.eastwoodno1.supersnake.domain.Cell

class BoardPixels private constructor(
    private val shot: Bitmap,
    private val bounds: Rect,
) {
    fun at(cell: Cell): Int {
        val frame = BoardLayout.fit(bounds.width().toFloat(), bounds.height().toFloat())
        val x = bounds.left + frame.left + (cell.x + 0.5f) * frame.cellSize
        val y = bounds.top + frame.top + (cell.y + 0.5f) * frame.cellSize
        return shot.getPixel(x.toInt(), y.toInt())
    }

    companion object {
        fun capture(activity: Activity): BoardPixels {
            val bounds = Rect()
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                val view = findBoard(activity.window.decorView)
                val origin = IntArray(2)
                view.getLocationOnScreen(origin)
                bounds.set(origin[0], origin[1], origin[0] + view.width, origin[1] + view.height)
            }
            val raw = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
            return BoardPixels(raw.copy(Bitmap.Config.ARGB_8888, false), bounds)
        }

        private fun findBoard(view: View): View {
            if (view is GLSurfaceView) return view
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    val found = runCatching { findBoard(view.getChildAt(i)) }.getOrNull()
                    if (found != null) return found
                }
            }
            error("no GLSurfaceView in the view tree")
        }
    }
}
