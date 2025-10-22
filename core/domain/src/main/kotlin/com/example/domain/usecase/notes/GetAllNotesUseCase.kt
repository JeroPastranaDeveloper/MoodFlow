package com.example.domain.usecase.notes

import com.jero.core.model.Note

interface GetAllNotesUseCase {
    suspend operator fun invoke(): Result<List<Note>>
}
