package com.jero.trash

import androidx.lifecycle.viewModelScope
import com.example.domain.providers.StringsProvider
import com.example.domain.usecase.notes.interfaces.GetTrashedNotesUseCase
import com.example.domain.usecase.notes.interfaces.PermanentlyDeleteNoteUseCase
import com.example.domain.usecase.notes.interfaces.RestoreNoteUseCase
import com.jero.core.designsystem.R
import com.jero.core.viewmodel.BaseViewModelWithActions
import com.jero.trash.TrashViewContract.UiAction
import com.jero.trash.TrashViewContract.UiIntent
import com.jero.trash.TrashViewContract.UiState
import kotlinx.coroutines.launch

class TrashViewModel(
    private val getTrashedNotesUseCase: GetTrashedNotesUseCase,
    private val restoreNoteUseCase: RestoreNoteUseCase,
    private val permanentlyDeleteNoteUseCase: PermanentlyDeleteNoteUseCase,
    private val stringsProvider: StringsProvider,
) : BaseViewModelWithActions<UiState, UiIntent, UiAction>() {

    override val initialViewState = UiState()

    override suspend fun manageIntent(intent: UiIntent) {
        when (intent) {
            is UiIntent.OnRestoreNote -> restoreNote(intent.noteId)
            is UiIntent.OnRequestDeletePermanently -> setState { copy(noteIdPendingDeletion = intent.noteId) }
            UiIntent.OnConfirmDeletePermanently -> confirmDeletePermanently()
            UiIntent.OnCancelDeletePermanently -> setState { copy(noteIdPendingDeletion = null) }
            UiIntent.OnEmptyTrash -> emptyTrash()
            UiIntent.OnChangeEmptyTrashDialogVisibility -> setState {
                copy(showEmptyTrashDialog = !showEmptyTrashDialog)
            }
            UiIntent.OnGoBack -> dispatchAction(UiAction.GoBack)
        }
    }

    init {
        observeNotes()
    }

    private fun observeNotes() {
        viewModelScope.launch {
            getTrashedNotesUseCase().collect { notes ->
                setState { copy(notes = notes.sortedByDescending { it.deletedAt }) }
            }
        }
    }

    private fun restoreNote(noteId: String) {
        viewModelScope.launch {
            restoreNoteUseCase(noteId).onFailure {
                dispatchAction(UiAction.ShowToast(it.message ?: stringsProvider(R.string.unknown_error)))
            }
        }
    }

    private fun confirmDeletePermanently() {
        val noteId = state.value.noteIdPendingDeletion ?: return
        setState { copy(noteIdPendingDeletion = null) }
        viewModelScope.launch {
            permanentlyDeleteNoteUseCase(noteId).onFailure {
                dispatchAction(UiAction.ShowToast(it.message ?: stringsProvider(R.string.unknown_error)))
            }
        }
    }

    private fun emptyTrash() {
        viewModelScope.launch {
            state.value.notes.forEach { note ->
                permanentlyDeleteNoteUseCase(note.id).onFailure {
                    dispatchAction(UiAction.ShowToast(it.message ?: stringsProvider(R.string.unknown_error)))
                }
            }
            setState { copy(showEmptyTrashDialog = false) }
        }
    }
}
