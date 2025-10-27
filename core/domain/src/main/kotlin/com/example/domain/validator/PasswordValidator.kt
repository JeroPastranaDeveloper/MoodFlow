package com.example.domain.validator

import com.example.domain.providers.StringsProvider
import com.jero.core.designsystem.R

interface PasswordValidator {
    fun validate(password: String): ValidationError.PasswordError?
    fun getErrorMessage(error: ValidationError.PasswordError): String
}

class PasswordValidatorImpl(
    private val stringsProvider: StringsProvider,
) : PasswordValidator {

    companion object {
        private const val MIN_LENGTH = 8
        private val UPPERCASE_REGEX = "[A-Z]".toRegex()
        private val LOWERCASE_REGEX = "[a-z]".toRegex()
        private val DIGIT_REGEX = "\\d".toRegex()
        private val SPECIAL_CHAR_REGEX = "[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]".toRegex()
        private val WHITESPACE_REGEX = "\\s".toRegex()
    }

    override fun validate(password: String): ValidationError.PasswordError? =
        when {
            password.isEmpty() -> ValidationError.PasswordError.Empty
            password.length < MIN_LENGTH -> ValidationError.PasswordError.TooShort
            WHITESPACE_REGEX.containsMatchIn(password) -> ValidationError.PasswordError.ContainsWhitespace
            !UPPERCASE_REGEX.containsMatchIn(password) -> ValidationError.PasswordError.NoUppercase
            !LOWERCASE_REGEX.containsMatchIn(password) -> ValidationError.PasswordError.NoLowercase
            !DIGIT_REGEX.containsMatchIn(password) -> ValidationError.PasswordError.NoDigit
            !SPECIAL_CHAR_REGEX.containsMatchIn(password) -> ValidationError.PasswordError.NoSpecialChar
            else -> null
        }

    override fun getErrorMessage(error: ValidationError.PasswordError): String =
        when (error) {
            ValidationError.PasswordError.Empty -> stringsProvider(R.string.validation_error_password_empty)
            ValidationError.PasswordError.TooShort -> stringsProvider(R.string.validation_error_password_too_short)
            ValidationError.PasswordError.NoUppercase -> stringsProvider(R.string.validation_error_password_no_uppercase)
            ValidationError.PasswordError.NoLowercase -> stringsProvider(R.string.validation_error_password_no_lowercase)
            ValidationError.PasswordError.NoDigit -> stringsProvider(R.string.validation_error_password_no_digit)
            ValidationError.PasswordError.NoSpecialChar -> stringsProvider(R.string.validation_error_password_no_special_char)
            ValidationError.PasswordError.ContainsWhitespace -> stringsProvider(R.string.validation_error_password_contains_whitespace)
        }
}
