package com.jero.login

import com.example.domain.preferences.PreferencesHandler
import com.jero.login.LoginViewContract.UiState
import com.jero.login.LoginViewContract.UiIntent
import com.jero.login.LoginViewContract.UiAction
import com.jero.core.viewmodel.BaseViewModelWithActions

class LoginViewModel(
    private val preferencesHandler: PreferencesHandler,
): BaseViewModelWithActions<UiState, UiIntent, UiAction>() {
    override val initialViewState = UiState()
    override suspend fun manageIntent(intent: UiIntent) {

    }
}