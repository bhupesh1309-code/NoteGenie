package com.example.notegenie.data

import android.content.Context
import android.net.Uri
import com.example.notegenie.data.remote.NotePreferencesDto
import com.example.notegenie.data.remote.NotesApi
import com.example.notegenie.data.remote.RetrofitClient
import com.example.notegenie.data.remote.SummarizeRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

sealed class NoteGenerationResult {
    data class Success(val topic: String, val content: String) : NoteGenerationResult()
    data class Error(val message: String) : NoteGenerationResult()
}

class NotesRepository(
    private val api: NotesApi = RetrofitClient.notesApi
) {
    private fun AiPreferences.toDto() = NotePreferencesDto(
        noteLength = noteLength.name,
        difficulty = difficulty.name,
        language = language.name,
        noteStyle = noteStyle.name
    )

    suspend fun generateFromTopic(topic: String, preferences: AiPreferences): NoteGenerationResult {
        return try {
            val response = api.summarize(
                SummarizeRequestDto(topic = topic, rawText = null, preferences = preferences.toDto())
            )
            NoteGenerationResult.Success(response.topic, response.content)
        } catch (e: Exception) {
            NoteGenerationResult.Error(mapNetworkError(e))
        }
    }

    suspend fun generateFromFile(
        context: Context,
        fileUri: Uri,
        topic: String,
        preferences: AiPreferences
    ): NoteGenerationResult {
        return try {
            withContext(Dispatchers.IO) {
                val contentResolver = context.contentResolver
                val mimeType = contentResolver.getType(fileUri) ?: "application/octet-stream"
                val fileName = queryFileName(context, fileUri) ?: "upload"

                val tempFile = File(context.cacheDir, fileName)
                contentResolver.openInputStream(fileUri)?.use { input ->
                    tempFile.outputStream().use { output -> input.copyTo(output) }
                }

                val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", fileName, requestFile)

                val topicPart = topic.toRequestBody("text/plain".toMediaTypeOrNull())
                val preferencesJson = Json.encodeToString(preferences.toDto())
                val preferencesPart = preferencesJson.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = api.upload(filePart, topicPart, preferencesPart)

                tempFile.delete()

                NoteGenerationResult.Success(response.topic, response.content)
            }
        } catch (e: Exception) {
            NoteGenerationResult.Error(mapNetworkError(e))
        }
    }

    private fun queryFileName(context: Context, uri: Uri): String? {
        var name: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }

    private fun mapNetworkError(e: Exception): String {
        return when {
            e is java.net.ConnectException -> "Can't reach the server. Check your connection and try again."
            e is java.net.SocketTimeoutException -> "The request took too long. Please try again."
            e.message?.contains("401") == true -> "Session expired. Please log in again."
            e.message?.contains("400") == true -> "Unsupported file type."
            else -> e.message ?: "Something went wrong while generating your notes."
        }
    }
}