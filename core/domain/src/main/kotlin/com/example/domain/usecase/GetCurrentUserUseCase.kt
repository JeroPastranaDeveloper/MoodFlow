package com.example.domain.usecase

import com.example.domain.repository.AuthRepository
import com.jero.core.model.User

class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): User? = authRepository.getCurrentUser()
}
