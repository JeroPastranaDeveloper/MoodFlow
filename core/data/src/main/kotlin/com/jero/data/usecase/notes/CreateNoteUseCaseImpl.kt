package com.jero.data.usecase.notes

import com.example.domain.repository.NotesRepository
import com.example.domain.usecase.notes.CreateNoteUseCase
import com.google.firebase.auth.FirebaseAuth
import com.jero.core.model.Note
import java.util.UUID

class CreateNoteUseCaseImpl(
    private val notesRepository: NotesRepository,
    private val auth: FirebaseAuth
) : CreateNoteUseCase {
    
    override suspend fun invoke(note: Note): Result<Note> {
        val userId = auth.currentUser?.uid 
            ?: return Result.failure(Exception("Usuario no autenticado"))
        
        if (note.title.isBlank()) {
            return Result.failure(Exception("El título no puede estar vacío"))
        }
        
        val note = Note(
            id = UUID.randomUUID().toString(),
            title = note.title.trim(),
            content = note.content.trim(),
            date = System.currentTimeMillis(),
            userId = userId
        )
        
        return notesRepository.createNote(note)
    }
}
