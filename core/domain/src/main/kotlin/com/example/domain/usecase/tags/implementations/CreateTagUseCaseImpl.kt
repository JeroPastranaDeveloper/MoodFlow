package com.example.domain.usecase.tags.implementations

import com.example.domain.providers.StringsProvider
import com.example.domain.repository.TagRepository
import com.example.domain.usecase.tags.interfaces.CreateTagUseCase
import com.google.firebase.auth.FirebaseAuth
import com.jero.core.designsystem.R
import com.jero.core.model.Tag

class CreateTagUseCaseImpl(
    private val tagRepository: TagRepository,
    private val auth: FirebaseAuth,
    private val strings: StringsProvider,
) : CreateTagUseCase {
    override suspend fun invoke(name: String): Result<Tag> {
        val userId = auth.currentUser?.uid
            ?: return Result.failure(Exception(strings(R.string.user_not_authenticated)))
        return tagRepository.createTag(name.trim(), userId)
    }
}
