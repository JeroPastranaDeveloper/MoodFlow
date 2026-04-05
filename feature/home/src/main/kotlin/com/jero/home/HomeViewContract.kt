package com.jero.home

import com.jero.core.model.Note
import com.jero.core.model.Tag
import com.jero.core.utils.emptyString

data class SearchFilter(
    val onlyPinned: Boolean = false,
    val selectedTagId: String? = null,
)

class HomeViewContract {
    data class UiState(
        val allNotes: List<Note> = emptyList(),
        val filteredNotes: List<Note> = emptyList(),
        val notes: List<Note> = emptyList(),
        val pinnedNotes: List<Note> = emptyList(),
        val query: String = emptyString(),
        val searchFilter: SearchFilter = SearchFilter(),
        val showMoreMenu: Boolean = false,
        val notesCanBeSelected: Boolean = false,
        val selectedNotes: List<String> = emptyList(),
        val allTags: List<Tag> = emptyList(),
    )

    sealed class UiIntent {
        data object OnDeleteMultipleNotes : UiIntent()
        data object OnGoTrashScreen : UiIntent()
        data class OnGoEditNoteScreen(val noteId: String?) : UiIntent()
        data class OnSelectNote(val noteId: String, val isChecked: Boolean) : UiIntent()
        data class OnSearchQueryChanged(val query: String) : UiIntent()
        data class OnSearchFilterChanged(val filter: SearchFilter) : UiIntent()
        data class OnTagFilterSelected(val tagId: String?) : UiIntent()

        data object OnChangeMoreMenuVisibility : UiIntent()
        data object OnGoSettingsScreen : UiIntent()
        data object OnChangeMultipleSelectorUIVisibility : UiIntent()
        data object OnPinOrUnpinSelectedNotes : UiIntent()
        data object OnClearFocus : UiIntent()
        data object OnGoBack : UiIntent()
    }

    sealed class UiAction {
        data class GoEditNoteScreen(val noteId: String) : UiAction()
        data class ShowToast(val message: String) : UiAction()

        data object GoLogin : UiAction()
        data object GoSettingsScreen : UiAction()
        data object GoTrashScreen : UiAction()
        data object OnClearFocus : UiAction()
        data object OnGoBack : UiAction()
    }
}
