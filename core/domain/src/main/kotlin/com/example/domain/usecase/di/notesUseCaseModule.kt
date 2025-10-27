package com.example.domain.usecase.di

import com.example.domain.usecase.notes.implementations.CreateNoteUseCaseImpl
import com.example.domain.usecase.notes.implementations.DeleteNoteUseCaseImpl
import com.example.domain.usecase.notes.implementations.GetAllNotesUseCaseImpl
import com.example.domain.usecase.notes.implementations.GetNoteByIdUseCaseImpl
import com.example.domain.usecase.notes.implementations.UpdateNoteUseCaseImpl
import com.example.domain.usecase.notes.interfaces.CreateNoteUseCase
import com.example.domain.usecase.notes.interfaces.DeleteNoteUseCase
import com.example.domain.usecase.notes.interfaces.GetAllNotesUseCase
import com.example.domain.usecase.notes.interfaces.GetNoteByIdUseCase
import com.example.domain.usecase.notes.interfaces.UpdateNoteUseCase
import org.koin.dsl.module

val notesUseCaseModule = module {
    factory<CreateNoteUseCase> {
        CreateNoteUseCaseImpl(get(), get(), get())
    }

    factory<UpdateNoteUseCase> {
        UpdateNoteUseCaseImpl(get(), get())
    }

    factory<DeleteNoteUseCase> {
        DeleteNoteUseCaseImpl(get())
    }

    factory<GetAllNotesUseCase> {
        GetAllNotesUseCaseImpl(get(), get())
    }

    factory<GetNoteByIdUseCase> {
        GetNoteByIdUseCaseImpl(get())
    }
}
