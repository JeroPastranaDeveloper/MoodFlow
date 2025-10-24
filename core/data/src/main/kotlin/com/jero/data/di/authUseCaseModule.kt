package com.jero.data.di

import com.example.domain.usecase.user.GetCurrentUserUseCase
import com.example.domain.usecase.user.SendPasswordResetEmailUseCase
import com.example.domain.usecase.user.SignInWithEmailUseCase
import com.example.domain.usecase.user.SignOutUseCase
import com.example.domain.usecase.user.SignUpWithEmailUseCase
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
