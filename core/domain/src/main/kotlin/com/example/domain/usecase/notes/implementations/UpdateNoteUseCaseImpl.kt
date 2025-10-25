package com.example.domain.usecase.notes.implementations

import com.example.domain.repository.NotesRepository
import com.example.domain.usecase.notes.interfaces.UpdateNoteUseCase
import com.jero.core.model.Note

class UpdateNoteUseCaseImpl(
    private val notesRepository: NotesRepository,
) : UpdateNoteUseCase {
    
    override suspend fun invoke(note: Note): Result<Note> {
        if (note.title.isBlank()) {
            return Result.failure(Exception("El título no puede estar vacío"))
        }

        return notesRepository.getNote(note.id).fold(
            onSuccess = { existingNote ->
                val updatedNote = existingNote.copy(
                    title = note.title.trim(),
                    content = note.content.trim(),
                    pinned = note.pinned,
                    date = note.date,
                )
                notesRepository.updateNote(updatedNote)
            },
            onFailure = { Result.failure(it) }
        )
    }
}
