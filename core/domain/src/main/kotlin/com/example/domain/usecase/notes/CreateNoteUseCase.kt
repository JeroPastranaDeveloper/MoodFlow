package com.example.domain.usecase.notes

import com.jero.core.model.Note

interface CreateNoteUseCase {
    suspend operator fun invoke(title: String, content: String): Result<Note>
}
