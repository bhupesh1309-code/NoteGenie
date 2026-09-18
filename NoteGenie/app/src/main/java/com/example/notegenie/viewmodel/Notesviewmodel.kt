package com.example.notegenie.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notegenie.data.AiPreferencesRepository
import com.example.notegenie.data.AuthRepository
import com.example.notegenie.data.NoteGenerationResult
import com.example.notegenie.data.NotesRepository
import com.example.notegenie.data.local.NoteDatabase
import com.example.notegenie.data.local.NoteEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class GenerationUiState {
    data object Idle : GenerationUiState()
    data object Loading : GenerationUiState()
    data class Success(val topic: String, val content: String, val dateGenerated: String) : GenerationUiState()
    data class Error(val message: String) : GenerationUiState()
}

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val notesRepository = NotesRepository()
    private val aiPreferencesRepository = AiPreferencesRepository(application.applicationContext)
    private val noteDao = NoteDatabase.getInstance(application.applicationContext).noteDao()
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow<GenerationUiState>(GenerationUiState.Idle)
    val uiState: StateFlow<GenerationUiState> = _uiState.asStateFlow()

    private val currentUserIdFlow = callbackFlow {
        val auth = FirebaseAuth.getInstance()
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val allNotes: StateFlow<List<NoteEntity>> = currentUserIdFlow
        .flatMapLatest { uid ->
            if (uid == null) flowOf(emptyList()) else noteDao.getAllNotes(uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun generateFromTopic(topic: String) {
        if (topic.isBlank()) {
            _uiState.value = GenerationUiState.Error("Please enter a topic.")
            return
        }
        viewModelScope.launch {
            _uiState.value = GenerationUiState.Loading
            val preferences = aiPreferencesRepository.preferencesFlow.first()

            when (val result = notesRepository.generateFromTopic(topic, preferences)) {
                is NoteGenerationResult.Success -> emitSuccess(result.topic, result.content)
                is NoteGenerationResult.Error -> _uiState.value = GenerationUiState.Error(result.message)
            }
        }
    }

    fun generateFromFile(fileUri: Uri, topic: String) {
        if (topic.isBlank()) {
            _uiState.value = GenerationUiState.Error("Please enter a topic to describe the file's content.")
            return
        }
        viewModelScope.launch {
            _uiState.value = GenerationUiState.Loading
            val preferences = aiPreferencesRepository.preferencesFlow.first()

            val result = notesRepository.generateFromFile(
                context = getApplication<Application>().applicationContext,
                fileUri = fileUri,
                topic = topic,
                preferences = preferences
            )
            when (result) {
                is NoteGenerationResult.Success -> emitSuccess(result.topic, result.content)
                is NoteGenerationResult.Error -> _uiState.value = GenerationUiState.Error(result.message)
            }
        }
    }

    private fun emitSuccess(topic: String, content: String) {
        val dateGenerated = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())
        _uiState.value = GenerationUiState.Success(topic, content, dateGenerated)
    }

    fun saveNote(noteId: String, topic: String, content: String, dateGenerated: String) {
        val uid = authRepository.currentUserId() ?: return
        viewModelScope.launch {
            noteDao.insertNote(
                NoteEntity(
                    id = noteId,
                    userId = uid,
                    topic = topic,
                    content = content,
                    dateGenerated = dateGenerated
                )
            )
        }
    }

    fun resetState() {
        _uiState.value = GenerationUiState.Idle
    }
}