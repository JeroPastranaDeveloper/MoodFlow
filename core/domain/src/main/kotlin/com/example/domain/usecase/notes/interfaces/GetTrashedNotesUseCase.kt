package com.example.domain.usecase.notes.interfaces

import com.jero.core.model.Note
import kotlinx.coroutines.flow.Flow

interface GetTrashedNotesUseCase {
    suspend operator fun invoke(): Flow<List<Note>>
}
