package com.example.domain.usecase.notes.interfaces

interface PermanentlyDeleteNoteUseCase {
    suspend operator fun invoke(noteId: String): Result<Unit>
}
