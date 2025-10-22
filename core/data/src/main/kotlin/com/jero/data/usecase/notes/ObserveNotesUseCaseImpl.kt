package com.jero.data.usecase.notes

import com.example.domain.repository.NotesRepository
import com.example.domain.usecase.notes.ObserveNotesUseCase
import com.google.firebase.auth.FirebaseAuth
import com.jero.core.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ObserveNotesUseCaseImpl(
    private val notesRepository: NotesRepository,
    private val auth: FirebaseAuth
) : ObserveNotesUseCase {
    
    override fun invoke(): Flow<List<Note>> {
        val userId = auth.currentUser?.uid
        
        return if (userId != null) {
            notesRepository.observeNotes(userId)
        } else {
            flowOf(emptyList())
        }
    }
}