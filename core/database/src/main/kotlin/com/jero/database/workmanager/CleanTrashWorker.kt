package com.jero.database.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.domain.repository.NotesRepository

class CleanTrashWorker(
    context: Context,
    params: WorkerParameters,
    private val notesRepository: NotesRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            notesRepository.cleanOldTrash()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
