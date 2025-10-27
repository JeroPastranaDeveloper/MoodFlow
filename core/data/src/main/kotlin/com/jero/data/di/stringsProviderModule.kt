package com.jero.data.di

import com.example.domain.providers.StringsProvider
import com.jero.data.providers.StringsProviderImpl
import org.koin.dsl.module

val stringsProviderModule = module {
    single<StringsProvider> { StringsProviderImpl(get()) }
}
