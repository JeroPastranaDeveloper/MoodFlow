package com.jero.home

import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.notes.interfaces.DeleteNoteUseCase
import com.example.domain.usecase.notes.interfaces.GetAllNotesUseCase
import com.example.domain.usecase.notes.interfaces.UpdateNoteUseCase
import com.jero.core.model.Note
import com.jero.core.viewmodel.BaseViewModelWithActions
import com.jero.home.HomeViewContract.UiAction
import com.jero.home.HomeViewContract.UiIntent
import com.jero.home.HomeViewContract.UiState
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
) : BaseViewModelWithActions<UiState, UiIntent, UiAction>() {

    override val initialViewState = UiState()
    override suspend fun manageIntent(intent: UiIntent) {
        when (intent) {
            is UiIntent.OnSearchQueryChanged -> searchNotes(intent.query)

            is UiIntent.OnGoEditNoteScreen -> showEditNoteDialog(intent.noteId)
            is UiIntent.OnSelectNote -> updateNoteSelection(intent.noteId, intent.isChecked)

            UiIntent.OnShowDeleteNoteDialog -> showDeleteNoteDialog()
            UiIntent.OnGoSettingsScreen -> goSettings()
            UiIntent.OnChangeDeleteNotesDialogVisibility -> setState {
                copy(
                    showDeleteNotesDialog = !showDeleteNotesDialog)
            }
            UiIntent.OnChangeMoreMenuVisibility -> setState { copy(showMoreMenu = !showMoreMenu) }
            UiIntent.OnChangeMultipleSelectorUIVisibility -> changeMultipleSelectorUIVisibility()
            UiIntent.OnDeleteMultipleNotes -> deleteMultipleNotes()
            UiIntent.OnPinOrUnpinSelectedNotes -> pinOrUnpinSelectedNotes()
        }
    }

    init {
        observeNotes()
    }

    private fun pinOrUnpinSelectedNotes() {
        viewModelScope.launch {
            val selectedNotes = state.value.selectedNotes
            selectedNotes.forEach { noteId ->
                val note = state.value.allNotes.find { it.id == noteId }
                if (note != null) {
                    val updatedNote = note.copy(pinned = !note.pinned)
                    updateNoteUseCase(updatedNote).fold(
                        onSuccess = { /* no-op */ },
                        onFailure = {
                            dispatchAction(UiAction.ShowToast(it.message.orEmpty()))
                        }
                    )
                }
            }

            changeMultipleSelectorUIVisibility()
        }
    }

    private fun changeMultipleSelectorUIVisibility() {
        setState {
            copy(
                notesCanBeSelected = !notesCanBeSelected,
                showDeleteNotesDialog = false,
                selectedNotes = emptyList()
            )
        }
    }

    private fun deleteMultipleNotes() {
        viewModelScope.launch {
            val selectedNotes = state.value.selectedNotes
            selectedNotes.forEach { noteId ->
                val result = deleteNoteUseCase(noteId)
                result.fold(
                    onSuccess = { /* no-op */ },
                    onFailure = {
                        dispatchAction(UiAction.ShowToast(it.message.orEmpty()))
                    }
                )
            }

            changeMultipleSelectorUIVisibility()
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

    private fun showDeleteNoteDialog() {
        setState { copy(showDeleteNotesDialog = true) }
    }

    private fun showEditNoteDialog(noteId: String?) {
        val note = state.value.allNotes.find { it.id == noteId } ?: Note()
        dispatchAction(UiAction.GoEditNoteScreen(note))
    }

    private fun goSettings() {
        setState { copy(showMoreMenu = false) }
        dispatchAction(UiAction.GoSettingsScreen)
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
        val notes = state.value.allNotes.filter {
            it.title.contains(query, true)
        }

        setState { copy(query = query, filteredNotes = notes) }
    }
}
