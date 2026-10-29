package io.github.eastwoodno1.supersnake

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.eastwoodno1.supersnake.domain.NeonPalette

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun MenuScreen(
    viewModel: MenuViewModel,
    onPlay: () -> Unit,
    onHighScores: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val best by viewModel.best.collectAsStateWithLifecycle()
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (viewModel.reducedMotion) {
            Image(painterResource(R.drawable.ic_launcher_foreground), null, Modifier.size(ICON_SIZE))
        } else {
            val image = AnimatedImageVector.animatedVectorResource(R.drawable.ic_snake_animated)
            var atEnd by remember { mutableStateOf(false) }
            LaunchedEffect(image) { atEnd = true }
            Image(rememberAnimatedVectorPainter(image, atEnd), null, Modifier.size(ICON_SIZE))
        }
        Text(
            stringResource(R.string.app_name).uppercase(),
            Modifier.semantics { heading() },
            color = Color(NeonPalette.SNAKE),
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
        )
        NeonButton(stringResource(R.string.play), onPlay, Modifier.fillMaxWidth(BUTTON_WIDTH), primary = true)
        NeonButton(stringResource(R.string.high_scores), onHighScores, Modifier.fillMaxWidth(BUTTON_WIDTH))
        NeonButton(stringResource(R.string.settings), onSettings, Modifier.fillMaxWidth(BUTTON_WIDTH))
        Text(stringResource(R.string.best_score, best), color = Color(NeonPalette.TEXT))
    }
}

private val ICON_SIZE = 160.dp
private const val BUTTON_WIDTH = 0.85f
