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

    @Query("SELECT * FROM NoteEntity WHERE userId = :userId")
    suspend fun getNotes(userId: String): List<NoteEntity>

    @Query("SELECT * FROM NoteEntity WHERE userId = :userId")
    fun getNotesFlow(userId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM NoteEntity WHERE id = :id")
    suspend fun getNoteById(id: String): NoteEntity?

    @Query("DELETE FROM NoteEntity WHERE id = :id")
    suspend fun deleteNote(id: String)

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
