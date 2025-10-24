package com.jero.data.usecase.notes

import com.example.domain.repository.NotesRepository
import com.example.domain.usecase.notes.GetAllNotesUseCase
import com.google.firebase.auth.FirebaseAuth
import com.jero.core.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetAllNotesUseCaseImpl(
    private val notesRepository: NotesRepository,
    private val auth: FirebaseAuth
) : GetAllNotesUseCase {

    override fun invoke(): Flow<Result<List<Note>>> {
        val userId = auth.currentUser?.uid
            ?: return flowOf(Result.failure(Exception("Usuario no autenticado")))

        return notesRepository.getAllNotes(userId)
    }
}
