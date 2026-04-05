package com.jero.home

import androidx.lifecycle.viewModelScope
import com.example.domain.providers.StringsProvider
import com.example.domain.usecase.notes.interfaces.DeleteNoteUseCase
import com.example.domain.usecase.notes.interfaces.GetAllNotesUseCase
import com.example.domain.usecase.notes.interfaces.UpdateNoteUseCase
import com.example.domain.usecase.tags.interfaces.GetTagsUseCase
import com.jero.core.designsystem.R
import com.jero.core.viewmodel.BaseViewModelWithActions
import com.jero.home.HomeViewContract.UiAction
import com.jero.home.HomeViewContract.UiIntent
import com.jero.home.HomeViewContract.UiState
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val getTagsUseCase: GetTagsUseCase,
    private val stringsProvider: StringsProvider,
) : BaseViewModelWithActions<UiState, UiIntent, UiAction>() {

    override val initialViewState = UiState()
    override suspend fun manageIntent(intent: UiIntent) {
        when (intent) {
            is UiIntent.OnSearchQueryChanged -> searchNotes(intent.query)
            is UiIntent.OnSearchFilterChanged -> {
                setState { copy(searchFilter = intent.filter) }
                searchNotes(state.value.query)
            }
            is UiIntent.OnTagFilterSelected -> {
                setState { copy(searchFilter = searchFilter.copy(selectedTagId = intent.tagId)) }
                searchNotes(state.value.query)
            }

            is UiIntent.OnGoEditNoteScreen -> showEditNoteDialog(intent.noteId)
            is UiIntent.OnSelectNote -> updateNoteSelection(intent.noteId, intent.isChecked)

            UiIntent.OnGoSettingsScreen -> goSettings()
            UiIntent.OnChangeMoreMenuVisibility -> setState { copy(showMoreMenu = !showMoreMenu) }
            UiIntent.OnChangeMultipleSelectorUIVisibility -> changeMultipleSelectorUIVisibility()
            UiIntent.OnDeleteMultipleNotes -> deleteMultipleNotes()
            UiIntent.OnPinOrUnpinSelectedNotes -> pinOrUnpinSelectedNotes()
            UiIntent.OnGoTrashScreen -> goTrash()
            UiIntent.OnClearFocus -> dispatchAction(UiAction.OnClearFocus)
            UiIntent.OnGoBack -> dispatchAction(UiAction.OnGoBack)
        }
    }

    init {
        observeNotes()
        observeTags()
    }

    private fun pinOrUnpinSelectedNotes() {
        viewModelScope.launch {
            val selectedNotes = state.value.selectedNotes
            val failedIds = mutableListOf<String>()

            selectedNotes.forEach { noteId ->
                val note = state.value.allNotes.find { it.id == noteId }
                if (note != null) {
                    val updatedNote = note.copy(pinned = !note.pinned)
                    updateNoteUseCase(updatedNote).fold(
                        onSuccess = { },
                        onFailure = {
                            failedIds.add(noteId)
                            dispatchAction(UiAction.ShowToast(it.message ?: stringsProvider(R.string.unknown_error)))
                        }
                    )
                }
            }

            if (failedIds.isEmpty()) {
                changeMultipleSelectorUIVisibility()
            } else {
                setState { copy(selectedNotes = failedIds) }
            }
        }
    }

    private fun changeMultipleSelectorUIVisibility() {
        setState {
            copy(
                notesCanBeSelected = !notesCanBeSelected,
                selectedNotes = emptyList()
            )
        }
    }

    private fun deleteMultipleNotes() {
        viewModelScope.launch {
            val selectedNotes = state.value.selectedNotes
            val failedIds = mutableListOf<String>()

            selectedNotes.forEach { noteId ->
                deleteNoteUseCase(noteId).fold(
                    onSuccess = { },
                    onFailure = {
                        failedIds.add(noteId)
                        dispatchAction(UiAction.ShowToast(it.message ?: stringsProvider(R.string.unknown_error)))
                    }
                )
            }

            if (failedIds.isEmpty()) {
                changeMultipleSelectorUIVisibility()
            } else {
                setState { copy(selectedNotes = failedIds) }
            }
        }
    }

    private fun updateNoteSelection(noteId: String, isSelected: Boolean) {
        val selectedNotes = state.value.selectedNotes.toMutableSet()

        if (isSelected) {
            selectedNotes.add(noteId)
        } else {
            selectedNotes.remove(noteId)
        }

        val canBeSelected = selectedNotes.isNotEmpty()

        setState {
            copy(
                selectedNotes = selectedNotes.toList(),
                notesCanBeSelected = canBeSelected
            )
        }
    }

    private fun showEditNoteDialog(noteId: String?) {
        dispatchAction(UiAction.GoEditNoteScreen(noteId.orEmpty()))
    }

    private fun goSettings() {
        setState { copy(showMoreMenu = false) }
        dispatchAction(UiAction.GoSettingsScreen)
    }

    private fun goTrash() {
        setState { copy(showMoreMenu = false) }
        dispatchAction(UiAction.GoTrashScreen)
    }

    private fun observeTags() {
        viewModelScope.launch {
            getTagsUseCase().collect { tags ->
                setState { copy(allTags = tags) }
            }
        }
    }

    private fun observeNotes() {
        viewModelScope.launch {
            getAllNotesUseCase().collect { allNotes ->
                val sortedNotes = allNotes.sortedByDescending { it.date }
                val otherNotes = sortedNotes.filter { !it.pinned }
                val pinnedNotes = sortedNotes.filter { it.pinned }
                setState {
                    copy(
                        allNotes = sortedNotes,
                        notes = otherNotes,
                        pinnedNotes = pinnedNotes,
                    )
                }

                searchNotes(state.value.query)
            }
        }
    }

    private fun searchNotes(query: String) {
        val filter = state.value.searchFilter
        val filtered = state.value.allNotes
            .filter { note ->
                if (query.isBlank()) return@filter true
                note.title.contains(query, ignoreCase = true) ||
                        note.content.contains(query, ignoreCase = true)
            }
            .filter { note -> if (filter.onlyPinned) note.pinned else true }
            .filter { note ->
                filter.selectedTagId == null || filter.selectedTagId in note.tagIds
            }

        setState { copy(query = query, filteredNotes = filtered) }
    }
}
