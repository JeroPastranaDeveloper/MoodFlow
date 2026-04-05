package com.jero.moodflow

import android.app.Application
import androidx.work.Configuration
import com.example.domain.di.authErrorHandlerModule
import com.example.domain.di.validatorModule
import com.example.domain.usecase.di.notesUseCaseModule
import com.example.domain.usecase.di.tagsUseCaseModule
import com.jero.authentication.data.di.authDatasourceModule
import com.jero.data.di.authUseCaseModule
import com.jero.data.di.preferencesModule
import com.jero.data.di.stringsProviderModule
import com.jero.database.di.notesDatasourceModule
import com.jero.database.di.workManagerModule
import com.jero.database.sync.NotesSyncManager
import com.jero.editnote.di.editNoteViewModelModule
import com.jero.home.di.homeViewModelModule
import com.jero.localdatabase.di.localDatabaseModule
import com.jero.login.di.loginViewModelModule
import com.jero.moodflow.di.mainViewModelModule
import com.jero.network.di.networkMonitorModule
import com.jero.register.di.registerViewModelModule
import com.jero.settings.di.settingsViewModelModule
import com.jero.trash.di.trashViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class App : Application(), Configuration.Provider, KoinComponent {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            workManagerFactory()

            modules(
                workManagerModule,
                mainViewModelModule,
                preferencesModule,
                stringsProviderModule,
                networkMonitorModule,
                localDatabaseModule,
                notesUseCaseModule,
                tagsUseCaseModule,
                authDatasourceModule,
                authUseCaseModule,
                notesDatasourceModule,
                authErrorHandlerModule,
                validatorModule,
                loginViewModelModule,
                registerViewModelModule,
                homeViewModelModule,
                editNoteViewModelModule,
                settingsViewModelModule,
                trashViewModelModule,
            )
        }

        val syncManager: NotesSyncManager by inject()
        syncManager.scheduleTrashClean()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(KoinWorkerFactory())
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
