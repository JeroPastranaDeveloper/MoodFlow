package com.example.domain.usecase.notes.implementations

import com.example.domain.providers.StringsProvider
import com.example.domain.repository.NotesRepository
import com.example.domain.usecase.notes.interfaces.UpdateNoteUseCase
import com.jero.core.designsystem.R
import com.jero.core.model.Note

class UpdateNoteUseCaseImpl(
    private val notesRepository: NotesRepository,
    private val stringsProvider: StringsProvider,
) : UpdateNoteUseCase {
    
    override suspend fun invoke(note: Note): Result<Note> {
        if (note.title.isBlank()) {
            return Result.failure(Exception(stringsProvider(R.string.title_cannot_be_empty)))
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
