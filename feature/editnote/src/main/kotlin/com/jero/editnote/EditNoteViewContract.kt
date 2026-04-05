package com.jero.editnote

import com.jero.core.model.Note
import com.jero.core.model.Tag

class EditNoteViewContract {
    data class UiState(
        val editedNote: Note = Note(),
        val originalNote: Note = Note(),
        val allTags: List<Tag> = emptyList(),
        val showTagSelector: Boolean = false,
    )

    sealed class UiIntent {
        data object OnDeleteNote : UiIntent()
        data object OnPinChanged : UiIntent()
        data class OnFetchNoteDetails(val noteId: String) : UiIntent()
        data class OnTitleChanged(val title: String) : UiIntent()
        data class OnDescriptionChanged(val description: String) : UiIntent()
        data class OnColorChanged(val color: Long) : UiIntent()
        data object OnToggleTagSelector : UiIntent()
        data class OnToggleTag(val tagId: String) : UiIntent()
        data object OnGoBack : UiIntent()
    }

    sealed class UiAction {
        data class ShowToast(val message: String) : UiAction()
        data object GoBack : UiAction()
    }
}
