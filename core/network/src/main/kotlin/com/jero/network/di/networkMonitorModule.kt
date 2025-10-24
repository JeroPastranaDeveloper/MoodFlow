package com.jero.network.di

import com.jero.network.NetworkMonitor
import com.jero.network.NetworkMonitorImpl
import org.koin.dsl.module

val networkMonitorModule = module {
    single<NetworkMonitor> { NetworkMonitorImpl(context = get()) }
}
