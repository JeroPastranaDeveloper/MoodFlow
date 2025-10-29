package com.jero.core.model

sealed class AuthError : Exception() {
    object InvalidCredentials : AuthError()
    object UserNotFound : AuthError()
    object EmailAlreadyInUse : AuthError()
    object WeakPassword : AuthError()
    object NetworkError : AuthError()
    data class Unknown(override val message: String) : AuthError()
}
