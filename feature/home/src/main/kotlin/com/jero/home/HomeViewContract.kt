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
        val showDeleteNoteDialog: Boolean = false,
        val selectedNoteData: Note = Note(),
    )

    sealed class UiIntent {
        data object OnCloseSession : UiIntent() // Irá fuera
        data object OnDeleteNote : UiIntent()
        data object OnChangeDeleteNoteDialogVisibility : UiIntent()

        data class OnShowDeleteNoteDialog(val noteId: String) : UiIntent()
        data class OnGoEditNoteScreen(val noteId: String?) : UiIntent()
        data class OnSearchQueryChanged(val query: String) : UiIntent()
    }

    sealed class UiAction {
        data object GoHome : UiAction()

        data class GoEditNoteScreen(val note: Note) : UiAction()
        data class ShowToast(val message: String) : UiAction()
    }
}
