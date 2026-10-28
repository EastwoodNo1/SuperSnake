package io.github.eastwoodno1.supersnake.data

import androidx.datastore.core.DataStore
import io.github.eastwoodno1.supersnake.domain.HighScore
import io.github.eastwoodno1.supersnake.domain.HighScoreRepository
import io.github.eastwoodno1.supersnake.domain.HighScoreTable
import kotlinx.coroutines.flow.Flow

class DataStoreHighScoreRepository(
    private val store: DataStore<List<HighScore>>,
) : HighScoreRepository {
    override val entries: Flow<List<HighScore>> = store.data

    override suspend fun update(transform: (List<HighScore>) -> List<HighScore>) {
        store.updateData { HighScoreTable.normalize(transform(it)) }
    }
}
