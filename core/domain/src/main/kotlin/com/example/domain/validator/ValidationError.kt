package com.example.domain.validator

sealed class ValidationError {
    sealed class EmailError : ValidationError() {
        data object Empty : EmailError()
        data object InvalidFormat : EmailError()
        data object TooLong : EmailError()
    }

    sealed class PasswordError : ValidationError() {
        data object Empty : PasswordError()
        data object TooShort : PasswordError()
        data object NoUppercase : PasswordError()
        data object NoLowercase : PasswordError()
        data object NoDigit : PasswordError()
        data object NoSpecialChar : PasswordError()
        data object ContainsWhitespace : PasswordError()
    }
}
