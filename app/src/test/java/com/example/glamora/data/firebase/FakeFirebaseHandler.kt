package com.example.glamora.data.firebase

import com.google.firebase.auth.FirebaseUser

class FakeFirebaseHandler : IFirebaseHandler {
    override suspend fun resetPassword(email: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun signUp(email: String, password: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun signOut() {
        TODO("Not yet implemented")
    }

    override fun isUserSignedIn(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getCurrentUser(): FirebaseUser? {
        TODO("Not yet implemented")
    }
}