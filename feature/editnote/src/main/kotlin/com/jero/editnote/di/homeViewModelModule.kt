package com.jero.editnote.di

import com.jero.editnote.EditNoteViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val editNoteViewModelModule = module {
    viewModel {
        EditNoteViewModel(get(), get(), get(), get())
    }
}
