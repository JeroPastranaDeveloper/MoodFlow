package com.example.domain.di

import com.example.domain.usecase.GetCurrentUserUseCase
import com.example.domain.usecase.SendPasswordResetEmailUseCase
import com.example.domain.usecase.SignInWithEmailUseCase
import com.example.domain.usecase.SignOutUseCase
import com.example.domain.usecase.SignUpWithEmailUseCase
import org.koin.dsl.module

val authRepositoryModule = module {
    factory { SignInWithEmailUseCase(get()) }
    factory { SignUpWithEmailUseCase(get()) }
    factory { SignOutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { SendPasswordResetEmailUseCase(get()) }
}
