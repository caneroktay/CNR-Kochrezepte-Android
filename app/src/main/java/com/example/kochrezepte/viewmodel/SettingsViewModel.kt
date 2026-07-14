package com.example.kochrezepte.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kochrezepte.data.local.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = SettingsDataStore(application)

    val userName = dataStore.userName.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val language = dataStore.language.stateIn(viewModelScope, SharingStarted.Eagerly, "de")

    fun setUserName(value: String) {
        viewModelScope.launch { dataStore.setUserName(value) }
    }

    fun setLanguage(value: String) {
        viewModelScope.launch { dataStore.setLanguage(value) }
    }
}
