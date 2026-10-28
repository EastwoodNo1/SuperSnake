package io.github.eastwoodno1.supersnake

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import kotlinx.coroutines.launch

class MainActivity :
    FragmentActivity(),
    AndroidFragmentApplication.Callbacks {
    private val model: GameViewModel by viewModels { GameViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var playing by rememberSaveable { mutableStateOf(false) }
            if (playing) {
                GameScreen(model)
            } else {
                Button(
                    onClick = {
                        lifecycleScope.launch {
                            model.newGame()
                            playing = true
                        }
                    },
                ) {
                    Text(stringResource(R.string.play))
                }
            }
        }
    }

    override fun exit() = Unit
}
