package com.jero.trash.di

import com.jero.trash.TrashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val trashViewModelModule = module {
    viewModel {
        TrashViewModel(get(), get(), get(), get())
    }
}
