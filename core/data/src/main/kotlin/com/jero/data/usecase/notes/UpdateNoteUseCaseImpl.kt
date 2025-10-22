package com.jero.data.usecase.notes

import com.example.domain.repository.NotesRepository
import com.example.domain.usecase.notes.UpdateNoteUseCase
import com.google.firebase.auth.FirebaseAuth
import com.jero.core.model.Note

class UpdateNoteUseCaseImpl(
    private val notesRepository: NotesRepository,
    private val auth: FirebaseAuth
) : UpdateNoteUseCase {
    
    override suspend fun invoke(
        noteId: String, 
        title: String, 
        content: String
    ): Result<Note> {
        val userId = auth.currentUser?.uid 
            ?: return Result.failure(Exception("Usuario no autenticado"))
        
        if (title.isBlank()) {
            return Result.failure(Exception("El título no puede estar vacío"))
        }

        // Primero obtener la nota existente para mantener la fecha original
        return notesRepository.getNote(noteId).fold(
            onSuccess = { existingNote ->
                val updatedNote = existingNote.copy(
                    title = title.trim(),
                    content = content.trim()
                )
                notesRepository.updateNote(updatedNote)
            },
            onFailure = { Result.failure(it) }
        )
    }
}
