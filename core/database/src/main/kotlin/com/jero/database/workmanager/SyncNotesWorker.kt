package com.jero.database.workmanager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.domain.repository.NotesRepository
import com.example.domain.repository.TagRepository
import com.google.firebase.auth.FirebaseAuth
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncNotesWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

    private val notesRepository: NotesRepository by inject()
    private val tagRepository: TagRepository by inject()
    private val auth: FirebaseAuth by inject()

    override suspend fun doWork(): Result {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.success()
            Log.d("SyncNotesWorker", "Starting sync...")
            tagRepository.syncPendingChanges(userId)
            notesRepository.syncPendingChanges()
            Log.d("SyncNotesWorker", "Sync completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncNotesWorker", "Error syncing", e)
            Result.retry()
        }
    }
}
