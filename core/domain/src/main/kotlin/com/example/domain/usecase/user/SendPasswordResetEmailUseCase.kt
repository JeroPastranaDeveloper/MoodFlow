package com.example.domain.usecase.user

interface SendPasswordResetEmailUseCase {
    suspend operator fun invoke(email: String): Result<Unit>
}
