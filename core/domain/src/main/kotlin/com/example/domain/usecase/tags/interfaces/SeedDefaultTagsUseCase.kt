package com.example.domain.usecase.tags.interfaces

interface SeedDefaultTagsUseCase {
    suspend operator fun invoke(userId: String)
}
