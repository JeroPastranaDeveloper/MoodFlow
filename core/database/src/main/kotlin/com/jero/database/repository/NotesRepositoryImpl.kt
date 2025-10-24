package com.jero.database.repository

import com.example.domain.repository.NotesRepository
import com.google.firebase.auth.FirebaseAuth
import com.jero.core.model.Note
import com.jero.database.datasource.NotesDataSource
import com.jero.database.mapper.toDomain
import com.jero.database.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotesRepositoryImpl(
    private val notesDataSource: NotesDataSource,
    private val auth: FirebaseAuth
) : NotesRepository {

    private fun getCurrentUserId(): String? = auth.currentUser?.uid

    override suspend fun createNote(note: Note): Result<Note> =
        notesDataSource.createNote(note.toEntity())
            .map { it.toDomain() }

    override suspend fun updateNote(note: Note): Result<Note> =
        notesDataSource.updateNote(note.toEntity())
            .map { it.toDomain() }

    override suspend fun deleteNote(noteId: String): Result<Unit> {
        val userId = getCurrentUserId()
            ?: return Result.failure(Exception("Usuario no autenticado"))

        return notesDataSource.deleteNote(noteId, userId)
    }

    override suspend fun getNote(noteId: String): Result<Note> {
        val userId = getCurrentUserId()
            ?: return Result.failure(Exception("Usuario no autenticado"))

        return notesDataSource.getNote(noteId, userId)
            .map { it.toDomain() }
    }

    override fun getAllNotes(userId: String): Flow<Result<List<Note>>> =
        notesDataSource.getAllNotes(userId)
            .map { result ->
                result.map { notes -> notes.map { it.toDomain() } }
            }
}
