package com.jero.login

class LoginViewContract {
    data class UiState(
        val isLoading: Boolean = false,
    )

    sealed class UiIntent {}

    sealed class UiAction {}
}
