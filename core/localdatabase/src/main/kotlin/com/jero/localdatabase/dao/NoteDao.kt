package com.jero.localdatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jero.localdatabase.model.NoteEntity
import com.jero.localdatabase.model.PendingDeletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(noteEntity: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)

    // Returns all notes (including trashed) — used by sync
    @Query("SELECT * FROM NoteEntity WHERE userId = :userId")
    suspend fun getNotes(userId: String): List<NoteEntity>

    // Returns only active (non-trashed) notes — used by home screen
    @Query("SELECT * FROM NoteEntity WHERE userId = :userId AND deletedAt IS NULL")
    fun getNotesFlow(userId: String): Flow<List<NoteEntity>>

    // Returns only trashed notes — used by trash screen
    @Query("SELECT * FROM NoteEntity WHERE userId = :userId AND deletedAt IS NOT NULL")
    fun getDeletedNotesFlow(userId: String): Flow<List<NoteEntity>>

    // Returns trashed notes older than cutoffTime — used by CleanTrashWorker
    @Query("SELECT * FROM NoteEntity WHERE userId = :userId AND deletedAt IS NOT NULL AND deletedAt <= :cutoffTime")
    suspend fun getOldTrashedNotes(userId: String, cutoffTime: Long): List<NoteEntity>

    @Query("SELECT * FROM NoteEntity WHERE id = :id")
    suspend fun getNoteById(id: String): NoteEntity?

    @Query("DELETE FROM NoteEntity WHERE id = :id")
    suspend fun deleteNote(id: String)

    @Query("UPDATE NoteEntity SET deletedAt = :deletedAt, pendingSync = 1 WHERE id = :id")
    suspend fun softDeleteNote(id: String, deletedAt: Long)

    @Query("UPDATE NoteEntity SET deletedAt = NULL, pendingSync = 1 WHERE id = :id")
    suspend fun restoreNote(id: String)

    @Query("SELECT * FROM NoteEntity WHERE pendingSync = 1 AND userId = :userId")
    suspend fun getPendingNotes(userId: String): List<NoteEntity>

    @Query("UPDATE NoteEntity SET pendingSync = 0 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingDeletion(deletion: PendingDeletionEntity)

    @Query("SELECT * FROM PendingDeletionEntity WHERE userId = :userId")
    suspend fun getPendingDeletions(userId: String): List<PendingDeletionEntity>

    @Query("DELETE FROM PendingDeletionEntity WHERE noteId = :noteId")
    suspend fun removePendingDeletion(noteId: String)

    @Query("DELETE FROM PendingDeletionEntity WHERE userId = :userId")
    suspend fun clearPendingDeletions(userId: String)
}
