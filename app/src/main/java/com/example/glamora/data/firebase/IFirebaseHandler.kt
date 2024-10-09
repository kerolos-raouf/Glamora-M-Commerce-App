package com.example.glamora.data.firebase

import com.google.firebase.auth.FirebaseUser

interface IFirebaseHandler {
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun signInWithGoogle(idToken: String): Result<Unit>
    suspend fun signInWithEmail(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String): Result<Unit>

    fun signOut()
    fun isUserSignedIn(): Boolean
    fun getCurrentUser(): FirebaseUser?

}