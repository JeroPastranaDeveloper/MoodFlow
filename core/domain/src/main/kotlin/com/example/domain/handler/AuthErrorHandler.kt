package com.example.domain.handler

import com.example.domain.providers.StringsProvider
import com.jero.core.designsystem.R
import com.jero.core.model.AuthError

interface AuthErrorHandler {
    operator fun invoke(error: Throwable): String
}

class AuthErrorHandlerImpl(private val stringsProvider: StringsProvider) : AuthErrorHandler {
    override fun invoke(error: Throwable): String {
        val message = when (error) {
            is AuthError.InvalidCredentials -> stringsProvider(R.string.invalid_credentials)
            is AuthError.UserNotFound -> stringsProvider(R.string.user_not_found)
            is AuthError.EmailAlreadyInUse -> stringsProvider(R.string.used_email)
            is AuthError.WeakPassword -> stringsProvider(R.string.weak_password)
            is AuthError.NetworkError -> stringsProvider(R.string.connection_error)
            is AuthError.Unknown -> error.message
            else -> stringsProvider(R.string.unknown_error)
        }

        return message
    }
}
