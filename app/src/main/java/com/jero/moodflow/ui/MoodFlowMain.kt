package com.jero.moodflow.ui

import androidx.compose.runtime.Composable
import com.jero.moodflow.navigation.MoodFlowNavigation
import com.jero.moodflow.ui.theme.MoodFlowTheme

@Composable
fun MoodFlowMain(isLogged: Boolean) {
    MoodFlowTheme {
        MoodFlowNavigation(isLogged = isLogged)
    }
}
