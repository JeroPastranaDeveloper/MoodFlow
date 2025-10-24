package com.example.domain.usecase.notes.implementations

import com.example.domain.repository.NotesRepository
import com.example.domain.usecase.notes.interfaces.CreateNoteUseCase
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

        val id = UUID.randomUUID().toString()

        // TODO: HACER QUE GENERE UN NUEVO ID SI LA NOTA NO ES NOTE() (CREO QUE DEVOLVÍA ESO), PERO CREO QUE DEVUELVE UN FAILURE, MÍRALO BIEN. AL EDITAR SIN INTERNET, SE PONEN DEBAJO DEL TODO EN LA UI, REVISAR
        notesRepository.getNote(id)
        
        val note = note.copy(
            id = UUID.randomUUID().toString(),
            title = note.title.trim(),
            content = note.content.trim(),
            date = System.currentTimeMillis(),
            pinned = note.pinned,
            userId = userId
        )
        
        return notesRepository.createNote(note)
    }
}