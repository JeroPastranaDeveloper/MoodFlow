package com.jero.settings

import androidx.lifecycle.viewModelScope
import com.example.domain.preferences.PreferencesHandler
import com.example.domain.providers.StringsProvider
import com.example.domain.usecase.user.SignOutUseCase
import com.jero.core.designsystem.R
import com.jero.core.viewmodel.BaseViewModelWithActions
import com.jero.settings.SettingsViewContract.UiAction
import com.jero.settings.SettingsViewContract.UiIntent
import com.jero.settings.SettingsViewContract.UiState
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val closeSessionUseCase: SignOutUseCase,
    private val preferencesHandler: PreferencesHandler,
    private val stringsProvider: StringsProvider,
) : BaseViewModelWithActions<UiState, UiIntent, UiAction>() {

    override val initialViewState = UiState()
    override suspend fun manageIntent(intent: UiIntent) {
        when(intent) {
            UiIntent.OnCloseSession -> closeSession()
            UiIntent.OnGoBack -> dispatchAction(UiAction.GoBack)
            UiIntent.OnChangeCloseSessionDialogVisibility -> setState { copy(isCloseSessionDialogVisible = !isCloseSessionDialogVisible) }
        }
    }

    private fun closeSession() {
        viewModelScope.launch {
            val result = closeSessionUseCase()

            setState { copy(isCloseSessionDialogVisible = false) }

            result.fold(
                onSuccess = {
                    preferencesHandler.isLogged = false
                    dispatchAction(UiAction.GoLogin)
                },
                onFailure = {
                    dispatchAction(UiAction.ShowToast(it.message ?: stringsProvider(R.string.unknown_error)))
                }
            )
        }
    }
}
