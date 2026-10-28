package io.github.eastwoodno1.supersnake.board

import io.github.eastwoodno1.supersnake.domain.BoardScene

class SceneHolder {
    @Volatile
    var scene: BoardScene = BoardScene(emptyList(), null, 1f, emptyList(), null)
}
