package com.jero.login

import androidx.lifecycle.viewModelScope
import com.example.domain.preferences.PreferencesHandler
import com.example.domain.usecase.SignInWithEmailUseCase
import com.example.domain.usecase.SignUpWithEmailUseCase
import com.example.domain.validator.EmailValidator
import com.example.domain.validator.PasswordValidator
import com.jero.core.model.AuthError
import com.jero.core.viewmodel.BaseViewModelWithActions
import com.jero.login.LoginViewContract.UiAction
import com.jero.login.LoginViewContract.UiIntent
import com.jero.login.LoginViewContract.UiState
import kotlinx.coroutines.launch

class LoginViewModel(
    private val preferencesHandler: PreferencesHandler,
    private val signInUseCase: SignInWithEmailUseCase,
    private val signUpUseCase: SignUpWithEmailUseCase,
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator,
) : BaseViewModelWithActions<UiState, UiIntent, UiAction>() {
    override val initialViewState = UiState()
    override suspend fun manageIntent(intent: UiIntent) {
        when (intent) {
            is UiIntent.OnEmailChanged -> setEmail(intent.email)
            is UiIntent.OnPasswordChanged -> setPassword(intent.password)
            is UiIntent.OnChangePasswordVisibility -> changePasswordVisibility(intent.visible)
            UiIntent.OnLoginWithGoogleClicked -> {}
            UiIntent.OnSignUpClicked -> {}
            UiIntent.OnEmailLoginClicked -> validateParams()
            UiIntent.OnBack -> {}
        }
    }

    private fun validateParams() {
        val emailError = emailValidator.validate(state.value.email)
        val passwordError = passwordValidator.validate(state.value.password)

        when {
            emailError == null && passwordError == null -> signUp()
            else -> setState {
                copy(
                    emailError = emailError?.let { emailValidator.getErrorMessage(it) },
                    passwordError = passwordError?.let { passwordValidator.getErrorMessage(it) },
                    hasToValidateEmail = emailError != null,
                    hasToValidatePassword = passwordError != null,
                )
            }
        }
    }

    private fun signUp() {
        viewModelScope.launch {
            val result = signUpUseCase(state.value.email, state.value.password)

            result.fold(
                onSuccess = { user ->
                    preferencesHandler.isLogged = true
                    dispatchAction(UiAction.ShowToast("logged"))
                },
                onFailure = { error ->
                    handleError(error)
                }
            )
        }
    }

    private fun handleError(error: Throwable) {
        val message = when (error) {
            is AuthError.InvalidEmail -> "Email inválido"
            is AuthError.InvalidPassword -> "Contraseña debe tener al menos 6 caracteres"
            is AuthError.UserNotFound -> "Usuario no encontrado"
            is AuthError.EmailAlreadyInUse -> "El email ya está en uso"
            is AuthError.WeakPassword -> "La contraseña es muy débil"
            is AuthError.NetworkError -> "Error de conexión"
            is AuthError.Unknown -> error.message
            else -> "Error desconocido"
        }

        dispatchAction(UiAction.ShowToast(message))
    }

    private fun setEmail(email: String) {
        if (state.value.hasToValidateEmail) {
            val validator = emailValidator.validate(email)

            validator?.let {
                val errorMessage = emailValidator.getErrorMessage(it)
                setState { copy(emailError = errorMessage) }
            } ?: setState { copy(emailError = null) }
        }
        setState { copy(email = email) }
    }

    private fun setPassword(password: String) {
        if (state.value.hasToValidatePassword) {
            val validator = passwordValidator.validate(password)

            validator?.let {
                val errorMessage = passwordValidator.getErrorMessage(it)
                setState { copy(passwordError = errorMessage) }
            } ?: setState { copy(passwordError = null) }
        }
        setState { copy(password = password) }
    }

    private fun changePasswordVisibility(visible: Boolean) {
        setState { copy(isPasswordVisible = visible) }
    }
}
