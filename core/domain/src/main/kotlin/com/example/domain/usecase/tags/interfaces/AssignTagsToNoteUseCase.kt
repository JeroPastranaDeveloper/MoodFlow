package com.example.domain.usecase.tags.interfaces

interface AssignTagsToNoteUseCase {
    suspend operator fun invoke(noteId: String, tagIds: List<String>): Result<Unit>
}
