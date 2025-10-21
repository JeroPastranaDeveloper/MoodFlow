package com.jero.login

import com.jero.core.utils.emptyString

class LoginViewContract {
    data class UiState(
        val isLoading: Boolean = false,
        val email: String = emptyString(),
        val password: String = emptyString(),
        val isPasswordVisible: Boolean = false,
    )

    sealed class UiIntent {
        data class OnEmailChanged(val email: String) : UiIntent()
        data class OnPasswordChanged(val password: String) : UiIntent()
        data object OnChangePasswordVisibility : UiIntent()
        data object OnLoginClicked : UiIntent()
    }

    sealed class UiAction {}
}
