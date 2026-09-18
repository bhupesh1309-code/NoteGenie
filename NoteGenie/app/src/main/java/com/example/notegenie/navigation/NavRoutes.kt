package com.example.notegenie.navigation

import kotlinx.serialization.Serializable

// ---------- Type-safe route definitions ----------
// Each destination is a serializable type instead of a raw string path.
// The compiler catches typos and missing/wrong-typed arguments at compile time.

@Serializable
object Login

@Serializable
object SignUp

@Serializable
object ForgotPassword

@Serializable
object Home

@Serializable
object Profile

@Serializable
object AiPreferencesRoute

@Serializable
object About

@Serializable
object Notes

@Serializable
data class NoteDetail(
    val noteId: String,
    val topic: String,
    val content: String,
    val dateGenerated: String,
    val isSaved: Boolean = false
)