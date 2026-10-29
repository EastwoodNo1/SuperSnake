package io.github.eastwoodno1.supersnake

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.eastwoodno1.supersnake.domain.NeonPalette

@Composable
fun Hud(
    score: Int,
    level: String,
    onPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pause = stringResource(R.string.pause)
    Row(
        modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(stringResource(R.string.hud_score, score), color = Color(NeonPalette.TEXT), fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.hud_level, level), Modifier.weight(1f), color = Color(NeonPalette.TEXT))
        NeonButton("II", onPause, Modifier.semantics { contentDescription = pause })
    }
}
