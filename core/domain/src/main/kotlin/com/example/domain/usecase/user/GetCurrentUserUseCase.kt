package com.example.domain.usecase.user

import com.jero.core.model.User

interface GetCurrentUserUseCase {
    suspend operator fun invoke(): User?
}
