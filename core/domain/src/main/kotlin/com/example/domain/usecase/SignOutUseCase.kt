package com.example.domain.usecase

interface SignOutUseCase {
    suspend operator fun invoke(): Result<Unit>
}

