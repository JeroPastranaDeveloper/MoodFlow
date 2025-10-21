package com.example.domain.validator

interface PasswordValidator {
    fun validate(password: String): ValidationError.PasswordError?
    fun getErrorMessage(error: ValidationError.PasswordError): String
}

class PasswordValidatorImpl : PasswordValidator {
    
    companion object {
        private const val MIN_LENGTH = 8
        private val UPPERCASE_REGEX = "[A-Z]".toRegex()
        private val LOWERCASE_REGEX = "[a-z]".toRegex()
        private val DIGIT_REGEX = "\\d".toRegex()
        private val SPECIAL_CHAR_REGEX = "[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]".toRegex()
        private val WHITESPACE_REGEX = "\\s".toRegex()
    }
    
    override fun validate(password: String): ValidationError.PasswordError? {
        return when {
            password.isEmpty() -> ValidationError.PasswordError.Empty
            password.length < MIN_LENGTH -> ValidationError.PasswordError.TooShort
            WHITESPACE_REGEX.containsMatchIn(password) -> ValidationError.PasswordError.ContainsWhitespace
            !UPPERCASE_REGEX.containsMatchIn(password) -> ValidationError.PasswordError.NoUppercase
            !LOWERCASE_REGEX.containsMatchIn(password) -> ValidationError.PasswordError.NoLowercase
            !DIGIT_REGEX.containsMatchIn(password) -> ValidationError.PasswordError.NoDigit
            !SPECIAL_CHAR_REGEX.containsMatchIn(password) -> ValidationError.PasswordError.NoSpecialChar
            else -> null
        }
    }

    override fun getErrorMessage(error: ValidationError.PasswordError): String =
        when (error) {
            ValidationError.PasswordError.Empty -> "La contraseña no puede estar vacía"
            ValidationError.PasswordError.TooShort -> "La contraseña debe tener al menos 8 caracteres"
            ValidationError.PasswordError.NoUppercase -> "Debe incluir al menos una mayúscula"
            ValidationError.PasswordError.NoLowercase -> "Debe incluir al menos una minúscula"
            ValidationError.PasswordError.NoDigit -> "Debe incluir al menos un número"
            ValidationError.PasswordError.NoSpecialChar -> "Debe incluir al menos un carácter especial (!@#$%...)"
            ValidationError.PasswordError.ContainsWhitespace -> "La contraseña no puede contener espacios"
        }
}
