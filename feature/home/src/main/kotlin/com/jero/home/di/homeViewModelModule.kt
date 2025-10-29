package com.jero.home.di

import com.jero.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeViewModelModule = module {
    viewModel {
        HomeViewModel(
            get(),
            get(),
            get(),
        )
    }
}
