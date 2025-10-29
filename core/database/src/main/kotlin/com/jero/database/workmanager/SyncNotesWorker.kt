package com.jero.database.workmanager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.domain.repository.NotesRepository
import org.koin.core.component.KoinComponent

class SyncNotesWorker(
    context: Context,
    params: WorkerParameters,
    private val notesRepository: NotesRepository,
) : CoroutineWorker(context, params), KoinComponent {

    override suspend fun doWork(): Result {
        return try {
            Log.d("SyncNotesWorker", "Starting sync...")
            notesRepository.syncPendingChanges()
            Log.d("SyncNotesWorker", "Sync completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncNotesWorker", "Error syncing notes", e)
            Result.retry()
        }
    }
}
