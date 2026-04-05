package com.example.domain.usecase.notes.implementations

import com.example.domain.repository.NotesRepository
import com.example.domain.usecase.notes.interfaces.GetTrashedNotesUseCase
import com.google.firebase.auth.FirebaseAuth
import com.jero.core.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetTrashedNotesUseCaseImpl(
    private val notesRepository: NotesRepository,
    private val auth: FirebaseAuth,
) : GetTrashedNotesUseCase {

    override suspend fun invoke(): Flow<List<Note>> {
        val userId = auth.currentUser?.uid ?: return flowOf(emptyList())
        return notesRepository.getDeletedNotes(userId)
    }
}
