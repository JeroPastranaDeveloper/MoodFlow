package com.jero.designsystem.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier

private val LocalColors = compositionLocalOf<MoodFlowColors> {
    error("No colors provided! Make sure to wrap all usages of MoodFlow components in MoodFlowTheme.")
}

@Composable
fun MoodFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colors: MoodFlowColors = if (darkTheme) {
        MoodFlowColors.Companion.defaultDarkColors()
    } else {
        MoodFlowColors.Companion.defaultLightColors()
    },
    background: MoodFlowBackground = MoodFlowBackground.defaultBackground(darkTheme),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalColors provides colors,
        LocalBackgroundTheme provides background,
    ) {
        Box(
            modifier = Modifier.background(background.color)
        ) {
            content()
        }
    }
}

/**
 * Contains ease-of-use accessors for different properties used to style and customize the app
 * look and feel.
 */

object MoodFlowTheme {
    val colors: MoodFlowColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val background: MoodFlowBackground
        @Composable
        @ReadOnlyComposable
        get() = LocalBackgroundTheme.current
}
