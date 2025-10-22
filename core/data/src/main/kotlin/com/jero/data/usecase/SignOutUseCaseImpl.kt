package com.jero.data.usecase

import com.example.domain.repository.AuthRepository
import com.example.domain.usecase.SignOutUseCase

class SignOutUseCaseImpl(
    private val authRepository: AuthRepository
): SignOutUseCase {
    override suspend operator fun invoke(): Result<Unit> = authRepository.signOut()
}
