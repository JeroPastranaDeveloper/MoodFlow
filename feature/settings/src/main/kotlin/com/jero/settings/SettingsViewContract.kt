package com.jero.settings

class SettingsViewContract {
    data class UiState(
        val isCloseSessionDialogVisible: Boolean = false,
    )

    sealed class UiIntent {
        data object OnCloseSession : UiIntent()
        data object OnChangeCloseSessionDialogVisibility : UiIntent()
        data object OnGoTagsScreen : UiIntent()
        data object OnGoBack : UiIntent()
    }

    sealed class UiAction {
        data object GoLogin : UiAction()
        data class ShowToast(val message: String) : UiAction()
        data object GoTagsScreen : UiAction()
        data object GoBack : UiAction()
    }
}