package com.jero.home

import com.jero.core.model.Note
import com.jero.core.utils.emptyString

class HomeViewContract {
    data class UiState(
        val isLoading: Boolean = false,
        val notes: List<Note> = emptyList(),
        val query: String = emptyString(),
    )

    sealed class UiIntent {
        data object OnCloseSession : UiIntent()
        data object OnCreateNote : UiIntent()
        data class OnSearchQueryChanged(val query: String) : UiIntent()
    }

    sealed class UiAction {
        data object GoHome : UiAction()
        data class ShowToast(val message: String) : UiAction()
    }
}
