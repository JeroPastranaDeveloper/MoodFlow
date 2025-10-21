package com.example.domain.usecase

import com.example.domain.repository.AuthRepository

class SignOutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> = authRepository.signOut()
}
