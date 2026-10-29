package io.github.eastwoodno1.supersnake

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import io.github.eastwoodno1.supersnake.board.BoardRenderer

class BoardFragment : AndroidFragmentApplication() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val model = ViewModelProvider(requireActivity(), GameViewModel.Factory)[GameViewModel::class.java]
        return initializeForView(BoardRenderer(model.scene), AndroidApplicationConfiguration().apply { useImmersiveMode = false })
    }
}
