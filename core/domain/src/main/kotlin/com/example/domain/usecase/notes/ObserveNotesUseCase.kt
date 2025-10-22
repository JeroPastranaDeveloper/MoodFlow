package com.example.domain.usecase.notes

import com.jero.core.model.Note
import kotlinx.coroutines.flow.Flow

interface ObserveNotesUseCase {
    operator fun invoke(): Flow<List<Note>>
}
