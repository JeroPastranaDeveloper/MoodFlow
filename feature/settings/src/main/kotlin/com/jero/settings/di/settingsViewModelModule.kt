package com.jero.settings.di

import com.jero.settings.SettingsViewModel
import com.jero.settings.TagsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsViewModelModule = module {
    viewModel { SettingsViewModel(get(), get(), get()) }
    viewModel { TagsViewModel(get(), get(), get(), get(), get()) }
}
