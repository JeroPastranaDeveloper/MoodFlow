package com.jero.data.usecase

import com.example.domain.repository.AuthRepository
import com.example.domain.usecase.user.SignInWithGoogleUseCase
import com.jero.core.model.User

class SignInWithGoogleUseCaseImpl(
    private val authRepository: AuthRepository,
) : SignInWithGoogleUseCase {
    override suspend operator fun invoke(idToken: String): Result<User> =
        authRepository.signInWithGoogle(idToken)
}
