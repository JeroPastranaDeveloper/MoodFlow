package com.jero.database.repository

import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.domain.repository.NotesRepository
import com.google.firebase.auth.FirebaseAuth
import com.jero.core.model.Note
import com.jero.database.datasource.NotesDataSource
import com.jero.database.mapper.toDomain
import com.jero.database.mapper.toDto
import com.jero.database.mapper.toEntity
import com.jero.database.workmanager.SyncNotesWorker
import com.jero.localdatabase.dao.NoteDao
import com.jero.localdatabase.model.PendingDeletionEntity
import com.jero.network.NetworkMonitor
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class NotesRepositoryImpl(
    private val notesDataSource: NotesDataSource,
    private val auth: FirebaseAuth,
    private val notesDao: NoteDao,
    private val networkMonitor: NetworkMonitor,
    private val workManager: WorkManager
) : NotesRepository {

    private fun getCurrentUserId(): String? = auth.currentUser?.uid

    override suspend fun getAllNotes(userId: String) = channelFlow {
        launch {
            notesDao.getNotesFlow(userId).collect { entities ->
                send(entities.map { it.toDomain() })
            }
        }

        launch {
            networkMonitor.observeConnectivity()
                .filter { it }
                .collect {
                    scheduleSyncWork()
                }
        }

        launch {
            notesDataSource.getAllNotes(userId).collect { result ->
                result.onSuccess { notesDto ->
                    val pendingIds = notesDao.getPendingNotes(userId).map { it.id }.toSet()
                    val pendingDeletionIds = notesDao.getPendingDeletions(userId).map { it.noteId }.toSet()

                    val firebaseIds = notesDto.map { it.id }.toSet()

                    val localNotes = notesDao.getNotes(userId).filter { !it.pendingSync }

                    localNotes.forEach { localNote ->
                        if (localNote.id !in firebaseIds &&
                            localNote.id !in pendingIds &&
                            localNote.id !in pendingDeletionIds) {
                            notesDao.deleteNote(localNote.id)
                        }
                    }

                    val notesToInsert = notesDto
                        .filter { it.id !in pendingIds && it.id !in pendingDeletionIds }
                        .map { dto ->
                            dto.toEntity().copy(
                                pendingSync = false,
                            )
                        }

                    if (notesToInsert.isNotEmpty()) {
                        notesDao.insertNotes(notesToInsert)
                    }
                }
            }
        }
    }

    override suspend fun createNote(note: Note): Result<Note> {
        return try {
            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            val entity = note.toEntity().copy(
                userId = userId,
                pendingSync = true
            )
            notesDao.insertNote(entity)

            scheduleSyncWork()

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

            if (networkMonitor.isConnected()) {
                scheduleSyncWork()
            }

            Result.success(note)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNote(noteId: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            if (networkMonitor.isConnected()) {
                val deleteResult = notesDataSource.deleteNote(noteId, userId)
                deleteResult.onSuccess {
                    notesDao.deleteNote(noteId)
                    notesDao.removePendingDeletion(noteId)
                }.onFailure {
                    notesDao.insertPendingDeletion(
                        PendingDeletionEntity(
                            noteId = noteId,
                            userId = userId
                        )
                    )
                    notesDao.deleteNote(noteId)
                    scheduleSyncWork()
                }
                return deleteResult.map { Unit }
            } else {
                notesDao.insertPendingDeletion(
                    PendingDeletionEntity(
                        noteId = noteId,
                        userId = userId
                    )
                )
                notesDao.deleteNote(noteId)

                scheduleSyncWork()

                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNote(noteId: String): Result<Note> {
        return try {
            val entity = notesDao.getNoteById(noteId)
            if (entity != null) {
                return Result.success(entity.toDomain())
            }

            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            notesDataSource.getNote(noteId, userId)
                .map { it.toDomain() }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncPendingChanges() {
        val userId = getCurrentUserId() ?: return

        try {
            val pendingNotes = notesDao.getPendingNotes(userId)
            pendingNotes.forEach { noteEntity ->
                val result = notesDataSource.syncNote(userId, noteEntity.toDto())
                result.onSuccess {
                    notesDao.markAsSynced(noteEntity.id)
                }.onFailure {
                    Log.e("NotesRepository", "Failed to sync note ${noteEntity.id}", it)
                }
            }

            val pendingDeletions = notesDao.getPendingDeletions(userId)
            pendingDeletions.forEach { deletion ->
                val result = notesDataSource.deleteNote(deletion.noteId, userId)
                result.onSuccess {
                    notesDao.removePendingDeletion(deletion.noteId)
                }.onFailure {
                    Log.e("NotesRepository", "Failed to delete note ${deletion.noteId}", it)
                }
            }
        } catch (e: Exception) {
            Log.e("NotesRepository", "Error syncing pending changes", e)
        }
    }

    private fun scheduleSyncWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncNotesWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniqueWork(
            "sync_notes",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }
}
