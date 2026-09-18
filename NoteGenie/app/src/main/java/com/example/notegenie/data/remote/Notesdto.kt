package com.example.notegenie.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotePreferencesDto(
    @SerialName("note_length") val noteLength: String,
    @SerialName("difficulty") val difficulty: String,
    @SerialName("language") val language: String,
    @SerialName("note_style") val noteStyle: String
)

@Serializable
data class SummarizeRequestDto(
    val topic: String,
    @SerialName("raw_text") val rawText: String? = null,
    val preferences: NotePreferencesDto
)

@Serializable
data class NoteResponseDto(
    val topic: String,
    val content: String
)

