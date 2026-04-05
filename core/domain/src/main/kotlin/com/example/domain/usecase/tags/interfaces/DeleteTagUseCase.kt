package com.example.domain.usecase.tags.interfaces

interface DeleteTagUseCase {
    suspend operator fun invoke(tagId: String): Result<Unit>
}
