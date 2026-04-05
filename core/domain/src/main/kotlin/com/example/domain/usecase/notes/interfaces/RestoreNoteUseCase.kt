package com.example.domain.usecase.notes.interfaces

interface RestoreNoteUseCase {
    suspend operator fun invoke(noteId: String): Result<Unit>
}
