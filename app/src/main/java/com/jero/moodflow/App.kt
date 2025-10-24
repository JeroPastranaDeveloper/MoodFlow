package com.jero.moodflow

import android.app.Application
import com.example.domain.di.authErrorHandlerModule
import com.example.domain.di.validatorModule
import com.jero.authentication.data.di.authDatasourceModule
import com.jero.data.di.authUseCaseModule
import com.jero.data.di.notesUseCaseModule
import com.jero.data.di.preferencesModule
import com.jero.database.di.notesDatasourceModule
import com.jero.home.di.homeViewModelModule
import com.jero.login.di.loginViewModelModule
import com.jero.moodflow.di.mainViewModelModule
import com.jero.navigation.navigationModule
import com.jero.register.di.registerViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                mainViewModelModule,
                navigationModule,
                preferencesModule,
                authDatasourceModule,
                authUseCaseModule,
                notesDatasourceModule,
                notesUseCaseModule,
                authErrorHandlerModule,
                validatorModule,
                loginViewModelModule,
                registerViewModelModule,
                homeViewModelModule,
            )
        }
    }
}
