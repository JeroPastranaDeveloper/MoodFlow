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

        val randomId = UUID.randomUUID().toString()
        val date = System.currentTimeMillis()
        val finalId = "${randomId}_${date}"

        val noteWithId = note.copy(
            id = finalId,
            title = note.title.trim(),
            content = note.content.trim(),
            date = date,
            pinned = note.pinned,
            userId = userId
        )

        return notesRepository.createNote(noteWithId)
    }
}
