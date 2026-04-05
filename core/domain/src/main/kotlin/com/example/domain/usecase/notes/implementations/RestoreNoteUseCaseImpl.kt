package com.example.domain.usecase.notes.implementations

import com.example.domain.repository.NotesRepository
import com.example.domain.usecase.notes.interfaces.RestoreNoteUseCase

class RestoreNoteUseCaseImpl(
    private val notesRepository: NotesRepository,
) : RestoreNoteUseCase {

    override suspend fun invoke(noteId: String): Result<Unit> =
        notesRepository.restoreNote(noteId)
}
