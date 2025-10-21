package com.jero.moodflow.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.jero.navigation.AppComposeNavigator
import com.jero.moodflow.navigation.MoodFlowNavHost
import com.jero.moodflow.ui.theme.MoodFlowTheme
import com.jero.navigation.MoodFLowScreen

@Composable
fun MoodFlowMain(composeNavigator: AppComposeNavigator<MoodFLowScreen>) {
    MoodFlowTheme {
        val navHostController = rememberNavController()

        LaunchedEffect(Unit) {
            composeNavigator.handleNavigationCommands(navHostController)
        }

        MoodFlowNavHost(navHostController = navHostController)
    }
}
