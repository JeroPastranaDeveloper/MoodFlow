package com.example.domain.usecase.tags.implementations

import com.example.domain.providers.StringsProvider
import com.example.domain.repository.TagRepository
import com.example.domain.usecase.tags.interfaces.UpdateTagUseCase
import com.jero.core.designsystem.R
import com.jero.core.model.Tag
import com.jero.core.model.UNTAGGED_TAG_ID

class UpdateTagUseCaseImpl(
    private val tagRepository: TagRepository,
    private val strings: StringsProvider,
) : UpdateTagUseCase {
    override suspend fun invoke(tag: Tag): Result<Unit> {
        if (tag.id == UNTAGGED_TAG_ID) {
            return Result.failure(Exception(strings(R.string.error_tag_not_editable)))
        }
        return tagRepository.updateTag(tag.copy(name = tag.name.trim()))
    }
}
