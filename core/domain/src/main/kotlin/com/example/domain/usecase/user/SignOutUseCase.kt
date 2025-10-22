package com.example.domain.usecase.user

interface SignOutUseCase {
    suspend operator fun invoke(): Result<Unit>
}

