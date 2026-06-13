package com.jero.register

import androidx.lifecycle.viewModelScope
import com.example.domain.handler.AuthErrorHandler
import com.example.domain.preferences.PreferencesHandler
import com.example.domain.providers.StringsProvider
import com.example.domain.usecase.tags.interfaces.SeedDefaultTagsUseCase
import com.example.domain.usecase.user.GetCurrentUserUseCase
import com.example.domain.usecase.user.GetGoogleIdTokenUseCase
import com.example.domain.usecase.user.SignInWithGoogleUseCase
import com.example.domain.usecase.user.SignUpWithEmailUseCase
import com.example.domain.validator.EmailValidator
import com.example.domain.validator.PasswordValidator
import com.jero.core.designsystem.R
import com.jero.core.model.AuthError
import com.jero.core.viewmodel.BaseViewModelWithActions
import com.jero.register.RegisterViewContract.UiAction
import com.jero.register.RegisterViewContract.UiIntent
import com.jero.register.RegisterViewContract.UiState
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val signUpUseCase: SignUpWithEmailUseCase,
    private val preferencesHandler: PreferencesHandler,
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator,
    private val authErrorHandler: AuthErrorHandler,
    private val stringsProvider: StringsProvider,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val getGoogleIdTokenUseCase: GetGoogleIdTokenUseCase,
    private val seedDefaultTagsUseCase: SeedDefaultTagsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : BaseViewModelWithActions<UiState, UiIntent, UiAction>() {
    override val initialViewState = UiState()
    override suspend fun manageIntent(intent: UiIntent) {
        when (intent) {
            is UiIntent.OnEmailChanged -> setEmail(intent.email)
            is UiIntent.OnPasswordChanged -> setPassword(intent.password)
            is UiIntent.OnRepeatPasswordChanged -> setRepeatPassword(intent.repeatPassword)
            is UiIntent.OnChangePasswordVisibility -> changePasswordVisibility(intent.visible)
            is UiIntent.OnChangeRepeatPasswordVisibility -> changeRepeatPasswordVisibility(intent.visible)
            UiIntent.OnLoginWithGoogleClicked -> signInWithGoogle()
            UiIntent.OnSignUpClicked -> validateParams()
            UiIntent.OnGoBack -> goBack()
        }
    }

    private fun goBack() {
        setState { copy(emailError = null, passwordError = null, repeatPasswordError = null) }
        dispatchAction(UiAction.GoBack)
    }

    private fun validateParams() {
        val emailError = emailValidator.validate(state.value.email)
        val passwordError = passwordValidator.validate(state.value.password)
        val repeatPasswordError = if (state.value.password != state.value.repeatPassword)
            stringsProvider(R.string.validation_error_password_do_not_match) else null

        when {
            emailError == null && passwordError == null && repeatPasswordError == null -> signUp()
            else -> setState {
                copy(
                    emailError = emailError?.let { emailValidator.getErrorMessage(it) },
                    passwordError = passwordError?.let { passwordValidator.getErrorMessage(it) },
                    repeatPasswordError = repeatPasswordError,
                    hasToValidateEmail = emailError != null,
                    hasToValidatePassword = passwordError != null,
                )
            }
        }
    }

    private fun signUp() {
        viewModelScope.launch {
            signUpUseCase(state.value.email, state.value.password).fold(
                onSuccess = { onAuthSuccess() },
                onFailure = { dispatchAction(UiAction.ShowToast(authErrorHandler(it))) }
            )
        }
    }

    private fun signInWithGoogle() {
        viewModelScope.launch {
            try {
                val idToken = getGoogleIdTokenUseCase() ?: return@launch
                signInWithGoogleUseCase(idToken).fold(
                    onSuccess = { onAuthSuccess() },
                    onFailure = { dispatchAction(UiAction.ShowToast(authErrorHandler(it))) }
                )
            } catch (e: Exception) {
                dispatchAction(UiAction.ShowToast(authErrorHandler(AuthError.Unknown(e.message.orEmpty()))))
            }
        }
    }

    private suspend fun onAuthSuccess() {
        preferencesHandler.isLogged = true
        getCurrentUserUseCase()?.id?.let { seedDefaultTagsUseCase(it) }
        dispatchAction(UiAction.GoHome)
    }

    private fun setEmail(email: String) {
        if (state.value.hasToValidateEmail) {
            val validator = emailValidator.validate(email)
            validator?.let { setState { copy(emailError = emailValidator.getErrorMessage(it)) } }
                ?: setState { copy(emailError = null) }
        }
        setState { copy(email = email) }
    }

    private fun setPassword(password: String) {
        if (state.value.hasToValidatePassword) {
            val validator = passwordValidator.validate(password)
            validator?.let { setState { copy(passwordError = passwordValidator.getErrorMessage(it)) } }
                ?: setState { copy(passwordError = null) }
        }
        setState { copy(password = password) }
        setRepeatPassword(state.value.repeatPassword)
    }

    private fun setRepeatPassword(repeatPassword: String) {
        setRepeatPasswordError(repeatPassword)
        setState { copy(repeatPassword = repeatPassword) }
    }

    private fun setRepeatPasswordError(repeatPassword: String) {
        val errorMessage = if (repeatPassword != state.value.password)
            stringsProvider(R.string.validation_error_password_do_not_match) else null
        setState { copy(repeatPasswordError = errorMessage) }
    }

    private fun changePasswordVisibility(visible: Boolean) {
        setState { copy(isPasswordVisible = visible) }
    }

    private fun changeRepeatPasswordVisibility(visible: Boolean) {
        setState { copy(isRepeatPasswordVisible = visible) }
    }
}
