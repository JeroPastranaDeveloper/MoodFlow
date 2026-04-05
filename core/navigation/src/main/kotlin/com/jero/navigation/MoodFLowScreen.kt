package com.jero.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface MoodFLowScreen : NavKey {
    @Serializable
    data object Home : MoodFLowScreen

    @Serializable
    data object Login : MoodFLowScreen

    @Serializable
    data object Register : MoodFLowScreen

    @Serializable
    data class EditNote(val id: String = "") : MoodFLowScreen

    @Serializable
    data object Settings : MoodFLowScreen

    @Serializable
    data object Trash : MoodFLowScreen

    @Serializable
    data object Tags : MoodFLowScreen
}
