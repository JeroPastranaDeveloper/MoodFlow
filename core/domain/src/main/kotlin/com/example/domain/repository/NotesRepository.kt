package com.example.domain.repository

import com.jero.core.model.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    suspend fun createNote(note: Note): Result<Note>
    suspend fun updateNote(note: Note): Result<Note>
    suspend fun deleteNote(noteId: String): Result<Unit>
    suspend fun getNote(noteId: String): Result<Note>
    suspend fun getAllNotes(userId: String): Flow<List<Note>>
    suspend fun getDeletedNotes(userId: String): Flow<List<Note>>
    suspend fun restoreNote(noteId: String): Result<Unit>
    suspend fun permanentlyDeleteNote(noteId: String): Result<Unit>
    suspend fun syncPendingChanges()
    suspend fun cleanOldTrash()
}
