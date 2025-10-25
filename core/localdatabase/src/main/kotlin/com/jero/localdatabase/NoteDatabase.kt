package com.jero.localdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jero.localdatabase.dao.NoteDao
import com.jero.localdatabase.model.NoteEntity
import com.jero.localdatabase.model.PendingDeletionEntity

@Database(
    entities = [
        NoteEntity::class,
        PendingDeletionEntity::class
    ],
    version = 3,
    exportSchema = true
)

abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
