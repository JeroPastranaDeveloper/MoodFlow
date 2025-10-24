package com.example.domain.usecase.notes.interfaces

interface DeleteNoteUseCase {
    suspend operator fun invoke(noteId: String): Result<Unit>
}
