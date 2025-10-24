package com.jero.data.usecase

import com.example.domain.repository.AuthRepository
import com.example.domain.usecase.user.SignUpWithEmailUseCase
import com.jero.core.model.User

class SignUpWithEmailUseCaseImpl(
    private val authRepository: AuthRepository
): SignUpWithEmailUseCase {
    override suspend operator fun invoke(email: String, password: String): Result<User> =
        authRepository.signUpWithEmail(email, password)
}
