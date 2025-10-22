package com.jero.moodflow.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.jero.navigation.MoodFLowScreen

@Composable
fun MoodFlowNavHost(navHostController: NavHostController, isLogged: Boolean) {
    SharedTransitionLayout {
        NavHost(
            navController = navHostController,
            startDestination = if (isLogged) MoodFLowScreen.Home else MoodFLowScreen.Login
        ) {
            moodFlowNavigation()
        }
    }
}
