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
