package com.example.domain.usecase.notes.interfaces

import com.jero.core.model.Note

interface CreateNoteUseCase {
    suspend operator fun invoke(note: Note): Result<Note>
}
