package com.example.notegenie.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.aiPreferencesDataStore by preferencesDataStore(name = "ai_preferences")

class AiPreferencesRepository(private val context: Context) {

    private object Keys {
        val NOTE_LENGTH = stringPreferencesKey("note_length")
        val DIFFICULTY = stringPreferencesKey("difficulty")
        val LANGUAGE = stringPreferencesKey("language")
        val NOTE_STYLE = stringPreferencesKey("note_style")
    }

    val preferencesFlow: Flow<AiPreferences> = context.aiPreferencesDataStore.data.map { prefs ->
        AiPreferences(
            noteLength = prefs[Keys.NOTE_LENGTH]?.let { runCatching { NoteLength.valueOf(it) }.getOrNull() }
                ?: NoteLength.MEDIUM,
            difficulty = prefs[Keys.DIFFICULTY]?.let { runCatching { Difficulty.valueOf(it) }.getOrNull() }
                ?: Difficulty.BEGINNER,
            language = prefs[Keys.LANGUAGE]?.let { runCatching { NoteLanguage.valueOf(it) }.getOrNull() }
                ?: NoteLanguage.ENGLISH,
            noteStyle = prefs[Keys.NOTE_STYLE]?.let { runCatching { NoteStyle.valueOf(it) }.getOrNull() }
                ?: NoteStyle.EXAM_FOCUSED
        )
    }

    suspend fun savePreferences(preferences: AiPreferences) {
        context.aiPreferencesDataStore.edit { prefs ->
            prefs[Keys.NOTE_LENGTH] = preferences.noteLength.name
            prefs[Keys.DIFFICULTY] = preferences.difficulty.name
            prefs[Keys.LANGUAGE] = preferences.language.name
            prefs[Keys.NOTE_STYLE] = preferences.noteStyle.name
        }
    }

    suspend fun clearAll() {
        context.aiPreferencesDataStore.edit { it.clear() }
    }
}