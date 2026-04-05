package com.example.domain.usecase.tags.implementations

import com.example.domain.repository.TagRepository
import com.example.domain.usecase.tags.interfaces.SeedDefaultTagsUseCase

class SeedDefaultTagsUseCaseImpl(
    private val tagRepository: TagRepository,
) : SeedDefaultTagsUseCase {
    override suspend fun invoke(userId: String) {
        tagRepository.seedDefaultTags(userId)
    }
}
