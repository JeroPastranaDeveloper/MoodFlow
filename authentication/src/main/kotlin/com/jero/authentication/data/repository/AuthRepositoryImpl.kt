package com.jero.authentication.data.repository

import com.example.domain.repository.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.jero.authentication.data.datasource.FirebaseAuthDataSource
import com.jero.authentication.data.mapper.toDomain
import com.jero.core.model.AuthError
import com.jero.core.model.User

class AuthRepositoryImpl(
    private val firebaseDataSource: FirebaseAuthDataSource,
) : AuthRepository {

    override suspend fun signInWithEmail(email: String, password: String): Result<User> = try {
        val firebaseUser = firebaseDataSource.signInWithEmail(email, password)
        if (firebaseUser != null) {
            Result.success(firebaseUser.toDomain())
        } else {
            Result.failure(AuthError.Unknown("User not authenticated"))
        }
    } catch (_: FirebaseAuthInvalidCredentialsException) {
        Result.failure(AuthError.InvalidPassword)
    } catch (_: FirebaseAuthInvalidUserException) {
        Result.failure(AuthError.UserNotFound)
    } catch (e: Exception) {
        Result.failure(mapFirebaseException(e))
    }

    override suspend fun signUpWithEmail(email: String, password: String): Result<User> = try {
        val firebaseUser = firebaseDataSource.signUpWithEmail(email, password)
        if (firebaseUser != null) {
            Result.success(firebaseUser.toDomain())
        } else {
            Result.failure(AuthError.Unknown("Error creating user"))
        }
    } catch (_: FirebaseAuthUserCollisionException) {
        Result.failure(AuthError.EmailAlreadyInUse)
    } catch (_: FirebaseAuthWeakPasswordException) {
        Result.failure(AuthError.WeakPassword)
    } catch (e: Exception) {
        Result.failure(mapFirebaseException(e))
    }

    override suspend fun signOut(): Result<Unit> = try {
        firebaseDataSource.signOut()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(mapFirebaseException(e))
    }

    override suspend fun getCurrentUser(): User? = firebaseDataSource.getCurrentUser()?.toDomain()

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = try {
        firebaseDataSource.sendPasswordResetEmail(email)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(mapFirebaseException(e))
    }

    override suspend fun sendEmailVerification(): Result<Unit> = try {
        firebaseDataSource.sendEmailVerification()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(mapFirebaseException(e))
    }

    private fun mapFirebaseException(exception: Exception): AuthError = when (exception) {
        is FirebaseNetworkException -> AuthError.NetworkError
        else -> AuthError.Unknown(exception.message ?: "Unknown error")
    }
}
