package io.github.eastwoodno1.supersnake

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.badlogic.gdx.backends.android.AndroidFragmentApplication

class MainActivity :
    FragmentActivity(),
    AndroidFragmentApplication.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent { SuperSnakeApp((application as SuperSnakeApp).graph) }
    }

    override fun exit() = Unit
}
