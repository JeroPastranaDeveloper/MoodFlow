package com.jero.home

import com.jero.core.model.Note
import com.jero.core.utils.emptyString

class HomeViewContract {
    data class UiState(
        val allNotes: List<Note> = emptyList(),
        val filteredNotes: List<Note> = emptyList(),
        val notes: List<Note> = emptyList(),
        val pinnedNotes: List<Note> = emptyList(),
        val query: String = emptyString(),
        val showNoteDialog: Boolean = false,
        val showDeleteNoteDialog: Boolean = false,
        val selectedNoteData: Note = Note(),
    )

    sealed class UiIntent {
        data object OnCloseSession : UiIntent()
        data object OnCreateNote : UiIntent()
        data object OnChangeNoteDialogVisibility : UiIntent()
        data object OnDeleteNote : UiIntent()
        data object OnChangeDeleteNoteDialogVisibility : UiIntent()
        data object OnEditNote : UiIntent()

        data class OnShowDeleteNoteDialog(val noteId: String) : UiIntent()
        data class OnShowEditNoteDialog(val noteId: String) : UiIntent()
        data class OnNoteTitleChanged(val title: String) : UiIntent()
        data class OnNoteDescriptionChanged(val description: String) : UiIntent()
        data class OnSearchQueryChanged(val query: String) : UiIntent()
        data class OnPinChanged(val pinned: Boolean) : UiIntent()
    }

    sealed class UiAction {
        data object GoHome : UiAction()
        data class ShowToast(val message: String) : UiAction()
    }
}
