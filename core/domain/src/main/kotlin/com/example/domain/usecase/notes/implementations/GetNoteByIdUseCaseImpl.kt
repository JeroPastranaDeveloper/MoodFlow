package com.example.domain.usecase.notes.implementations

import com.example.domain.repository.NotesRepository
import com.example.domain.usecase.notes.interfaces.GetNoteByIdUseCase

class GetNoteByIdUseCaseImpl(
    private val noteRepository: NotesRepository
): GetNoteByIdUseCase {
    override suspend fun invoke(noteId: String) = noteRepository.getNote(noteId)
}
