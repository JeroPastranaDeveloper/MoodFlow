package com.example.domain.repository

import com.jero.core.model.User

// domain/repository/AuthRepository.kt
interface AuthRepository {
    suspend fun signInWithEmail(email: String, password: String): Result<User>
    suspend fun signUpWithEmail(email: String, password: String): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): User?
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun sendEmailVerification(): Result<Unit>
}
