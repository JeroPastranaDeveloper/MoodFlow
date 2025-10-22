package com.jero.login

import com.jero.core.utils.emptyString

class LoginViewContract {
    data class UiState(
        val isLoading: Boolean = false,
        val email: String = emptyString(),
        val password: String = emptyString(),
        val emailError: String? = null,
        val passwordError: String? = null,
        val hasToValidateEmail: Boolean = false,
        val hasToValidatePassword: Boolean = false,
        val isPasswordVisible: Boolean = false,
    )

    sealed class UiIntent {
        data class OnEmailChanged(val email: String) : UiIntent()
        data class OnPasswordChanged(val password: String) : UiIntent()
        data class OnChangePasswordVisibility(val visible: Boolean) : UiIntent()
        data object OnEmailLoginClicked : UiIntent()
        data object OnLoginWithGoogleClicked : UiIntent()
        data object OnSignUpClicked : UiIntent()
    }

    sealed class UiAction {
        data object GoHome : UiAction()
        data object GoRegister : UiAction()
        data class ShowToast(val message: String) : UiAction()
    }
}
