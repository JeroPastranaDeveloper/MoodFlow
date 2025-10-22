package com.jero.data.usecase

import com.example.domain.repository.AuthRepository
import com.example.domain.usecase.user.SignInWithEmailUseCase
import com.jero.core.model.User

class SignInWithEmailUseCaseImpl(
    private val authRepository: AuthRepository,
): SignInWithEmailUseCase {
    override suspend operator fun invoke(email: String, password: String): Result<User> =
        authRepository.signInWithEmail(email, password)
}
