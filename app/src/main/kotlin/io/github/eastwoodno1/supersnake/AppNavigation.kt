package io.github.eastwoodno1.supersnake

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.eastwoodno1.supersnake.domain.BackStack
import io.github.eastwoodno1.supersnake.domain.NeonPalette
import io.github.eastwoodno1.supersnake.domain.Screen
import kotlinx.coroutines.launch

private fun backStackSaver(game: GameViewModel) =
    listSaver<BackStack, String>(
        save = { stack -> stack.screens.map { it.name } },
        restore = { names -> BackStack.restore(names.map(Screen::valueOf), game.live) },
    )

@Composable
fun SuperSnakeApp(graph: AppGraph) {
    val game = viewModel { GameViewModel(graph) }
    var stack by rememberSaveable(stateSaver = backStackSaver(game)) { mutableStateOf(BackStack()) }
    val scope = rememberCoroutineScope()
    BackHandler(enabled = stack.canPop) { stack = stack.pop() }
    NeonTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.radialGradient(listOf(Color(NeonPalette.SURFACE), Color(NeonPalette.BACKGROUND))))
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            when (stack.top) {
                Screen.MENU -> {
                    MenuScreen(
                        viewModel { MenuViewModel(graph) },
                        onPlay = {
                            scope.launch {
                                game.newGame()
                                stack = stack.push(Screen.GAME)
                            }
                        },
                        onHighScores = { stack = stack.push(Screen.HIGH_SCORES) },
                        onSettings = { stack = stack.push(Screen.SETTINGS) },
                    )
                }

                Screen.GAME -> {
                    GameScreen(game, onLeave = { stack = stack.pop() })
                }

                Screen.HIGH_SCORES -> {
                    HighScoresScreen(viewModel { HighScoresViewModel(graph) })
                }

                Screen.SETTINGS -> {
                    SettingsScreen(viewModel { SettingsViewModel(graph) })
                }
            }
        }
    }
}
