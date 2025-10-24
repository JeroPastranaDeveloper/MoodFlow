package com.jero.data.usecase

import com.example.domain.repository.AuthRepository
import com.example.domain.usecase.user.GetCurrentUserUseCase

class GetCurrentUserUseCaseImpl(
    private val authRepository: AuthRepository
): GetCurrentUserUseCase {
    override suspend operator fun invoke() = authRepository.getCurrentUser()
}
