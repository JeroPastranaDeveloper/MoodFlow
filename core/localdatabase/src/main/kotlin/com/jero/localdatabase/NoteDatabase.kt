package com.jero.localdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jero.localdatabase.dao.NoteDao
import com.jero.localdatabase.model.NoteEntity

@Database(
    entities = [NoteEntity::class],
    version = 2,
    exportSchema = true
)

abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
