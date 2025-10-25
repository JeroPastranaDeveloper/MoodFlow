package com.jero.localdatabase.di

import androidx.room.Room
import com.jero.localdatabase.NoteDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val localDatabaseModule = module {
    single {
        Room.databaseBuilder(
                androidApplication(),
                NoteDatabase::class.java,
                "Notes.db"
            ).fallbackToDestructiveMigration(true).build() // TODO: Crear migraciones y cambiar a false
    }

    single { get<NoteDatabase>().noteDao() }
}
