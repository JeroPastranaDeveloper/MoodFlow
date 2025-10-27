package com.example.domain.validator

import com.example.domain.providers.StringsProvider
import com.jero.core.designsystem.R

interface EmailValidator {
    fun validate(email: String): ValidationError.EmailError?
    fun getErrorMessage(error: ValidationError.EmailError): String
}

class EmailValidatorImpl(
    private val stringsProvider: StringsProvider,
) : EmailValidator {
    
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
            ValidationError.EmailError.Empty -> stringsProvider(R.string.validation_error_email_empty)
            ValidationError.EmailError.TooLong -> stringsProvider(R.string.validation_error_email_too_short, MAX_EMAIL_LENGTH)
            ValidationError.EmailError.InvalidFormat -> stringsProvider(R.string.validation_error_email_invalid_format)
        }
}
