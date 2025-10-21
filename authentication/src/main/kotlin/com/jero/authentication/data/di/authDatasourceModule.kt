package com.jero.authentication.data.di

import com.example.domain.repository.AuthRepository
import com.jero.authentication.data.datasource.FirebaseAuthDataSource
import com.jero.authentication.data.datasource.FirebaseAuthDataSourceImpl
import com.jero.authentication.data.repository.AuthRepositoryImpl
import org.koin.dsl.module

val authDatasourceModule = module {
    single<FirebaseAuthDataSource> { FirebaseAuthDataSourceImpl() }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
}
