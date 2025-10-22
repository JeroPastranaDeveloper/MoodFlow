package com.example.domain.usecase

interface SendPasswordResetEmailUseCase {
    suspend operator fun invoke(email: String): Result<Unit>
}
