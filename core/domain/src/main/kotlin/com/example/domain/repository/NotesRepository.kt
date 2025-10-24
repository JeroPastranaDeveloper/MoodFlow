package com.example.domain.repository

import com.jero.core.model.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    suspend fun createNote(note: Note): Result<Note>
    suspend fun updateNote(note: Note): Result<Note>
    suspend fun deleteNote(noteId: String): Result<Unit>
    suspend fun getNote(noteId: String): Result<Note>
    fun getAllNotes(userId: String): Flow<Result<List<Note>>>
}
