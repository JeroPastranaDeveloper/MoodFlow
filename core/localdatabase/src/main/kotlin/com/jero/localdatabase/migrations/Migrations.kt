package com.jero.localdatabase.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `PendingDeletionEntity` (
                `noteId` TEXT NOT NULL,
                `userId` TEXT NOT NULL,
                `date`   INTEGER NOT NULL,
                PRIMARY KEY(`noteId`)
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `NoteEntity` ADD COLUMN `color` INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `NoteEntity` ADD COLUMN `deletedAt` INTEGER DEFAULT NULL")
    }
}
