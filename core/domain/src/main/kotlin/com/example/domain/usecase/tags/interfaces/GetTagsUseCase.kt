package com.example.domain.usecase.tags.interfaces

import com.jero.core.model.Tag
import kotlinx.coroutines.flow.Flow

interface GetTagsUseCase {
    operator fun invoke(): Flow<List<Tag>>
}
