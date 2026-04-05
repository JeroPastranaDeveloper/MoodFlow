package com.example.domain.usecase.tags.implementations

import com.example.domain.providers.StringsProvider
import com.example.domain.repository.TagRepository
import com.example.domain.usecase.tags.interfaces.DeleteTagUseCase
import com.google.firebase.auth.FirebaseAuth
import com.jero.core.designsystem.R
import com.jero.core.model.UNTAGGED_TAG_ID

class DeleteTagUseCaseImpl(
    private val tagRepository: TagRepository,
    private val auth: FirebaseAuth,
    private val strings: StringsProvider,
) : DeleteTagUseCase {
    override suspend fun invoke(tagId: String): Result<Unit> {
        if (tagId == UNTAGGED_TAG_ID) {
            return Result.failure(Exception(strings(R.string.error_tag_not_deletable)))
        }
        val userId = auth.currentUser?.uid
            ?: return Result.failure(Exception(strings(R.string.user_not_authenticated)))
        return tagRepository.deleteTag(tagId, userId)
    }
}
