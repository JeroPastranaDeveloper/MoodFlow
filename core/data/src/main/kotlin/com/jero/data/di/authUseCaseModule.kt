package com.jero.data.di

import com.example.domain.usecase.GetCurrentUserUseCase
import com.example.domain.usecase.SendPasswordResetEmailUseCase
import com.example.domain.usecase.SignInWithEmailUseCase
import com.example.domain.usecase.SignOutUseCase
import com.example.domain.usecase.SignUpWithEmailUseCase
import com.jero.data.usecase.GetCurrentUserUseCaseImpl
import com.jero.data.usecase.SendPasswordResetEmailUseCaseImpl
import com.jero.data.usecase.SignInWithEmailUseCaseImpl
import com.jero.data.usecase.SignOutUseCaseImpl
import com.jero.data.usecase.SignUpWithEmailUseCaseImpl
import org.koin.dsl.module

val authUseCaseModule = module {
    factory<SignInWithEmailUseCase> {
        SignInWithEmailUseCaseImpl(get())
    }

    factory<SignUpWithEmailUseCase> {
        SignUpWithEmailUseCaseImpl(get())
    }

    factory<SignOutUseCase> {
        SignOutUseCaseImpl(get())
    }

    factory<GetCurrentUserUseCase> {
        GetCurrentUserUseCaseImpl(get())
    }

    factory<SendPasswordResetEmailUseCase> {
        SendPasswordResetEmailUseCaseImpl(get())
    }
}
