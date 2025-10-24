package com.jero.home

import com.jero.core.model.Note
import com.jero.core.utils.emptyString

class HomeViewContract {
    data class UiState(
        val allNotes: List<Note> = emptyList(),
        val notes: List<Note> = emptyList(),
        val pinnedNotes: List<Note> = emptyList(),
        val query: String = emptyString(),
        val showNewNoteDialog: Boolean = false,
        val newNoteData: Note = Note(),
    )

    sealed class UiIntent {
        data object OnCloseSession : UiIntent()
        data object OnCreateNote : UiIntent()
        data object OnChangeNewNoteDialogVisibility : UiIntent()
        data class OnDeleteNote(val noteId: String) : UiIntent()
        data class OnNewNoteTitleChanged(val title: String) : UiIntent()
        data class OnNewNoteDescriptionChanged(val description: String) : UiIntent()
        data class OnSearchQueryChanged(val query: String) : UiIntent()
    }

    sealed class UiAction {
        data object GoHome : UiAction()
        data class ShowToast(val message: String) : UiAction()
    }
}
