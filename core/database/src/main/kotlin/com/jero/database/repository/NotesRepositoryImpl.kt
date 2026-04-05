package com.jero.database.repository

import android.util.Log
import com.example.domain.repository.NotesRepository
import com.google.firebase.auth.FirebaseAuth
import com.jero.core.model.Note
import com.jero.database.datasource.NotesDataSource
import com.jero.database.mapper.toDomain
import com.jero.database.mapper.toDto
import com.jero.database.mapper.toEntity
import com.jero.database.sync.NotesSyncManager
import com.jero.localdatabase.dao.NoteDao
import com.jero.localdatabase.dao.TagDao
import com.jero.localdatabase.model.PendingDeletionEntity
import com.jero.network.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NotesRepositoryImpl(
    private val notesDataSource: NotesDataSource,
    private val auth: FirebaseAuth,
    private val notesDao: NoteDao,
    private val tagDao: TagDao,
    private val networkMonitor: NetworkMonitor,
    private val syncManager: NotesSyncManager,
) : NotesRepository {

    private fun getCurrentUserId(): String? = auth.currentUser?.uid

    override suspend fun getAllNotes(userId: String) = channelFlow {
        launch {
            notesDao.getNotesWithTagsFlow(userId).collect { notesWithTags ->
                send(notesWithTags.map { it.toDomain() })
            }
        }
        launch {
            networkMonitor.observeConnectivity()
                .filter { it }
                .collect { syncManager.scheduleSync() }
        }
        launch {
            syncManager.syncFromFirebase(userId)
        }
    }

    override suspend fun getDeletedNotes(userId: String): Flow<List<Note>> =
        notesDao.getDeletedNotesWithTagsFlow(userId).map { list -> list.map { it.toDomain() } }

    override suspend fun createNote(note: Note): Result<Note> {
        return try {
            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            val entity = note.toEntity().copy(userId = userId, pendingSync = true)
            notesDao.insertNote(entity)
            syncManager.scheduleSync()

            Result.success(note)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateNote(note: Note): Result<Note> {
        return try {
            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            val entity = note.toEntity().copy(
                userId = userId,
                pendingSync = true,
                date = System.currentTimeMillis()
            )
            notesDao.insertNote(entity)
            syncManager.scheduleSync()

            Result.success(note)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Moves a note to trash (soft delete)
    override suspend fun deleteNote(noteId: String): Result<Unit> {
        return try {
            notesDao.softDeleteNote(noteId, System.currentTimeMillis())
            syncManager.scheduleSync()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun restoreNote(noteId: String): Result<Unit> {
        return try {
            notesDao.restoreNote(noteId)
            syncManager.scheduleSync()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun permanentlyDeleteNote(noteId: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            if (networkMonitor.isConnected()) {
                val deleteResult = notesDataSource.deleteNote(noteId, userId)
                deleteResult.onSuccess {
                    notesDao.deleteNote(noteId)
                    notesDao.removePendingDeletion(noteId)
                }.onFailure {
                    queuePendingDeletion(noteId, userId)
                }
                return deleteResult.map {}
            } else {
                queuePendingDeletion(noteId, userId)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNote(noteId: String): Result<Note> {
        return try {
            val entity = notesDao.getNoteWithTagsById(noteId)
            if (entity != null) return Result.success(entity.toDomain())

            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            notesDataSource.getNote(noteId, userId).map { it.toDomain() }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncPendingChanges() {
        val userId = getCurrentUserId() ?: return

        try {
            notesDao.getPendingNotes(userId).forEach { noteEntity ->
                val tagIds = tagDao.getTagIdsForNote(noteEntity.id)
                notesDataSource.syncNote(userId, noteEntity.toDto(tagIds))
                    .onSuccess { notesDao.markAsSynced(noteEntity.id) }
                    .onFailure { Log.e("NotesRepository", "Failed to sync note ${noteEntity.id}", it) }
            }

            notesDao.getPendingDeletions(userId).forEach { deletion ->
                notesDataSource.deleteNote(deletion.noteId, userId)
                    .onSuccess { notesDao.removePendingDeletion(deletion.noteId) }
                    .onFailure { Log.e("NotesRepository", "Failed to delete note ${deletion.noteId}", it) }
            }
        } catch (e: Exception) {
            Log.e("NotesRepository", "Error syncing pending changes", e)
        }
    }

    override suspend fun cleanOldTrash() {
        val userId = getCurrentUserId() ?: return
        val cutoff = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
        notesDao.getOldTrashedNotes(userId, cutoff).forEach { entity ->
            queuePendingDeletion(entity.id, userId)
        }
    }

    private suspend fun queuePendingDeletion(noteId: String, userId: String) {
        notesDao.insertPendingDeletion(PendingDeletionEntity(noteId = noteId, userId = userId))
        notesDao.deleteNote(noteId)
        syncManager.scheduleSync()
    }
}
