package com.jero.database.di

import androidx.work.WorkManager
import com.jero.database.workmanager.CleanTrashWorker
import com.jero.database.workmanager.SyncNotesWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val workManagerModule = module {
    single { WorkManager.getInstance(androidContext()) }

    worker { SyncNotesWorker(get(), get(), get()) }
    worker { CleanTrashWorker(get(), get(), get()) }
}
