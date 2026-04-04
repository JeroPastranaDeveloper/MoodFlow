package com.jero.localdatabase.di

import androidx.room.Room
import com.jero.localdatabase.NoteDatabase
import com.jero.localdatabase.migrations.MIGRATION_2_3
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val localDatabaseModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            NoteDatabase::class.java,
            "Notes.db"
        )
            .addMigrations(MIGRATION_2_3)
            .fallbackToDestructiveMigrationFrom(dropAllTables = true, 1)
            .build()
    }

    single { get<NoteDatabase>().noteDao() }
}
