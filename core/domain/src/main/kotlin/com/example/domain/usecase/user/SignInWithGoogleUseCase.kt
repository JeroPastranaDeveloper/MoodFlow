package com.example.domain.usecase.user

import com.jero.core.model.User

interface SignInWithGoogleUseCase {
    suspend operator fun invoke(idToken: String): Result<User>
}
