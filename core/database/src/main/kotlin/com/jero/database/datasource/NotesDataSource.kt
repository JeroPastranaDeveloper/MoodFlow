package com.jero.database.datasource

import com.jero.database.model.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NotesDataSource {
    suspend fun createNote(note: NoteEntity): Result<NoteEntity>
    suspend fun updateNote(note: NoteEntity): Result<NoteEntity>
    suspend fun deleteNote(noteId: String, userId: String): Result<Unit>
    suspend fun getNote(noteId: String, userId: String): Result<NoteEntity>
    fun getAllNotes(userId: String): Flow<Result<List<NoteEntity>>>
}
