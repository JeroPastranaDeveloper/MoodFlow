package com.example.domain.usecase

import com.example.domain.repository.AuthRepository
import com.jero.core.model.AuthError

class SendPasswordResetEmailUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (!isValidEmail(email)) {
            return Result.failure(AuthError.InvalidEmail)
        }
        
        return authRepository.sendPasswordResetEmail(email)
    }
    
    private fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}