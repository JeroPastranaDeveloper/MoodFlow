package com.example.domain.handler

import com.jero.core.model.AuthError

interface AuthErrorHandler {
    operator fun invoke(error: Throwable): String
}

class AuthErrorHandlerImpl : AuthErrorHandler {
    override fun invoke(error: Throwable): String {
        val message = when (error) {
            is AuthError.InvalidEmail -> "Email inválido"
            is AuthError.InvalidPassword -> "Contraseña debe tener al menos 6 caracteres"
            is AuthError.UserNotFound -> "Usuario no encontrado"
            is AuthError.EmailAlreadyInUse -> "El email ya está en uso"
            is AuthError.WeakPassword -> "La contraseña es muy débil"
            is AuthError.NetworkError -> "Error de conexión"
            is AuthError.Unknown -> error.message
            else -> "Error desconocido"
        }

        return message
    }
}
