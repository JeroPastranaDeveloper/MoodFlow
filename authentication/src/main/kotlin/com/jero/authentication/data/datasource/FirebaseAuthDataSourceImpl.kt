package com.jero.authentication.data.datasource

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSourceImpl(
    private val auth: FirebaseAuth = Firebase.auth
) : FirebaseAuthDataSource {
    
    override suspend fun signInWithEmail(email: String, password: String): FirebaseUser? =
        auth.signInWithEmailAndPassword(email, password).await().user
    
    override suspend fun signUpWithEmail(email: String, password: String): FirebaseUser? =
        auth.createUserWithEmailAndPassword(email, password).await().user
    
    override suspend fun signOut() {
        auth.signOut()
    }
    
    override fun getCurrentUser(): FirebaseUser? =
        auth.currentUser
    
    override suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }
    
    override suspend fun sendEmailVerification() {
        auth.currentUser?.sendEmailVerification()?.await()
    }
}
