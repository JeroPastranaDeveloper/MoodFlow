package com.jero.navigation

import kotlinx.serialization.Serializable

sealed interface MoodFLowScreen {
    @Serializable
    data object Home : MoodFLowScreen

    @Serializable
    data object Login : MoodFLowScreen

    @Serializable
    data object Register : MoodFLowScreen

    /*@Serializable
    data object SelectDatabase : MoodFLowScreen

    @Serializable
    data object Accounts : MoodFLowScreen

    @Serializable
    data class AddEditAccount(val id: String) : MoodFLowScreen {
        companion object {
            val typeMap = mapOf(
                typeOf<String>() to NavType.StringType
            )
        }
    }

    @Serializable
    data class AccountDetail(val id: String) : MoodFLowScreen {
        companion object {
            val typeMap = mapOf(
                typeOf<String>() to NavType.StringType
            )
        }
    }*/
}
