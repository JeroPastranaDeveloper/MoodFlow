package com.jero.trash

import com.jero.core.model.Note

class TrashViewContract {
    data class UiState(
        val notes: List<Note> = emptyList(),
        val showEmptyTrashDialog: Boolean = false,
        val noteIdPendingDeletion: String? = null,
    )

    sealed class UiIntent {
        data class OnRestoreNote(val noteId: String) : UiIntent()
        data class OnRequestDeletePermanently(val noteId: String) : UiIntent()
        data object OnConfirmDeletePermanently : UiIntent()
        data object OnCancelDeletePermanently : UiIntent()
        data object OnEmptyTrash : UiIntent()
        data object OnChangeEmptyTrashDialogVisibility : UiIntent()
        data object OnGoBack : UiIntent()
    }

    sealed class UiAction {
        data class ShowToast(val message: String) : UiAction()
        data object GoBack : UiAction()
    }
}
