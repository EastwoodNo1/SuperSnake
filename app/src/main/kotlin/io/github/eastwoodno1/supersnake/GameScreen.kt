package io.github.eastwoodno1.supersnake

import android.view.Choreographer
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.fragment.compose.AndroidFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import io.github.eastwoodno1.supersnake.domain.BackAction
import io.github.eastwoodno1.supersnake.domain.ControlScheme
import io.github.eastwoodno1.supersnake.domain.GameFrame
import io.github.eastwoodno1.supersnake.domain.GameKey
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Composable
private fun <T> GameViewModel.select(select: (GameFrame) -> T): State<T> =
    remember(this) { frame.map(select).distinctUntilChanged() }.collectAsStateWithLifecycle(select(frame.value))

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val manager = (checkNotNull(LocalActivity.current) as FragmentActivity).supportFragmentManager
    val leave = {
        manager.fragments.filterIsInstance<BoardFragment>().forEach { manager.beginTransaction().detach(it).commitNow() }
        onLeave()
    }
    BackHandler { if (viewModel.back() == BackAction.LEAVE) leave() }
    val overlay by viewModel.overlay.collectAsStateWithLifecycle()
    val spoken by viewModel.spoken.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val best by viewModel.best.collectAsStateWithLifecycle()
    val score by viewModel.select { it.state.score }
    val level by viewModel.select { it.state.difficulty.label }
    val countdown by viewModel.select { it.countdown }
    val thresholdPx = with(LocalDensity.current) { 24.dp.toPx() }
    val scope = rememberCoroutineScope()
    val view = LocalView.current
    val beeps = remember { BeepPlayer() }
    val focus = remember { FocusRequester() }
    DisposableEffect(beeps) { onDispose(beeps::release) }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) { viewModel.pause() }
    LaunchedEffect(thresholdPx) { viewModel.swipeThreshold(thresholdPx) }
    LaunchedEffect(overlay) { focus.requestFocus() }
    LaunchedEffect(viewModel, beeps, view) {
        viewModel.feedback.collect { feedback ->
            feedback.sounds.forEach(beeps::play)
            feedback.haptics.forEach { view.performHapticFeedback(HAPTICS.getValue(it)) }
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (isActive) {
                awaitDisplayFrame()
                viewModel.advance()
            }
        }
    }
    val actions =
        OverlayActions(
            onResume = { viewModel.input(GameKey.PAUSE.input) },
            onRestart = { scope.launch { viewModel.newGame() } },
            onPlayAgain = { scope.launch { viewModel.newGame() } },
            onMainMenu = leave,
        )
    val dpad: @Composable () -> Unit = { DPad({ viewModel.input(it.input) }) }
    val showDpad = settings.controlScheme == ControlScheme.DPAD
    Box(
        modifier
            .fillMaxSize()
            .testTag("game")
            .onKeyEvent { it.type == KeyEventType.KeyDown && viewModel.key(it.key.nativeKeyCode) }
            .focusRequester(focus)
            .focusable(),
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val board: @Composable (Modifier) -> Unit = { boardModifier ->
                Box(boardModifier) {
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
                    Hud(score, level, { viewModel.input(GameKey.PAUSE.input) }, Modifier.align(Alignment.TopCenter))
                }
            }
            if (maxWidth > maxHeight) {
                Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                    board(Modifier.weight(1f).fillMaxSize())
                    if (showDpad) dpad()
                }
            } else {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    board(Modifier.weight(1f).fillMaxSize())
                    if (showDpad) dpad()
                }
            }
        }
        Overlays(overlay, countdown, score, best, spoken, actions)
        Announcer(spoken)
    }
}

private suspend fun awaitDisplayFrame() =
    suspendCancellableCoroutine { continuation ->
        val choreographer = Choreographer.getInstance()
        val callback = Choreographer.FrameCallback { continuation.resume(Unit) }
        choreographer.postFrameCallback(callback)
        continuation.invokeOnCancellation { choreographer.removeFrameCallback(callback) }
    }
