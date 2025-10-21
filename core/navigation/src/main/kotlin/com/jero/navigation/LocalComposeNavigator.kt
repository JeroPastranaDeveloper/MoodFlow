package com.jero.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf

val LocalComposeNavigator: ProvidableCompositionLocal<AppComposeNavigator<MoodFLowScreen>> =
    compositionLocalOf { error("No AppComposeNavigator provided!") }

val currentComposeNavigator: AppComposeNavigator<MoodFLowScreen>
    @Composable
    @ReadOnlyComposable
    get() = LocalComposeNavigator.current
