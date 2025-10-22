package com.example.domain.validator

interface EmailValidator {
    fun validate(email: String): ValidationError.EmailError?
    fun getErrorMessage(error: ValidationError.EmailError): String
}

class EmailValidatorImpl : EmailValidator {
    
    companion object {
        private const val MAX_EMAIL_LENGTH = 254
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    }
    
    override fun validate(email: String): ValidationError.EmailError? =
        when {
            email.isBlank() -> ValidationError.EmailError.Empty
            email.length > MAX_EMAIL_LENGTH -> ValidationError.EmailError.TooLong
            !EMAIL_REGEX.matches(email) -> ValidationError.EmailError.InvalidFormat
            else -> null
        }

    override fun getErrorMessage(error: ValidationError.EmailError): String =
        when (error) {
            ValidationError.EmailError.Empty -> "Email cannot be empty"
            ValidationError.EmailError.TooLong -> "Email cannot be longer than $MAX_EMAIL_LENGTH characters"
            ValidationError.EmailError.InvalidFormat -> "Invalid email format"
        }
}
