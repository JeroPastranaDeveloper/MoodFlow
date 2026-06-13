package com.example.domain.usecase.user

interface GetGoogleIdTokenUseCase {
    suspend operator fun invoke(): String?
}
