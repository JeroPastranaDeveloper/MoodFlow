package com.jero.home

import androidx.lifecycle.viewModelScope
import com.example.domain.preferences.PreferencesHandler
import com.example.domain.usecase.notes.interfaces.DeleteNoteUseCase
import com.example.domain.usecase.notes.interfaces.GetAllNotesUseCase
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
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val closeSessionUseCase: SignOutUseCase,
) : BaseViewModelWithActions<UiState, UiIntent, UiAction>() {

    override val initialViewState = UiState()
    override suspend fun manageIntent(intent: UiIntent) {
        when (intent) {
            is UiIntent.OnSearchQueryChanged -> searchNotes(intent.query)

            is UiIntent.OnGoEditNoteScreen -> showEditNoteDialog(intent.noteId)
            is UiIntent.OnShowDeleteNoteDialog -> showDeleteNoteDialog(intent.noteId)

            UiIntent.OnDeleteNote -> deleteNote()
            UiIntent.OnCloseSession -> signOut()

            UiIntent.OnChangeDeleteNoteDialogVisibility -> setState {
                copy(
                    showDeleteNoteDialog = !showDeleteNoteDialog,
                    selectedNoteData = Note()
                )
            }
        }
    }

    init {
        observeNotes()
    }

    private fun showDeleteNoteDialog(noteId: String) {
        val note = state.value.allNotes.find { it.id == noteId } ?: Note()
        setState { copy(showDeleteNoteDialog = true, selectedNoteData = note) }
    }

    private fun showEditNoteDialog(noteId: String?) {
        val note = state.value.allNotes.find { it.id == noteId } ?: Note()
        dispatchAction(UiAction.GoEditNoteScreen(note))
    }

    private fun deleteNote() {
        viewModelScope.launch {
            val result = deleteNoteUseCase(state.value.selectedNoteData.id)

            result.fold(
                onSuccess = {
                    setState { copy(selectedNoteData = Note(), showDeleteNoteDialog = false) }
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

    private fun observeNotes() {
        viewModelScope.launch {
            getAllNotesUseCase().collect { allNotes ->
                val sortedNotes = allNotes.sortedByDescending { it.date }
                val otherNotes = sortedNotes.filter { !it.pinned }
                val pinnedNotes = sortedNotes.filter { it.pinned }
                setState {
                    copy(
                        allNotes = sortedNotes,
                        notes = otherNotes,
                        pinnedNotes = pinnedNotes,
                    )
                }

                searchNotes(state.value.query)
            }
        }
    }

    private fun searchNotes(query: String) {
        val notes = state.value.allNotes.filter {
            it.title.contains(query, true)
        }

        setState { copy(query = query, filteredNotes = notes) }
    }
}
