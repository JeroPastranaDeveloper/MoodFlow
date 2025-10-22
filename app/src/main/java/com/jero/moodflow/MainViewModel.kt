package com.jero.moodflow

import com.example.domain.preferences.PreferencesHandler
import com.jero.core.viewmodel.BaseViewModel
import com.jero.moodflow.MainViewContract.UiIntent
import com.jero.moodflow.MainViewContract.UiState

class MainViewModel(
    private val preferencesHandler: PreferencesHandler,
): BaseViewModel<UiState, UiIntent>() {
    override val initialViewState: UiState = UiState()

    override suspend fun manageIntent(intent: UiIntent) { /* no-op */ }

    init {
        setState { copy(isLogged = preferencesHandler.isLogged) }
    }
}
