package com.jero.home

import androidx.lifecycle.viewModelScope
import com.example.domain.preferences.PreferencesHandler
import com.example.domain.usecase.notes.CreateNoteUseCase
import com.example.domain.usecase.notes.GetAllNotesUseCase
import com.example.domain.usecase.user.SignOutUseCase
import com.jero.core.model.Note
import com.jero.core.viewmodel.BaseViewModelWithActions
import com.jero.home.HomeViewContract.UiAction
import com.jero.home.HomeViewContract.UiIntent
import com.jero.home.HomeViewContract.UiState
import kotlinx.coroutines.launch

class HomeViewModel(
    private val preferencesHandler: PreferencesHandler,
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val closeSessionUseCase: SignOutUseCase,
) : BaseViewModelWithActions<UiState, UiIntent, UiAction>() {

    override val initialViewState = UiState()
    override suspend fun manageIntent(intent: UiIntent) {
        when (intent) {
            is UiIntent.OnSearchQueryChanged -> searchNotes(intent.query)
            UiIntent.OnCreateNote -> createNote()
            UiIntent.OnCloseSession -> signOut()
        }
    }

    init {
        getAllNotes()
    }

    private fun signOut() {
        viewModelScope.launch {
            val result = closeSessionUseCase()

            result.fold(
                onSuccess = {
                    preferencesHandler.isLogged = false
                    dispatchAction(UiAction.GoHome)
                },
                onFailure = {
                    dispatchAction(UiAction.ShowToast(it.message.orEmpty()))
                }
            )
        }
    }

    private fun createNote() {
        val note = Note(
            title = "Patatuelas",
            content = "",
        )
        viewModelScope.launch {
            val result = createNoteUseCase(note)

            result.fold(
                onSuccess = {
                    dispatchAction(UiAction.ShowToast("Note created"))
                    getAllNotes()
                },
                onFailure = {
                    dispatchAction(UiAction.ShowToast(it.message.orEmpty()))
                }
            )
        }
    }

    private fun getAllNotes() {
        viewModelScope.launch {
            val result = getAllNotesUseCase()

            result.fold(
                onSuccess = {
                    setState { copy(notes = it) }
                },
                onFailure = {
                    dispatchAction(UiAction.ShowToast(it.message.orEmpty()))
                }
            )
        }
    }

    private fun searchNotes(query: String) {
        /* meter delay al buscar */
        setState { copy(query = query) }
    }
}
