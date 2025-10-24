package com.example.domain.usecase.notes

interface DeleteNoteUseCase {
    suspend operator fun invoke(noteId: String): Result<Unit>
}
