package com.jero.authentication.data.di

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import org.koin.dsl.module

// TODO: CREAR MÓDULO ESPECÍFICO
val firebaseModule = module {
    single { Firebase.auth }

    single {
        FirebaseDatabase.getInstance(
            "https://moodflow-2d508-default-rtdb.firebaseio.com/"
        )
    }
}
