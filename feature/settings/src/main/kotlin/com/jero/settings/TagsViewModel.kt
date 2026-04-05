package com.jero.settings

import androidx.lifecycle.viewModelScope
import com.example.domain.providers.StringsProvider
import com.example.domain.usecase.tags.interfaces.CreateTagUseCase
import com.example.domain.usecase.tags.interfaces.DeleteTagUseCase
import com.example.domain.usecase.tags.interfaces.GetTagsUseCase
import com.example.domain.usecase.tags.interfaces.UpdateTagUseCase
import com.jero.core.designsystem.R
import com.jero.core.viewmodel.BaseViewModelWithActions
import com.jero.settings.TagsViewContract.UiAction
import com.jero.settings.TagsViewContract.UiIntent
import com.jero.settings.TagsViewContract.UiState
import kotlinx.coroutines.launch

class TagsViewModel(
    private val getTagsUseCase: GetTagsUseCase,
    private val createTagUseCase: CreateTagUseCase,
    private val updateTagUseCase: UpdateTagUseCase,
    private val deleteTagUseCase: DeleteTagUseCase,
    private val strings: StringsProvider,
) : BaseViewModelWithActions<UiState, UiIntent, UiAction>() {

    override val initialViewState = UiState()

    init {
        observeTags()
    }

    override suspend fun manageIntent(intent: UiIntent) {
        when (intent) {
            is UiIntent.OnNewTagNameChanged -> setState { copy(newTagName = intent.name) }
            is UiIntent.OnRequestDeleteTag -> setState { copy(tagToDelete = intent.tag) }
            is UiIntent.OnStartEditTag -> setState { copy(editingTag = intent.tag) }
            is UiIntent.OnEditTagNameChanged -> setState { copy(editingTag = editingTag?.copy(name = intent.name)) }
            UiIntent.OnShowCreateDialog -> setState { copy(showCreateDialog = true, newTagName = "") }
            UiIntent.OnDismissCreateDialog -> setState { copy(showCreateDialog = false, newTagName = "") }
            UiIntent.OnCreateTag -> createTag()
            UiIntent.OnConfirmEditTag -> editTag()
            UiIntent.OnDismissEditTag -> setState { copy(editingTag = null) }
            UiIntent.OnConfirmDeleteTag -> deleteTag()
            UiIntent.OnDismissDeleteTag -> setState { copy(tagToDelete = null) }
            UiIntent.OnGoBack -> dispatchAction(UiAction.GoBack)
        }
    }

    private fun observeTags() {
        viewModelScope.launch {
            getTagsUseCase().collect { tags ->
                setState { copy(tags = tags) }
            }
        }
    }

    private fun createTag() {
        val name = state.value.newTagName.trim()
        if (name.isBlank()) return

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            createTagUseCase(name).fold(
                onSuccess = { setState { copy(showCreateDialog = false, newTagName = "", isLoading = false) } },
                onFailure = {
                    setState { copy(isLoading = false) }
                    dispatchAction(UiAction.ShowToast(it.message ?: strings(R.string.unknown_error)))
                }
            )
        }
    }

    private fun editTag() {
        val tag = state.value.editingTag ?: return
        if (tag.name.isBlank()) return

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            updateTagUseCase(tag).fold(
                onSuccess = { setState { copy(editingTag = null, isLoading = false) } },
                onFailure = {
                    setState { copy(isLoading = false) }
                    dispatchAction(UiAction.ShowToast(it.message ?: strings(R.string.unknown_error)))
                }
            )
        }
    }

    private fun deleteTag() {
        val tag = state.value.tagToDelete ?: return

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            deleteTagUseCase(tag.id).fold(
                onSuccess = { setState { copy(tagToDelete = null, isLoading = false) } },
                onFailure = {
                    setState { copy(isLoading = false) }
                    dispatchAction(UiAction.ShowToast(it.message ?: strings(R.string.unknown_error)))
                }
            )
        }
    }
}
