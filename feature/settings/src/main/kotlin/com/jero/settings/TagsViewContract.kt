package com.jero.settings

import com.jero.core.model.Tag

class TagsViewContract {
    data class UiState(
        val tags: List<Tag> = emptyList(),
        val editingTag: Tag? = null,
        val newTagName: String = "",
        val showCreateDialog: Boolean = false,
        val tagToDelete: Tag? = null,
        val isLoading: Boolean = false,
    )

    sealed class UiIntent {
        data class OnNewTagNameChanged(val name: String) : UiIntent()
        data class OnEditTagNameChanged(val name: String) : UiIntent()
        data class OnRequestDeleteTag(val tag: Tag) : UiIntent()
        data class OnStartEditTag(val tag: Tag) : UiIntent()

        data object OnShowCreateDialog : UiIntent()
        data object OnDismissCreateDialog : UiIntent()
        data object OnCreateTag : UiIntent()
        data object OnConfirmEditTag : UiIntent()
        data object OnDismissEditTag : UiIntent()
        data object OnConfirmDeleteTag : UiIntent()
        data object OnDismissDeleteTag : UiIntent()
        data object OnGoBack : UiIntent()
    }

    sealed class UiAction {
        data class ShowToast(val message: String) : UiAction()

        data object GoBack : UiAction()
    }
}
