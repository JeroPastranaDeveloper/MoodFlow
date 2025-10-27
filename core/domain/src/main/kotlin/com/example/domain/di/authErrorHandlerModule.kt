package com.example.domain.di

import com.example.domain.handler.AuthErrorHandler
import com.example.domain.handler.AuthErrorHandlerImpl
import org.koin.dsl.module

val authErrorHandlerModule = module {
    single<AuthErrorHandler> {
        AuthErrorHandlerImpl(get())
    }
}
