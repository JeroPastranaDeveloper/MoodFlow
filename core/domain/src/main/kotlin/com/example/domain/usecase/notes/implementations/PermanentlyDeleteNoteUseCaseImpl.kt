package com.example.domain.usecase.notes.implementations

import com.example.domain.repository.NotesRepository
import com.example.domain.usecase.notes.interfaces.PermanentlyDeleteNoteUseCase

class PermanentlyDeleteNoteUseCaseImpl(
    private val notesRepository: NotesRepository,
) : PermanentlyDeleteNoteUseCase {

    override suspend fun invoke(noteId: String): Result<Unit> =
        notesRepository.permanentlyDeleteNote(noteId)
}
