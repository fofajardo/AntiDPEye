package com.fofajardo.antidpeye.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File

val Context.appSettingsDataStore: DataStore<AppSettings> by dataStore(
    fileName = "app_settings.json",
    serializer = AppSettingsSerializer,
)

class SettingsRepository(
    private val context: Context,
) {
    val settingsFlow: Flow<AppSettings> = context.appSettingsDataStore.data

    suspend fun getSettings(): AppSettings = context.appSettingsDataStore.data.first()

    fun getSettingsBlocking(): AppSettings = runBlocking { getSettings() }

    suspend fun update(transform: (AppSettings) -> AppSettings) {
        context.appSettingsDataStore.updateData(transform)
    }

    suspend fun exportJson(): String {
        val current = getSettings()
        return AppSettingsSerializer.json.encodeToString(AppSettings.serializer(), current)
    }

    suspend fun importJson(jsonString: String) {
        val imported = AppSettingsSerializer.json.decodeFromString<AppSettings>(jsonString)
        context.appSettingsDataStore.updateData { imported }
    }

    suspend fun resetToDefault() {
        context.appSettingsDataStore.updateData { AppSettings() }
    }
}
