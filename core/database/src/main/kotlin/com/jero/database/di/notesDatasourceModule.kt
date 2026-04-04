package com.jero.database.di

import com.example.domain.repository.NotesRepository
import com.google.firebase.database.FirebaseDatabase
import com.jero.database.datasource.NotesDataSource
import com.jero.database.datasource.NotesDataSourceImpl
import com.jero.database.repository.NotesRepositoryImpl
import com.jero.database.sync.NotesSyncManager
import org.koin.dsl.module

val notesDatasourceModule = module {

    single { FirebaseDatabase.getInstance() }

    single<NotesDataSource> {
        NotesDataSourceImpl(get(), get())
    }

    single {
        NotesSyncManager(get(), get(), get())
    }

    single<NotesRepository> {
        NotesRepositoryImpl(get(), get(), get(), get(), get())
    }
}
