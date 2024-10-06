package com.example.glamora.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject

class FirebaseHandler @Inject constructor(

) : IFirebaseHandler {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }



    override fun signUp(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // User account created successfully
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                callback(true, null) // Sign-up and verification email sent successfully
                            } else {
                                callback(false, verificationTask.exception?.message) // Error sending verification email
                            }
                        }
                } else {
                    callback(false, task.exception?.message) // Sign-up failed
                }
            }
    }


    override fun signIn(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null) // Sign-in successful
                } else {
                    callback(false, task.exception?.message) // Sign-in failed
                }
            }
    }

    override fun signInWithGoogle(idToken: String, callback: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null) // Google sign-in successful
                } else {
                    callback(false, task.exception?.message) // Google sign-in failed
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

    override fun resetPassword(email: String, callback: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null) // Reset email sent
                } else {
                    callback(false, task.exception?.message) // Error occurred
                }
            }
    }
}
