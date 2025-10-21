package com.jero.moodflow.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.jero.navigation.MoodFLowScreen

@Composable
fun MoodFlowNavHost(navHostController: NavHostController) {
    SharedTransitionLayout {
        NavHost(
            navController = navHostController,
            startDestination = MoodFLowScreen.Login
        ) {
            moodFlowNavigation()
        }
    }
}
