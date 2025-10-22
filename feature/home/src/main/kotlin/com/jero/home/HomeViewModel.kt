package com.jero.home

import com.example.domain.usecase.notes.GetAllNotesUseCase
import com.jero.core.viewmodel.BaseViewModelWithActions
import com.jero.home.HomeViewContract.UiAction
import com.jero.home.HomeViewContract.UiIntent
import com.jero.home.HomeViewContract.UiState

class HomeViewModel(
    private val getAllNotesUseCase: GetAllNotesUseCase,
) : BaseViewModelWithActions<UiState, UiIntent, UiAction>() {

    override val initialViewState = UiState()
    override suspend fun manageIntent(intent: UiIntent) {
        when (intent) {
            is UiIntent.OnSearchQueryChanged -> searchNotes(intent.query)
        }
    }

    private fun searchNotes(query: String) {
        /* meter delay al buscar */
        setState { copy(query = query) }
    }
}
