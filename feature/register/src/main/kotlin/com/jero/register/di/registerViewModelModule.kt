package com.jero.register.di

import com.jero.register.RegisterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val registerViewModelModule = module {
    viewModel {
        RegisterViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get())
    }
}
