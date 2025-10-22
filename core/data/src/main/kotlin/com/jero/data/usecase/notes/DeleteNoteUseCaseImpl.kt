package com.jero.data.usecase.notes

import com.example.domain.repository.NotesRepository
import com.example.domain.usecase.notes.DeleteNoteUseCase

class DeleteNoteUseCaseImpl(
    private val notesRepository: NotesRepository
) : DeleteNoteUseCase {
    
    override suspend fun invoke(noteId: String): Result<Unit> =
        notesRepository.deleteNote(noteId)
}
