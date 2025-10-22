package com.jero.data.usecase

import com.example.domain.repository.AuthRepository
import com.example.domain.usecase.SendPasswordResetEmailUseCase

class SendPasswordResetEmailUseCaseImpl(
    private val authRepository: AuthRepository,
): SendPasswordResetEmailUseCase {
    override suspend operator fun invoke(email: String): Result<Unit> = authRepository.sendPasswordResetEmail(email)
}
