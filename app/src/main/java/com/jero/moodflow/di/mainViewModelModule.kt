package com.jero.moodflow.di

import com.jero.moodflow.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mainViewModelModule = module {
    viewModel  {
        MainViewModel(
            get()
        )
    }
}
