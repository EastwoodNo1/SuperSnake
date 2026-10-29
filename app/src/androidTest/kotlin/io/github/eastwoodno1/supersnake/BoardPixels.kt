package io.github.eastwoodno1.supersnake

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Rect
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import androidx.test.platform.app.InstrumentationRegistry
import io.github.eastwoodno1.supersnake.domain.BoardLayout
import io.github.eastwoodno1.supersnake.domain.Cell
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.math.abs

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

    fun countNear(
        cell: Cell,
        expected: Int,
        tolerance: Int,
    ): Int {
        val frame = BoardLayout.fit(bounds.width().toFloat(), bounds.height().toFloat())
        val left = (bounds.left + frame.left + cell.x * frame.cellSize).toInt()
        val top = (bounds.top + frame.top + cell.y * frame.cellSize).toInt()
        val size = frame.cellSize.toInt()
        return (top until top + size).sumOf { y ->
            (left until left + size).count { x -> close(shot.getPixel(x, y), expected, tolerance) }
        }
    }

    private fun close(
        actual: Int,
        expected: Int,
        tolerance: Int,
    ) = listOf(16, 8, 0).all { shift -> abs(((actual shr shift) and 0xFF) - ((expected shr shift) and 0xFF)) <= tolerance }

    companion object {
        fun capture(activity: Activity): BoardPixels {
            lateinit var surface: GLSurfaceView
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                surface = findBoard(activity.window.decorView) as GLSurfaceView
            }
            val bitmap = Bitmap.createBitmap(surface.width, surface.height, Bitmap.Config.ARGB_8888)
            val done = CountDownLatch(1)
            var status = PixelCopy.ERROR_UNKNOWN
            PixelCopy.request(
                surface,
                bitmap,
                {
                    status = it
                    done.countDown()
                },
                Handler(Looper.getMainLooper()),
            )
            check(done.await(COPY_TIMEOUT_SECONDS, TimeUnit.SECONDS)) { "pixel copy timed out" }
            check(status == PixelCopy.SUCCESS) { "pixel copy failed: $status" }
            return BoardPixels(bitmap, Rect(0, 0, bitmap.width, bitmap.height))
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

        private const val COPY_TIMEOUT_SECONDS = 5L
    }
}
