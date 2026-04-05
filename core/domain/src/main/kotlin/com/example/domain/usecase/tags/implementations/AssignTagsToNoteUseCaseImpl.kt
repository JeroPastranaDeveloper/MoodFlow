package com.example.domain.usecase.tags.implementations

import com.example.domain.providers.StringsProvider
import com.example.domain.repository.TagRepository
import com.example.domain.usecase.tags.interfaces.AssignTagsToNoteUseCase
import com.google.firebase.auth.FirebaseAuth
import com.jero.core.designsystem.R
import com.jero.core.model.UNTAGGED_TAG_ID

class AssignTagsToNoteUseCaseImpl(
    private val tagRepository: TagRepository,
    private val auth: FirebaseAuth,
    private val strings: StringsProvider,
) : AssignTagsToNoteUseCase {
    override suspend fun invoke(noteId: String, tagIds: List<String>): Result<Unit> {
        val userId = auth.currentUser?.uid
            ?: return Result.failure(Exception(strings(R.string.user_not_authenticated)))
        val finalTagIds = tagIds.ifEmpty { listOf(UNTAGGED_TAG_ID) }
        return tagRepository.assignTagsToNote(noteId, finalTagIds, userId)
    }
}
