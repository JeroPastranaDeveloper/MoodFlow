package com.example.domain.usecase.tags.implementations

import com.example.domain.repository.TagRepository
import com.example.domain.usecase.tags.interfaces.GetTagsUseCase
import com.google.firebase.auth.FirebaseAuth
import com.jero.core.model.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetTagsUseCaseImpl(
    private val tagRepository: TagRepository,
    private val auth: FirebaseAuth,
) : GetTagsUseCase {
    override fun invoke(): Flow<List<Tag>> {
        val userId = auth.currentUser?.uid ?: return flowOf(emptyList())
        return tagRepository.getTagsFlow(userId)
    }
}
