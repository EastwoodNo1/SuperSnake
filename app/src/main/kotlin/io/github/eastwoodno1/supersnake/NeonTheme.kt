package io.github.eastwoodno1.supersnake

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.eastwoodno1.supersnake.domain.NeonPalette

private val NeonColors =
    darkColorScheme(
        primary = Color(NeonPalette.ORB),
        onPrimary = Color(NeonPalette.ON_PRIMARY),
        secondary = Color(NeonPalette.SNAKE),
        onSecondary = Color(NeonPalette.ON_PRIMARY),
        background = Color(NeonPalette.BACKGROUND),
        onBackground = Color(NeonPalette.TEXT),
        surface = Color(NeonPalette.SURFACE),
        onSurface = Color(NeonPalette.TEXT),
        surfaceVariant = Color(NeonPalette.SURFACE),
        onSurfaceVariant = Color(NeonPalette.TEXT),
        outline = Color(NeonPalette.ORB),
    )

@Composable
fun NeonTheme(content: @Composable () -> Unit) = MaterialTheme(colorScheme = NeonColors, content = content)
