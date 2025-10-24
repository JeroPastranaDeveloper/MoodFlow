package com.jero.localdatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jero.localdatabase.model.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(noteEntity: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)

    @Query("SELECT * FROM NoteEntity")
    suspend fun getNotes(): List<NoteEntity>

    @Query("SELECT * FROM NoteEntity")
    fun getNotesFlow(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM NoteEntity WHERE id = :id")
    suspend fun getNoteById(id: String): NoteEntity?

    @Query("DELETE FROM NoteEntity WHERE id = :id")
    suspend fun deleteNote(id: String)

    @Query("SELECT * FROM NoteEntity WHERE pendingSync = 1")
    suspend fun getPendingNotes(): List<NoteEntity>

    @Query("UPDATE NoteEntity SET pendingSync = 0 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}
