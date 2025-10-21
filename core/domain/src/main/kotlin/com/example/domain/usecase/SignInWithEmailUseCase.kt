package com.example.domain.usecase

import com.example.domain.repository.AuthRepository
import com.jero.core.model.AuthError
import com.jero.core.model.User

class SignInWithEmailUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (!isValidEmail(email)) {
            return Result.failure(AuthError.InvalidEmail)
        }
        
        if (!isValidPassword(password)) {
            return Result.failure(AuthError.InvalidPassword)
        }
        
        return authRepository.signInWithEmail(email, password)
    }
    
    private fun isValidEmail(email: String): Boolean = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    
    private fun isValidPassword(password: String): Boolean = password.length >= 6
}
