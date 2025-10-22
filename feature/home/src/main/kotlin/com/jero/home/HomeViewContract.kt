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
        data class OnSearchQueryChanged(val query: String) : UiIntent()
    }

    sealed class UiAction {
    }
}
