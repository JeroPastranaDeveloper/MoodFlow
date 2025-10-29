package com.jero.database.di

import androidx.work.WorkManager
import com.jero.database.workmanager.SyncNotesWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val workManagerModule = module {
    // WorkManager instance
    single { WorkManager.getInstance(androidContext()) }

    // Worker
    worker { SyncNotesWorker(get(), get(), get()) }
}
