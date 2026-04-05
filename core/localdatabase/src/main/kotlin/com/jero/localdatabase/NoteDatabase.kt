package com.jero.localdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jero.localdatabase.dao.NoteDao
import com.jero.localdatabase.dao.TagDao
import com.jero.localdatabase.model.NoteEntity
import com.jero.localdatabase.model.NoteTagCrossRef
import com.jero.localdatabase.model.PendingDeletionEntity
import com.jero.localdatabase.model.PendingTagDeletionEntity
import com.jero.localdatabase.model.TagEntity

@Database(
    entities = [
        NoteEntity::class,
        PendingDeletionEntity::class,
        TagEntity::class,
        NoteTagCrossRef::class,
        PendingTagDeletionEntity::class,
    ],
    version = 6,
    exportSchema = true
)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun tagDao(): TagDao
}
