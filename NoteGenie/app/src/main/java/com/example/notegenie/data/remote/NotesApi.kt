package com.example.notegenie.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface NotesApi {

    @POST("summarize")
    suspend fun summarize(@Body request: SummarizeRequestDto): NoteResponseDto

    @Multipart
    @POST("upload")
    suspend fun upload(
        @Part file: MultipartBody.Part,
        @Part("topic") topic: RequestBody,
        @Part("preferences") preferences: RequestBody
    ): NoteResponseDto
}