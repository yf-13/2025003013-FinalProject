package com.example.studyflash.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "studyflash_preferences")

class UserPreferencesRepository(private val context: Context) {
    companion object {
        private val THEME_KEY = booleanPreferencesKey("dark_mode")
        private val LAST_GROUP_ID_KEY = longPreferencesKey("last_group_id")
    }

    val themeFlow: Flow<Boolean> = context.dataStore.data
        .map { it[THEME_KEY] ?: false }

    suspend fun saveTheme(isDark: Boolean) {
        context.dataStore.edit { it[THEME_KEY] = isDark }
    }

    val lastGroupIdFlow: Flow<Long> = context.dataStore.data
        .map { it[LAST_GROUP_ID_KEY] ?: 0L }

    suspend fun saveLastGroupId(groupId: Long) {
        context.dataStore.edit { it[LAST_GROUP_ID_KEY] = groupId }
    }
}