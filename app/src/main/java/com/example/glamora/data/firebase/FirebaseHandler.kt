package com.example.glamora.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseHandler @Inject constructor(

) : IFirebaseHandler {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> =
        suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.success(Unit)) // Sign-in successful
                    } else {
                        continuation.resume(
                            Result.failure(
                                task.exception ?: Exception("Login failed")
                            )
                        )
                    }
                }
        }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> =
        suspendCoroutine { continuation ->
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.success(Unit)) // Google sign-in successful
                    } else {
                        continuation.resume(
                            Result.failure(
                                task.exception ?: Exception("Google login failed")
                            )
                        )
                    }
                }
        }

    override suspend fun resetPassword(email: String): Result<Unit> =
        suspendCoroutine { continuation ->
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.success(Unit)) // Reset email sent
                    } else {
                        continuation.resume(
                            Result.failure(
                                task.exception ?: Exception("Failed to send reset email")
                            )
                        )
                    }
                }
        }

    override suspend fun signUp(email: String, password: String): Result<Unit> =
        suspendCoroutine { continuation ->
            // Use a flag to prevent multiple resume calls
            var isResumed = false

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                if (!isResumed) {
                                    isResumed = true
                                    continuation.resume(Result.success(Unit))
                                }
                            } else {
                                if (!isResumed) {
                                    isResumed = true
                                    continuation.resume(
                                        Result.failure(
                                            verificationTask.exception
                                                ?: Exception("Verification failed")
                                        )
                                    )
                                }
                            }
                        }
                    } else {
                        if (!isResumed) {
                            isResumed = true
                            continuation.resume(
                                Result.failure(
                                    task.exception ?: Exception("Sign-up failed")
                                )
                            )
                        }
                    }
                }
        }


    override fun signOut() {
        auth.signOut()
    }

    override fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }


    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }


}
