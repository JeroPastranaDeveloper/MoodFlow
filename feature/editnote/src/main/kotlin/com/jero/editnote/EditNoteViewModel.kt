package com.jero.editnote

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.notes.interfaces.CreateNoteUseCase
import com.example.domain.usecase.notes.interfaces.DeleteNoteUseCase
import com.example.domain.usecase.notes.interfaces.UpdateNoteUseCase
import com.jero.core.model.Note
import com.jero.core.model.hasContentWithoutId
import com.jero.core.viewmodel.BaseViewModelWithActions
import com.jero.editnote.EditNoteViewContract.UiAction
import com.jero.editnote.EditNoteViewContract.UiIntent
import com.jero.editnote.EditNoteViewContract.UiState
import kotlinx.coroutines.launch

class EditNoteViewModel(
    private val createNoteUseCase: CreateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModelWithActions<UiState, UiIntent, UiAction>() {
    override val initialViewState = UiState()

    val note = savedStateHandle.getStateFlow<Note?>("note", null)

    init {
        note.value?.let {
            setState { copy(originalNote = it, editedNote = it) }
        }
    }

    override suspend fun manageIntent(intent: UiIntent) {
        when (intent) {
            UiIntent.OnChangeDeleteDialogVisibility -> setState { copy(showDeleteNoteDialog = !showDeleteNoteDialog) }
            UiIntent.OnDeleteNote -> deleteNote()
            UiIntent.OnPinChanged -> changePin()
            UiIntent.OnGoBack -> goBack()

            is UiIntent.OnDescriptionChanged -> changeDescription(intent.description)
            is UiIntent.OnTitleChanged -> changeTitle(intent.title)
        }
    }

    private fun changePin() {
        setState { copy(editedNote = editedNote.copy(pinned = !editedNote.pinned)) }
    }

    private fun deleteNote() {
        viewModelScope.launch {
            val result = deleteNoteUseCase(state.value.editedNote.id)

            result.fold(
                onSuccess = {
                    dispatchAction(UiAction.GoBack)
                },
                onFailure = {
                    dispatchAction(UiAction.ShowToast(it.message.orEmpty()))
                }
            )
            setState { copy(showDeleteNoteDialog = false) }
        }
    }

    private fun goBack() {
        viewModelScope.launch {
            when {
                state.value.editedNote == state.value.originalNote -> dispatchAction(UiAction.GoBack)
                !state.value.editedNote.id.isBlank() -> updateNote()
                state.value.editedNote.hasContentWithoutId() -> createNote()
                else -> dispatchAction(UiAction.GoBack)
            }
        }
    }

    private suspend fun createNote() {
        val result = createNoteUseCase(state.value.editedNote)
        result.fold(
            onSuccess = {
                dispatchAction(UiAction.GoBack)
            },
            onFailure = {
                dispatchAction(UiAction.ShowToast(it.message.orEmpty()))
            }
        )
    }

    private suspend fun updateNote() {
        val result = updateNoteUseCase(state.value.editedNote)
        result.fold(
            onSuccess = {
                dispatchAction(UiAction.GoBack)
            },
            onFailure = {
                dispatchAction(UiAction.ShowToast(it.message.orEmpty()))
            }
        )
    }

    private fun changeTitle(title: String) {
        setState {
            copy(
                editedNote = editedNote.copy(title = title)
            )
        }
    }

    private fun changeDescription(description: String) {
        setState {
            copy(
                editedNote = editedNote.copy(content = description)
            )
        }
    }
}
