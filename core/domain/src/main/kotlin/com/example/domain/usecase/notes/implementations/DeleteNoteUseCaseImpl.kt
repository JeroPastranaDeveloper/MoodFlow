package com.example.domain.usecase.notes.implementations

import com.example.domain.repository.NotesRepository
import com.example.domain.usecase.notes.interfaces.DeleteNoteUseCase

class DeleteNoteUseCaseImpl(
    private val notesRepository: NotesRepository
) : DeleteNoteUseCase {

    override suspend fun invoke(noteId: String): Result<Unit> =
        notesRepository.deleteNote(noteId)
}
