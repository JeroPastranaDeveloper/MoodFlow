package com.jero.login

import androidx.lifecycle.viewModelScope
import com.example.domain.handler.AuthErrorHandler
import com.example.domain.preferences.PreferencesHandler
import com.example.domain.usecase.tags.interfaces.SeedDefaultTagsUseCase
import com.example.domain.usecase.user.GetCurrentUserUseCase
import com.example.domain.usecase.user.SignInWithEmailUseCase
import com.example.domain.validator.EmailValidator
import com.example.domain.validator.PasswordValidator
import com.jero.core.viewmodel.BaseViewModelWithActions
import com.jero.login.LoginViewContract.UiAction
import com.jero.login.LoginViewContract.UiIntent
import com.jero.login.LoginViewContract.UiState
import kotlinx.coroutines.launch

class LoginViewModel(
    private val preferencesHandler: PreferencesHandler,
    private val signInUseCase: SignInWithEmailUseCase,
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator,
    private val authErrorHandler: AuthErrorHandler,
    private val seedDefaultTagsUseCase: SeedDefaultTagsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : BaseViewModelWithActions<UiState, UiIntent, UiAction>() {
    override val initialViewState = UiState()
    override suspend fun manageIntent(intent: UiIntent) {
        when (intent) {
            is UiIntent.OnEmailChanged -> setEmail(intent.email)
            is UiIntent.OnPasswordChanged -> setPassword(intent.password)
            is UiIntent.OnChangePasswordVisibility -> changePasswordVisibility(intent.visible)
            UiIntent.OnLoginWithGoogleClicked -> {}
            UiIntent.OnSignUpClicked -> goRegister()
            UiIntent.OnEmailLoginClicked -> validateParams()
        }
    }

    private fun goRegister() {
        setState { copy(emailError = null, passwordError = null) }
        dispatchAction(UiAction.GoRegister)
    }

    private fun validateParams() {
        val emailError = emailValidator.validate(state.value.email)
        val passwordError = passwordValidator.validate(state.value.password)

        when {
            emailError == null && passwordError == null -> signIn()
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

    private fun signIn() {
        viewModelScope.launch {
            val result = signInUseCase(state.value.email, state.value.password)

            result.fold(
                onSuccess = { _ ->
                    preferencesHandler.isLogged = true
                    getCurrentUserUseCase()?.id?.let { seedDefaultTagsUseCase(it) }
                    dispatchAction(UiAction.GoHome)
                },
                onFailure = { error ->
                    val message = authErrorHandler(error)
                    dispatchAction(UiAction.ShowToast(message))
                }
            )
        }
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
