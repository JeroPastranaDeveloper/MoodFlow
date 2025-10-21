package com.jero.authentication.data.datasource

import com.google.firebase.auth.FirebaseUser

interface FirebaseAuthDataSource {
    suspend fun signInWithEmail(email: String, password: String): FirebaseUser?
    suspend fun signUpWithEmail(email: String, password: String): FirebaseUser?
    suspend fun signOut()
    fun getCurrentUser(): FirebaseUser?
    suspend fun sendPasswordResetEmail(email: String)
    suspend fun sendEmailVerification()
}
