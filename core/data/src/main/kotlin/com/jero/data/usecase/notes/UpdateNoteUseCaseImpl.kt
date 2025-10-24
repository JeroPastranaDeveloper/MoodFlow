package com.jero.data.usecase.notes

import com.example.domain.repository.NotesRepository
import com.example.domain.usecase.notes.UpdateNoteUseCase
import com.google.firebase.auth.FirebaseAuth
import com.jero.core.model.Note

class UpdateNoteUseCaseImpl(
    private val notesRepository: NotesRepository,
    private val auth: FirebaseAuth
) : UpdateNoteUseCase {
    
    override suspend fun invoke(note: Note): Result<Note> {
        val userId = auth.currentUser?.uid 
            ?: return Result.failure(Exception("Usuario no autenticado"))
        
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
