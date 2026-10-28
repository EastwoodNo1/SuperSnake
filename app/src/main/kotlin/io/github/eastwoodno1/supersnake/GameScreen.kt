package io.github.eastwoodno1.supersnake

import android.view.Choreographer
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.compose.AndroidFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import io.github.eastwoodno1.supersnake.domain.NeonPalette
import io.github.eastwoodno1.supersnake.domain.Overlay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier,
) {
    val overlay by remember(viewModel) { viewModel.frame.map { Overlay.of(it) }.distinctUntilChanged() }
        .collectAsStateWithLifecycle(Overlay.NONE)
    val countdown by remember(viewModel) { viewModel.frame.map { it.countdown.toString() }.distinctUntilChanged() }
        .collectAsStateWithLifecycle("")
    val thresholdPx = with(LocalDensity.current) { 24.dp.toPx() }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) { viewModel.pause() }
    LaunchedEffect(thresholdPx) { viewModel.swipeThreshold(thresholdPx) }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (isActive) {
                awaitDisplayFrame()
                viewModel.advance()
            }
        }
    }
    Box(modifier.fillMaxSize()) {
        AndroidFragment<BoardFragment>(Modifier.fillMaxSize())
        Box(
            Modifier
                .fillMaxSize()
                .testTag("board")
                .pointerInput(viewModel) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val id = down.id.value.toInt()
                        viewModel.swipeDown(id, down.position.x, down.position.y)
                        drag(down.id) { change ->
                            viewModel.swipeMove(id, change.position.x, change.position.y)
                            change.consume()
                        }
                        viewModel.swipeUp(id)
                    }
                },
        )
        if (overlay == Overlay.COUNTDOWN) {
            Text(stringResource(R.string.countdown_number, countdown), Modifier.align(Alignment.Center), color = Color(NeonPalette.TEXT))
        }
    }
}

private suspend fun awaitDisplayFrame() =
    suspendCancellableCoroutine { continuation ->
        val choreographer = Choreographer.getInstance()
        val callback = Choreographer.FrameCallback { continuation.resume(Unit) }
        choreographer.postFrameCallback(callback)
        continuation.invokeOnCancellation { choreographer.removeFrameCallback(callback) }
    }
