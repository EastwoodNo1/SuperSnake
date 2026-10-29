package io.github.eastwoodno1.supersnake

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.eastwoodno1.supersnake.domain.NeonPalette
import io.github.eastwoodno1.supersnake.domain.Overlay
import io.github.eastwoodno1.supersnake.domain.Phrase
import io.github.eastwoodno1.supersnake.domain.Spoken
import kotlinx.coroutines.delay

class OverlayActions(
    val onResume: () -> Unit,
    val onRestart: () -> Unit,
    val onPlayAgain: () -> Unit,
    val onMainMenu: () -> Unit,
)

@Composable
fun Overlays(
    overlay: Overlay,
    countdown: Int?,
    score: Int,
    best: Int,
    spoken: Spoken?,
    actions: OverlayActions,
    modifier: Modifier = Modifier,
) {
    var go by remember { mutableStateOf(emptyList<Phrase>()) }
    LaunchedEffect(spoken) {
        val said = spoken?.lines.orEmpty().map { it.phrase }
        go = said.filter { it == Phrase.GO }
        delay(GO_MILLIS)
        go = emptyList()
    }
    Box(modifier.fillMaxSize()) {
        go.forEach { Big(stringResource(R.string.phrase_go), Modifier.align(Alignment.Center)) }
        when (overlay) {
            Overlay.NONE -> {}

            Overlay.COUNTDOWN -> {
                listOfNotNull(countdown).forEach { Big(stringResource(R.string.phrase_countdown, it), Modifier.align(Alignment.Center)) }
            }

            Overlay.PAUSED -> {
                Panel(stringResource(R.string.paused_title)) {
                    NeonButton(stringResource(R.string.resume), actions.onResume, Modifier.fillMaxWidth(), primary = true)
                    NeonButton(stringResource(R.string.restart), actions.onRestart, Modifier.fillMaxWidth())
                    NeonButton(stringResource(R.string.main_menu), actions.onMainMenu, Modifier.fillMaxWidth())
                }
            }

            Overlay.GAME_OVER -> {
                Ended(stringResource(R.string.game_over_title), score, best, actions)
            }

            Overlay.WON -> {
                Ended(stringResource(R.string.won_title), score, best, actions)
            }
        }
    }
}

@Composable
private fun Ended(
    title: String,
    score: Int,
    best: Int,
    actions: OverlayActions,
) {
    Panel(title) {
        Text(stringResource(R.string.final_score, score), color = Color(NeonPalette.TEXT), fontSize = 20.sp)
        Text(stringResource(R.string.best_score, best), color = Color(NeonPalette.TEXT), fontSize = 20.sp)
        NeonButton(stringResource(R.string.play_again), actions.onPlayAgain, Modifier.fillMaxWidth(), primary = true)
        NeonButton(stringResource(R.string.main_menu), actions.onMainMenu, Modifier.fillMaxWidth())
    }
}

@Composable
private fun Panel(
    title: String,
    content: @Composable () -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(NeonPalette.BACKGROUND).copy(alpha = SCRIM_ALPHA))
            .pointerInput(Unit) {},
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .fillMaxWidth(PANEL_WIDTH),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                title,
                Modifier.semantics { heading() },
                color = Color(NeonPalette.SNAKE),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            content()
        }
    }
}

@Composable
private fun Big(
    text: String,
    modifier: Modifier,
) = Text(text, modifier, color = Color(NeonPalette.TEXT), fontSize = 96.sp, fontWeight = FontWeight.ExtraBold)

private const val GO_MILLIS = 700L
private const val SCRIM_ALPHA = 0.85f
private const val PANEL_WIDTH = 0.85f
