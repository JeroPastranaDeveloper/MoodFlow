package com.jero.editnote

import androidx.lifecycle.viewModelScope
import com.example.domain.providers.StringsProvider
import com.example.domain.usecase.notes.interfaces.CreateNoteUseCase
import com.example.domain.usecase.notes.interfaces.DeleteNoteUseCase
import com.example.domain.usecase.notes.interfaces.GetNoteByIdUseCase
import com.example.domain.usecase.notes.interfaces.UpdateNoteUseCase
import com.jero.core.designsystem.R
import com.jero.core.model.Note
import com.jero.core.model.hasContentWithoutId
import com.jero.core.viewmodel.BaseViewModelWithActions
import com.jero.editnote.EditNoteViewContract.UiAction
import com.jero.editnote.EditNoteViewContract.UiIntent
import com.jero.editnote.EditNoteViewContract.UiState
import kotlinx.coroutines.launch

class EditNoteViewModel(
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val stringsProvider: StringsProvider,
) : BaseViewModelWithActions<UiState, UiIntent, UiAction>() {
    override val initialViewState = UiState()

    override suspend fun manageIntent(intent: UiIntent) {
        when (intent) {
            UiIntent.OnChangeDeleteDialogVisibility -> setState { copy(showDeleteNoteDialog = !showDeleteNoteDialog) }
            UiIntent.OnDeleteNote -> deleteNote()
            UiIntent.OnPinChanged -> changePin()
            UiIntent.OnGoBack -> goBack()

            is UiIntent.OnFetchNoteDetails -> fetchNoteDetails(intent.noteId)
            is UiIntent.OnDescriptionChanged -> changeDescription(intent.description)
            is UiIntent.OnTitleChanged -> changeTitle(intent.title)
        }
    }

    private fun fetchNoteDetails(noteId: String) {
        viewModelScope.launch {
            val result = getNoteByIdUseCase(noteId)

            result.fold(
                onSuccess = {
                    setState {
                        copy(
                            editedNote = it ?: Note(),
                            originalNote = it ?: Note(),
                        )
                    }
                },
                onFailure = {}
            )
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
                    dispatchAction(UiAction.ShowToast(it.message ?: stringsProvider(R.string.unknown_error)))
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
                dispatchAction(UiAction.ShowToast(it.message ?: stringsProvider(R.string.unknown_error)))
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
                dispatchAction(UiAction.ShowToast(it.message ?: stringsProvider(R.string.unknown_error)))
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
