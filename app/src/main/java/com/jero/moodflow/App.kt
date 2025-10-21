package com.jero.moodflow

import android.app.Application
import com.jero.data.di.preferencesModule
import com.jero.home.di.homeViewModelModule
import com.jero.login.di.loginViewModelModule
import com.jero.navigation.navigationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                navigationModule,
                preferencesModule,
                loginViewModelModule,
                homeViewModelModule,
            )
        }
    }
}
