package com.example.domain.usecase.notes.interfaces

import com.jero.core.model.Note

interface GetNoteByIdUseCase {
    suspend operator fun invoke(noteId: String): Result<Note?>
}
