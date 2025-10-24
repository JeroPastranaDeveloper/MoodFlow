package com.example.domain.usecase.notes.interfaces

import com.jero.core.model.Note

interface UpdateNoteUseCase {
    suspend operator fun invoke(note: Note): Result<Note>
}
