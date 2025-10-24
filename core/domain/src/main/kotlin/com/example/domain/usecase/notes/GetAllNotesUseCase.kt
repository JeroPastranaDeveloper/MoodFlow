package com.example.domain.usecase.notes

import com.jero.core.model.Note
import kotlinx.coroutines.flow.Flow

interface GetAllNotesUseCase {
    operator fun invoke(): Flow<Result<List<Note>>>
}
