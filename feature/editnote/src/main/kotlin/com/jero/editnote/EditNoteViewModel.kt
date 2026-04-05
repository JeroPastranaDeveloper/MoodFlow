package com.jero.editnote

import androidx.lifecycle.viewModelScope
import com.example.domain.providers.StringsProvider
import com.example.domain.usecase.notes.interfaces.CreateNoteUseCase
import com.example.domain.usecase.notes.interfaces.DeleteNoteUseCase
import com.example.domain.usecase.notes.interfaces.GetNoteByIdUseCase
import com.example.domain.usecase.notes.interfaces.UpdateNoteUseCase
import com.example.domain.usecase.tags.interfaces.AssignTagsToNoteUseCase
import com.example.domain.usecase.tags.interfaces.GetTagsUseCase
import com.jero.core.designsystem.R
import com.jero.core.model.Note
import com.jero.core.model.UNTAGGED_TAG_ID
import com.jero.core.model.hasContentWithoutId
import com.jero.core.viewmodel.BaseViewModelWithActions
import com.jero.editnote.EditNoteViewContract.UiAction
import com.jero.editnote.EditNoteViewContract.UiIntent
import com.jero.editnote.EditNoteViewContract.UiState
import kotlinx.coroutines.launch

class EditNoteViewModel(
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val getTagsUseCase: GetTagsUseCase,
    private val assignTagsToNoteUseCase: AssignTagsToNoteUseCase,
    private val stringsProvider: StringsProvider,
) : BaseViewModelWithActions<UiState, UiIntent, UiAction>() {
    override val initialViewState = UiState()

    init {
        observeTags()
    }

    override suspend fun manageIntent(intent: UiIntent) {
        when (intent) {
            is UiIntent.OnToggleTag -> toggleTag(intent.tagId)
            is UiIntent.OnFetchNoteDetails -> fetchNoteDetails(intent.noteId)
            is UiIntent.OnDescriptionChanged -> changeDescription(intent.description)
            is UiIntent.OnColorChanged -> setState { copy(editedNote = editedNote.copy(color = intent.color)) }
            is UiIntent.OnTitleChanged -> changeTitle(intent.title)
            UiIntent.OnDeleteNote -> deleteNote()
            UiIntent.OnPinChanged -> changePin()
            UiIntent.OnToggleTagSelector -> setState { copy(showTagSelector = !showTagSelector) }
            UiIntent.OnGoBack -> goBack()
        }
    }

    private fun observeTags() {
        viewModelScope.launch {
            getTagsUseCase().collect { tags ->
                setState { copy(allTags = tags) }
            }
        }
    }

    private fun toggleTag(tagId: String) {
        val current = state.value.editedNote.tagIds.toMutableList()
        if (tagId in current) {
            current.remove(tagId)
        } else {
            current.remove(UNTAGGED_TAG_ID)
            current.add(tagId)
        }
        val final = current.ifEmpty { listOf(UNTAGGED_TAG_ID) }
        setState { copy(editedNote = editedNote.copy(tagIds = final), showTagSelector = false) }
    }

    private fun fetchNoteDetails(noteId: String) {
        if (noteId.isBlank()) {
            setState { copy(editedNote = Note(), originalNote = Note()) }
        } else {
            viewModelScope.launch {
                val result = getNoteByIdUseCase(noteId)
                result.fold(
                    onSuccess = {
                        setState {
                            copy(
                                editedNote = it ?: Note(),
                                originalNote = it ?: Note(),
                            )
                        }
                    },
                    onFailure = {}
                )
            }
        }
    }

    private fun changePin() {
        setState { copy(editedNote = editedNote.copy(pinned = !editedNote.pinned)) }
    }

    private fun deleteNote() {
        viewModelScope.launch {
            deleteNoteUseCase(state.value.editedNote.id).fold(
                onSuccess = { dispatchAction(UiAction.GoBack) },
                onFailure = {
                    dispatchAction(UiAction.ShowToast(it.message ?: stringsProvider(R.string.unknown_error)))
                }
            )
        }
    }

    private fun goBack() {
        viewModelScope.launch {
            when {
                state.value.editedNote == state.value.originalNote -> dispatchAction(UiAction.GoBack)
                !state.value.editedNote.id.isBlank() -> updateNote()
                state.value.editedNote.hasContentWithoutId() -> createNote()
                else -> dispatchAction(UiAction.GoBack)
            }
        }
    }

    private suspend fun createNote() {
        createNoteUseCase(state.value.editedNote).fold(
            onSuccess = { createdNote ->
                assignTagsToNoteUseCase(createdNote.id, state.value.editedNote.tagIds)
                dispatchAction(UiAction.GoBack)
            },
            onFailure = {
                dispatchAction(UiAction.ShowToast(it.message ?: stringsProvider(R.string.unknown_error)))
            }
        )
    }

    private suspend fun updateNote() {
        updateNoteUseCase(state.value.editedNote).fold(
            onSuccess = {
                assignTagsToNoteUseCase(state.value.editedNote.id, state.value.editedNote.tagIds)
                dispatchAction(UiAction.GoBack)
            },
            onFailure = {
                dispatchAction(UiAction.ShowToast(it.message ?: stringsProvider(R.string.unknown_error)))
            }
        )
    }

    private fun changeTitle(title: String) {
        setState { copy(editedNote = editedNote.copy(title = title)) }
    }

    private fun changeDescription(description: String) {
        setState { copy(editedNote = editedNote.copy(content = description)) }
    }
}
