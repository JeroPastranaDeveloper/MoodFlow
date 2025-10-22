package com.jero.data.usecase.notes

import com.example.domain.repository.NotesRepository
import com.example.domain.usecase.notes.GetAllNotesUseCase
import com.google.firebase.auth.FirebaseAuth
import com.jero.core.model.Note

class GetAllNotesUseCaseImpl(
    private val notesRepository: NotesRepository,
    private val auth: FirebaseAuth
) : GetAllNotesUseCase {
    
    override suspend fun invoke(): Result<List<Note>> {
        val userId = auth.currentUser?.uid 
            ?: return Result.failure(Exception("Usuario no autenticado"))
        
        return notesRepository.getAllNotes(userId)
    }
}
