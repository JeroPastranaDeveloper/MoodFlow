package com.jero.database.di

import com.example.domain.repository.NotesRepository
import com.google.firebase.database.FirebaseDatabase
import com.jero.database.datasource.NotesDataSource
import com.jero.database.datasource.NotesDataSourceImpl
import com.jero.database.repository.NotesRepositoryImpl
import org.koin.dsl.module

val notesDatasourceModule = module {
    
    // Firebase Database
    single { FirebaseDatabase.getInstance() }
    
    // Data Source
    single<NotesDataSource> {
        NotesDataSourceImpl(get(), get())
    }
    
    // Repository
    single<NotesRepository> {
        NotesRepositoryImpl(get(), get(), get(), get())
    }
}
