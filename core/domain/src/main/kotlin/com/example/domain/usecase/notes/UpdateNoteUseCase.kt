package com.example.domain.usecase.notes

import com.jero.core.model.Note

interface UpdateNoteUseCase {
    suspend operator fun invoke(noteId: String, title: String, content: String): Result<Note>
}
