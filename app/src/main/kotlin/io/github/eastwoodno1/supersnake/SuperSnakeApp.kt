package io.github.eastwoodno1.supersnake

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class SuperSnakeApp : Application() {
    lateinit var graph: AppGraph
        private set

    override fun onCreate() {
        super.onCreate()
        graph = AppGraph(this, CoroutineScope(SupervisorJob() + Dispatchers.IO))
    }
}
