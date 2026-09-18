package com.example.notegenie.data.remote

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val currentUser = auth.currentUser

        val token = if (currentUser != null) {
            try {
                Tasks.await(currentUser.getIdToken(false)).token
            } catch (e: Exception) {
                null
            }
        } else null

        val request = chain.request().newBuilder()
            .apply {
                if (token != null) {
                    addHeader("Authorization", "Bearer $token")
                }
            }
            .build()

        return chain.proceed(request)
    }
}