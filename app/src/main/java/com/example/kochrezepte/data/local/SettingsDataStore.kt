package com.example.kochrezepte.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "kochrezepte_settings")

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val USERNAME = stringPreferencesKey("username")
        val LANGUAGE = stringPreferencesKey("language") // "de", "tr", "en" ...
    }

    val userName: Flow<String> = context.dataStore.data.map { it[Keys.USERNAME] ?: "" }
    val language: Flow<String> = context.dataStore.data.map { it[Keys.LANGUAGE] ?: "de" }

    suspend fun setUserName(value: String) {
        context.dataStore.edit { it[Keys.USERNAME] = value }
    }

    suspend fun setLanguage(value: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = value }
    }
}
