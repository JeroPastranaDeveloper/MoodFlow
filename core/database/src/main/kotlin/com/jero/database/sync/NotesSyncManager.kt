package com.jero.database.sync

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.jero.database.datasource.NotesDataSource
import com.jero.database.mapper.toEntity
import com.jero.database.model.NoteDto
import com.jero.localdatabase.dao.NoteDao
import java.util.concurrent.TimeUnit

class NotesSyncManager(
    private val notesDataSource: NotesDataSource,
    private val notesDao: NoteDao,
    private val workManager: WorkManager,
) {
    suspend fun syncFromFirebase(userId: String) {
        notesDataSource.getAllNotes(userId).collect { result ->
            result.onSuccess { notesDto ->
                val pendingIds = notesDao.getPendingNotes(userId).map { it.id }.toSet()
                val pendingDeletionIds = notesDao.getPendingDeletions(userId).map { it.noteId }.toSet()
                val firebaseIds = notesDto.map { it.id }.toSet()

                deleteStaleLocalNotes(userId, firebaseIds, pendingIds, pendingDeletionIds)
                upsertFirebaseNotes(notesDto, pendingIds, pendingDeletionIds)
            }
        }
    }

    private suspend fun deleteStaleLocalNotes(
        userId: String,
        firebaseIds: Set<String>,
        pendingIds: Set<String>,
        pendingDeletionIds: Set<String>,
    ) {
        notesDao.getNotes(userId)
            .filter { !it.pendingSync && it.id !in firebaseIds && it.id !in pendingIds && it.id !in pendingDeletionIds }
            .forEach { notesDao.deleteNote(it.id) }
    }

    private suspend fun upsertFirebaseNotes(
        notesDto: List<NoteDto>,
        pendingIds: Set<String>,
        pendingDeletionIds: Set<String>,
    ) {
        val notesToInsert = notesDto
            .filter { it.id !in pendingIds && it.id !in pendingDeletionIds }
            .map { dto ->
                val localColor = if (dto.color == 0L) notesDao.getNoteById(dto.id)?.color ?: 0L else dto.color
                dto.toEntity().copy(pendingSync = false, color = localColor)
            }

        if (notesToInsert.isNotEmpty()) notesDao.insertNotes(notesToInsert)
    }

    fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<com.jero.database.workmanager.SyncNotesWorker>()
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
