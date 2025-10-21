package com.example.domain.usecase

import com.example.domain.repository.AuthRepository
import com.jero.core.model.AuthError
import com.jero.core.model.User

class SignUpWithEmailUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun signup(email: String, password: String): Result<User> {
        if (!isValidEmail(email)) {
            return Result.failure(AuthError.InvalidEmail)
        }
        
        if (!isValidPassword(password)) {
            return Result.failure(AuthError.InvalidPassword)
        }
        
        return authRepository.signUpWithEmail(email, password)
    }
    
    private fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    
    private fun isValidPassword(password: String): Boolean =
        password.length >= 6
}

// TODO: HACER INTERFACES DE TODOS LOS CASOS DE USO, MIRAR SI HAY QUE TOCAR EL MÓDULO, HACER LA FUNCIÓN HANDLE ERROR PARA MOSTRAR EL MENSAJE DE ERROR DESDE AQUÍ Y NO DESDE EL VM
