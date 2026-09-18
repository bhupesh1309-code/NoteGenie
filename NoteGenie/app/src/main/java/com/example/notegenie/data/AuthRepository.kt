package com.example.notegenie.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

sealed class AuthResult {
    data class Success(val uid: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun login(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success(result.user?.uid.orEmpty())
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e))
        }
    }

    suspend fun signUp(name: String, email: String, password: String): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            user?.updateProfile(profileUpdate)?.await()

            AuthResult.Success(user?.uid.orEmpty())
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e))
        }
    }

    suspend fun sendPasswordResetEmail(email: String): AuthResult {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success(uid = "")
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e))
        }
    }

    suspend fun deleteAccount(): AuthResult {
        return try {
            auth.currentUser?.delete()?.await()
            AuthResult.Success(uid = "")
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e))
        }
    }

    fun currentUserId(): String? = auth.currentUser?.uid

    fun currentUserName(): String? = auth.currentUser?.displayName

    fun currentUserEmail(): String? = auth.currentUser?.email

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun logout() = auth.signOut()

    private fun mapFirebaseError(e: Exception): String {
        val raw = e.message ?: return "Something went wrong. Please try again."
        return when {
            raw.contains("badly formatted", ignoreCase = true) -> "That email address looks invalid."
            raw.contains("no user record", ignoreCase = true) -> "No account found with that email."
            raw.contains("password is invalid", ignoreCase = true) -> "Incorrect password."
            raw.contains("email address is already in use", ignoreCase = true) -> "An account already exists with this email."
            raw.contains("password should be at least", ignoreCase = true) -> "Password must be at least 6 characters."
            raw.contains("network error", ignoreCase = true) -> "Network error. Check your connection and try again."
            raw.contains("requires recent authentication", ignoreCase = true) ->
                "For your security, please log out and log back in, then try again."
            else -> raw
        }
    }
}