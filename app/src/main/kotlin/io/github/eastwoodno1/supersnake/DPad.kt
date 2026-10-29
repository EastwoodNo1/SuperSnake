package io.github.eastwoodno1.supersnake

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.eastwoodno1.supersnake.domain.GameKey
import io.github.eastwoodno1.supersnake.domain.NeonPalette

private val KEY_SIZE = 64.dp
private val KEY_SHAPE = RoundedCornerShape(14.dp)

@Composable
fun DPad(
    onKey: (GameKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        DPadKey("▲", stringResource(R.string.dpad_up)) { onKey(GameKey.UP) }
        Row(horizontalArrangement = Arrangement.spacedBy(KEY_SIZE)) {
            DPadKey("◀", stringResource(R.string.dpad_left)) { onKey(GameKey.LEFT) }
            DPadKey("▶", stringResource(R.string.dpad_right)) { onKey(GameKey.RIGHT) }
        }
        DPadKey("▼", stringResource(R.string.dpad_down)) { onKey(GameKey.DOWN) }
    }
}

@Composable
private fun DPadKey(
    glyph: String,
    description: String,
    onClick: () -> Unit,
) {
    Box(
        Modifier
            .size(KEY_SIZE)
            .clip(KEY_SHAPE)
            .border(2.dp, Color(NeonPalette.ORB), KEY_SHAPE)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Text(glyph, color = Color(NeonPalette.TEXT), fontSize = 24.sp)
    }
}
