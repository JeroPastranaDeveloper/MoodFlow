package com.jero.data.di

import com.example.domain.usecase.notes.CreateNoteUseCase
import com.example.domain.usecase.notes.DeleteNoteUseCase
import com.example.domain.usecase.notes.GetAllNotesUseCase
import com.example.domain.usecase.notes.ObserveNotesUseCase
import com.example.domain.usecase.notes.UpdateNoteUseCase
import com.jero.data.usecase.notes.CreateNoteUseCaseImpl
import com.jero.data.usecase.notes.DeleteNoteUseCaseImpl
import com.jero.data.usecase.notes.GetAllNotesUseCaseImpl
import com.jero.data.usecase.notes.ObserveNotesUseCaseImpl
import com.jero.data.usecase.notes.UpdateNoteUseCaseImpl
import org.koin.dsl.module

val notesUseCaseModule = module {
    factory<CreateNoteUseCase> {
        CreateNoteUseCaseImpl(get(), get())
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

    factory<ObserveNotesUseCase> {
        ObserveNotesUseCaseImpl(get(), get())
    }
}