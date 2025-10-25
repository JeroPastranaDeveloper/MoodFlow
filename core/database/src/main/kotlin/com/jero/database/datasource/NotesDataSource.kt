package com.jero.database.datasource

import com.jero.database.model.NoteDto
import kotlinx.coroutines.flow.Flow

interface NotesDataSource {
    suspend fun createNote(note: NoteDto): Result<NoteDto>
    suspend fun updateNote(note: NoteDto): Result<NoteDto>
    suspend fun deleteNote(noteId: String, userId: String): Result<Unit>
    suspend fun getNote(noteId: String, userId: String): Result<NoteDto>
    fun getAllNotes(userId: String): Flow<Result<List<NoteDto>>>
    suspend fun syncNote(userId: String, note: NoteDto): Result<Unit>
}
