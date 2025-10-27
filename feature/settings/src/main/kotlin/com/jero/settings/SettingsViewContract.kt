package com.jero.settings

class SettingsViewContract {
    data class UiState(
        val isCloseSessionDialogVisible: Boolean = false,
    )

    sealed class UiIntent {
        data object OnCloseSession : UiIntent()
        data object OnGoBack : UiIntent()
        data object OnChangeCloseSessionDialogVisibility : UiIntent()
    }

    sealed class UiAction {
        data object GoLogin : UiAction()
        data object GoBack : UiAction()

        data class ShowToast(val message: String) : UiAction()
    }
}