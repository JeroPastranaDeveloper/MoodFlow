package com.jero.home

import androidx.lifecycle.viewModelScope
import com.example.domain.preferences.PreferencesHandler
import com.example.domain.usecase.notes.CreateNoteUseCase
import com.example.domain.usecase.notes.DeleteNoteUseCase
import com.example.domain.usecase.notes.GetAllNotesUseCase
import com.example.domain.usecase.notes.UpdateNoteUseCase
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
    private val updateNoteUseCase: UpdateNoteUseCase,
) : BaseViewModelWithActions<UiState, UiIntent, UiAction>() {

    override val initialViewState = UiState()
    override suspend fun manageIntent(intent: UiIntent) {
        when (intent) {
            is UiIntent.OnSearchQueryChanged -> searchNotes(intent.query)
            is UiIntent.OnNoteTitleChanged -> setState {
                copy(
                    selectedNoteData = selectedNoteData.copy(
                        title = intent.title
                    )
                )
            }

            is UiIntent.OnNoteDescriptionChanged -> setState {
                copy(
                    selectedNoteData = selectedNoteData.copy(
                        content = intent.description
                    )
                )
            }
            is UiIntent.OnShowEditNoteDialog -> showEditNoteDialog(intent.noteId)
            is UiIntent.OnShowDeleteNoteDialog -> showDeleteNoteDialog(intent.noteId)
            is UiIntent.OnPinChanged -> changePin(intent.pinned)

            UiIntent.OnDeleteNote -> deleteNote()
            UiIntent.OnCreateNote -> createNote()
            UiIntent.OnCloseSession -> signOut()
            UiIntent.OnChangeNoteDialogVisibility -> setState { copy(showNoteDialog = !showNoteDialog, selectedNoteData = Note()) }
            UiIntent.OnChangeDeleteNoteDialogVisibility -> setState { copy(showDeleteNoteDialog = !showDeleteNoteDialog, selectedNoteData = Note()) }
            UiIntent.OnEditNote -> editNote()
        }
    }

    init {
        observeNotes()
    }

    private fun changePin(pinned: Boolean) {
        setState { copy(selectedNoteData = selectedNoteData.copy(pinned = pinned)) }
    }

    private fun editNote() {
        viewModelScope.launch {
            val date = System.currentTimeMillis()
            val result = updateNoteUseCase(state.value.selectedNoteData.copy(date = date))

            result.fold(
                onSuccess = {
                    setState { copy(showNoteDialog = false, selectedNoteData = Note()) }
                },
                onFailure = {
                    dispatchAction(UiAction.ShowToast(it.message.orEmpty()))
                }
            )
        }
    }

    private fun showDeleteNoteDialog(noteId: String) {
        val note = state.value.allNotes.find { it.id == noteId } ?: Note()
        setState { copy(showDeleteNoteDialog = true, selectedNoteData = note) }
    }

    private fun showEditNoteDialog(noteId: String) {
        val note = state.value.allNotes.find { it.id == noteId } ?: Note()
        setState { copy(showNoteDialog = true, selectedNoteData = note) }
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

    private fun createNote() {
        viewModelScope.launch {
            val result = createNoteUseCase(state.value.selectedNoteData)

            result.fold(
                onSuccess = {
                    setState { copy(showNoteDialog = false, selectedNoteData = Note()) }
                },
                onFailure = {
                    dispatchAction(UiAction.ShowToast(it.message.orEmpty()))
                }
            )
        }
    }

    private fun observeNotes() {
        viewModelScope.launch {
            getAllNotesUseCase().collect { result ->
                result.fold(
                    onSuccess = { allNotes ->
                        val pinnedNotes = allNotes.filter { it.pinned }
                        val otherNotes = allNotes.filter { !it.pinned }
                        setState {
                            copy(
                                allNotes = allNotes,
                                notes = otherNotes,
                                pinnedNotes = pinnedNotes,
                            )
                        }
                    },
                    onFailure = {
                        dispatchAction(UiAction.ShowToast(it.message.orEmpty()))
                    }
                )
            }
        }
    }

    private fun searchNotes(query: String) {
        /* meter delay al buscar */
        setState { copy(query = query) }
    }
}
