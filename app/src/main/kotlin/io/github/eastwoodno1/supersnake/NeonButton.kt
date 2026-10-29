package io.github.eastwoodno1.supersnake

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.eastwoodno1.supersnake.domain.NeonPalette

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
) {
    var focused by remember { mutableStateOf(false) }
    val orb = Color(NeonPalette.ORB)
    val label = Color(NeonPalette.TEXT)
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp).onFocusChanged { focused = it.hasFocus },
        shape = RoundedCornerShape(14.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = if (primary) orb else Color.Transparent,
                contentColor = if (primary) Color(NeonPalette.ON_PRIMARY) else label,
            ),
        border = if (focused) BorderStroke(3.dp, label) else BorderStroke(2.dp, orb),
    ) {
        Text(text)
    }
}
