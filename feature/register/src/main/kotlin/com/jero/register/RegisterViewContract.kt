package com.jero.register

import com.jero.core.utils.emptyString

class RegisterViewContract {
    data class UiState(
        val email: String = emptyString(),
        val password: String = emptyString(),
        val repeatPassword: String = emptyString(),
        val emailError: String? = null,
        val passwordError: String? = null,
        val repeatPasswordError: String? = null,
        val hasToValidateEmail: Boolean = false,
        val hasToValidatePassword: Boolean = false,
        val isPasswordVisible: Boolean = false,
        val isRepeatPasswordVisible: Boolean = false,
    )

    sealed class UiIntent {
        data class OnEmailChanged(val email: String) : UiIntent()
        data class OnPasswordChanged(val password: String) : UiIntent()
        data class OnRepeatPasswordChanged(val repeatPassword: String) : UiIntent()
        data class OnChangePasswordVisibility(val visible: Boolean) : UiIntent()
        data class OnChangeRepeatPasswordVisibility(val visible: Boolean) : UiIntent()
        data object OnLoginWithGoogleClicked : UiIntent()
        data object OnSignUpClicked : UiIntent()
        data object OnGoBack : UiIntent()
    }

    sealed class UiAction {
        data object GoBack : UiAction()
        data object GoHome : UiAction()
        data class ShowToast(val message: String) : UiAction()
    }
}
