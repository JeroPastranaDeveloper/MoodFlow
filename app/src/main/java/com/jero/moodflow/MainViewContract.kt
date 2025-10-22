package com.jero.moodflow

class MainViewContract {
    data class UiState(
        val isLogged: Boolean = false,
    )

    sealed class UiIntent
}
