package io.github.eastwoodno1.supersnake.data

import androidx.datastore.core.DataStore
import io.github.eastwoodno1.supersnake.domain.Settings
import io.github.eastwoodno1.supersnake.domain.SettingsRepository
import kotlinx.coroutines.flow.Flow

class DataStoreSettingsRepository(
    private val store: DataStore<Settings>,
) : SettingsRepository {
    override val settings: Flow<Settings> = store.data

    override suspend fun update(transform: (Settings) -> Settings) {
        store.updateData { transform(it) }
    }
}
