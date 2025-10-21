package com.jero.login

import com.example.domain.preferences.PreferencesHandler
import com.jero.core.viewmodel.BaseViewModelWithActions
import com.jero.login.LoginViewContract.UiAction
import com.jero.login.LoginViewContract.UiIntent
import com.jero.login.LoginViewContract.UiState

class LoginViewModel(
    private val preferencesHandler: PreferencesHandler,
): BaseViewModelWithActions<UiState, UiIntent, UiAction>() {
    override val initialViewState = UiState()
    override suspend fun manageIntent(intent: UiIntent) {
        when (intent) {
            is UiIntent.OnEmailChanged -> setEmail(intent.email)
            is UiIntent.OnPasswordChanged -> setPassword(intent.password)
            is UiIntent.OnChangePasswordVisibility -> changePasswordVisibility(intent.visible)
            UiIntent.OnLoginClicked -> {}
        }
    }

    private fun setEmail(email: String) {
        setState { copy(email = email) }
    }

    private fun setPassword(password: String) {
        setState { copy(password = password) }
    }

    private fun changePasswordVisibility(visible: Boolean) {
        setState { copy(isPasswordVisible = visible) }
    }
}