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

    fun setUserName(value: String) {
        viewModelScope.launch { dataStore.setUserName(value) }
    }


}
