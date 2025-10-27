package com.example.domain.usecase.notes.implementations

import com.example.domain.providers.StringsProvider
import com.example.domain.repository.NotesRepository
import com.example.domain.usecase.notes.interfaces.CreateNoteUseCase
import com.google.firebase.auth.FirebaseAuth
import com.jero.core.designsystem.R
import com.jero.core.model.Note
import java.util.UUID

class CreateNoteUseCaseImpl(
    private val notesRepository: NotesRepository,
    private val auth: FirebaseAuth,
    private val stringsProvider: StringsProvider,
) : CreateNoteUseCase {

    override suspend fun invoke(note: Note): Result<Note> {
        val userId = auth.currentUser?.uid
            ?: return Result.failure(Exception(stringsProvider(R.string.user_not_authenticated)))

        if (note.title.isBlank()) {
            return Result.failure(Exception(stringsProvider(R.string.title_cannot_be_empty)))
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
