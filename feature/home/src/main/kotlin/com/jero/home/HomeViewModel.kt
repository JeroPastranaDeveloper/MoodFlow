package com.jero.home

import androidx.lifecycle.viewModelScope
import com.example.domain.preferences.PreferencesHandler
import com.example.domain.usecase.notes.CreateNoteUseCase
import com.example.domain.usecase.notes.DeleteNoteUseCase
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
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val closeSessionUseCase: SignOutUseCase,
) : BaseViewModelWithActions<UiState, UiIntent, UiAction>() {

    override val initialViewState = UiState()
    override suspend fun manageIntent(intent: UiIntent) {
        when (intent) {
            is UiIntent.OnSearchQueryChanged -> searchNotes(intent.query)
            is UiIntent.OnDeleteNote -> deleteNote(intent.noteId)
            is UiIntent.OnNewNoteTitleChanged -> setState { copy(newNoteData = newNoteData.copy(title = intent.title)) }
            is UiIntent.OnNewNoteDescriptionChanged -> setState { copy(newNoteData = newNoteData.copy(content = intent.description)) }

            UiIntent.OnCreateNote -> createNote()
            UiIntent.OnCloseSession -> signOut()
            UiIntent.OnChangeNewNoteDialogVisibility -> setState { copy(showNewNoteDialog = !showNewNoteDialog) }
        }
    }

    init {
        getAllNotes()
    }

    private fun deleteNote(noteId: String) {
        viewModelScope.launch {
            val result = deleteNoteUseCase(noteId)

            result.fold(
                onSuccess = {
                    getAllNotes()
                },
                onFailure = {
                    dispatchAction(UiAction.ShowToast(it.message.orEmpty()))
                }
            )
        }
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
        viewModelScope.launch {
            val result = createNoteUseCase(state.value.newNoteData)

            result.fold(
                onSuccess = {
                    getAllNotes()
                },
                onFailure = {
                    dispatchAction(UiAction.ShowToast(it.message.orEmpty()))
                }
            )

            setState { copy(showNewNoteDialog = false, newNoteData = Note()) }
        }
    }

    private fun getAllNotes() {
        viewModelScope.launch {
            val result = getAllNotesUseCase()

            result.fold(
                onSuccess = { allNotes ->
                    val pinnedNotes = allNotes.filter { it.pinned }
                    val otherNotes = allNotes.filter { !it.pinned }
                    setState { copy(allNotes = allNotes, notes = otherNotes, pinnedNotes = pinnedNotes) }
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
