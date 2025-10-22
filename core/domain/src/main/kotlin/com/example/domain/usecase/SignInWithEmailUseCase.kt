package com.example.domain.usecase

import com.jero.core.model.User

interface SignInWithEmailUseCase {
    suspend operator fun invoke(email: String, password: String): Result<User>
}
