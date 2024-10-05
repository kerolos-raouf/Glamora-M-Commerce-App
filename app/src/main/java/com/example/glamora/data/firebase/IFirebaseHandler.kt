package com.example.glamora.data.firebase

import com.google.firebase.auth.FirebaseUser

interface IFirebaseHandler {
    fun signUp(email: String, password: String, callback: (Boolean, String?) -> Unit)
    fun signIn(email: String, password: String, callback: (Boolean, String?) -> Unit)
    fun signInWithGoogle(idToken: String, callback: (Boolean, String?) -> Unit)
    fun signOut()
    fun isUserSignedIn(): Boolean
    fun getCurrentUser(): FirebaseUser?
    fun resetPassword(email: String, callback: (Boolean, String?) -> Unit)
}