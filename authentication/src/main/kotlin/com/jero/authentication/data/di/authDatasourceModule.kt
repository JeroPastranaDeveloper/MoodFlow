package com.jero.authentication.data.di

import com.example.domain.repository.AuthRepository
import com.example.domain.usecase.user.GetGoogleIdTokenUseCase
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.jero.authentication.data.datasource.FirebaseAuthDataSource
import com.jero.authentication.data.datasource.FirebaseAuthDataSourceImpl
import com.jero.authentication.data.repository.AuthRepositoryImpl
import com.jero.authentication.data.usecase.GetGoogleIdTokenUseCaseImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val authDatasourceModule = module {
    single { Firebase.auth }

    single<FirebaseAuthDataSource> {
        FirebaseAuthDataSourceImpl(get())
    }

    single<AuthRepository> {
        AuthRepositoryImpl(get())
    }

    single<GetGoogleIdTokenUseCase> {
        GetGoogleIdTokenUseCaseImpl(androidContext(), get())
    }
}
