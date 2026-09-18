package com.example.notegenie.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notegenie.data.AiPreferences
import com.example.notegenie.data.AiPreferencesRepository
import com.example.notegenie.data.Difficulty
import com.example.notegenie.data.NoteLanguage
import com.example.notegenie.data.NoteLength
import com.example.notegenie.data.NoteStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiPreferencesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AiPreferencesRepository(application.applicationContext)

    private val _preferences = MutableStateFlow(AiPreferences())
    val preferences: StateFlow<AiPreferences> = _preferences.asStateFlow()

    private val _saveConfirmed = MutableStateFlow(false)
    val saveConfirmed: StateFlow<Boolean> = _saveConfirmed.asStateFlow()

    init {
        // Load whatever was previously saved, as soon as this ViewModel is created.
        viewModelScope.launch {
            repository.preferencesFlow.collect { saved ->
                _preferences.value = saved
            }
        }
    }

    fun onNoteLengthChange(value: NoteLength) {
        _preferences.value = _preferences.value.copy(noteLength = value)
    }

    fun onDifficultyChange(value: Difficulty) {
        _preferences.value = _preferences.value.copy(difficulty = value)
    }

    fun onLanguageChange(value: NoteLanguage) {
        _preferences.value = _preferences.value.copy(language = value)
    }

    fun onNoteStyleChange(value: NoteStyle) {
        _preferences.value = _preferences.value.copy(noteStyle = value)
    }

    fun savePreferences() {
        viewModelScope.launch {
            repository.savePreferences(_preferences.value)
            _saveConfirmed.value = true
        }
    }

    fun resetSaveConfirmation() {
        _saveConfirmed.value = false
    }
}