package com.jero.editnote

import com.jero.core.model.Note

class EditNoteViewContract {
    data class UiState(
        val editedNote: Note = Note(),
        val originalNote: Note = Note(),
        val showDeleteNoteDialog: Boolean = false,
    )

    sealed class UiIntent {
        data object OnChangeDeleteDialogVisibility : UiIntent()
        data object OnDeleteNote : UiIntent()
        data object OnPinChanged : UiIntent()
        data object OnGoBack : UiIntent()

        data class OnFetchNoteDetails(val noteId: String) : UiIntent()
        data class OnTitleChanged(val title: String) : UiIntent()
        data class OnDescriptionChanged(val description: String) : UiIntent()
    }

    sealed class UiAction {
        data object GoBack : UiAction()

        data class ShowToast(val message: String) : UiAction()
    }
}
