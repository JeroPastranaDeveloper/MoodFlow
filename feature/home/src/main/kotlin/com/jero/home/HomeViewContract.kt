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
        val showDeleteNotesDialog: Boolean = false,
        val showMoreMenu: Boolean = false,
        val canBeSelected: Boolean = false,
        val selectedNotes: List<String> = emptyList(),
    )

    sealed class UiIntent {
        data object OnChangeDeleteNotesDialogVisibility : UiIntent()
        data object OnChangeMoreMenuVisibility : UiIntent()
        data object OnGoSettingsScreen : UiIntent()
        data object OnChangeMultipleSelectorUIVisibility : UiIntent()
        data object OnShowDeleteNoteDialog : UiIntent()

        data object OnDeleteMultipleNotes : UiIntent()
        data class OnGoEditNoteScreen(val noteId: String?) : UiIntent()
        data class OnSelectNote(val noteId: String, val isChecked: Boolean) : UiIntent()
        data class OnSearchQueryChanged(val query: String) : UiIntent()
    }

    sealed class UiAction {
        data object GoHome : UiAction()
        data object GoSettingsScreen : UiAction()

        data class GoEditNoteScreen(val note: Note) : UiAction()
        data class ShowToast(val message: String) : UiAction()
    }
}
