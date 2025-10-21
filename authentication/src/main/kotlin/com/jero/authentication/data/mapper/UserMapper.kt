package com.jero.authentication.data.mapper

import com.google.firebase.auth.FirebaseUser
import com.jero.core.model.User

fun FirebaseUser.toDomain(): User = User(
        id = uid,
        email = email.orEmpty(),
    )
