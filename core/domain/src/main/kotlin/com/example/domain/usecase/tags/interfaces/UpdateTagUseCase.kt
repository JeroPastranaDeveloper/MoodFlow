package com.example.domain.usecase.tags.interfaces

import com.jero.core.model.Tag

interface UpdateTagUseCase {
    suspend operator fun invoke(tag: Tag): Result<Unit>
}
