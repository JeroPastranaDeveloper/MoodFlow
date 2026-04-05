package com.example.domain.usecase.tags.interfaces

import com.jero.core.model.Tag

interface CreateTagUseCase {
    suspend operator fun invoke(name: String): Result<Tag>
}
