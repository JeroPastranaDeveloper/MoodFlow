package com.jero.core.screen

import android.os.Build
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun <A> HandleActions(
    actions: Flow<A>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    handle: (A) -> Unit
) {
    val lifecycle = lifecycleOwner.lifecycle
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(lifecycle) {
        val job = coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                actions.collect { action ->
                    handle(action)
                }
            }
        }
        onDispose { job.cancel() }
    }
}

@Composable
fun SetStatusBarIconsColor(darkIcons: Boolean = true) {

    val systemUiController = rememberSystemUiController()

    systemUiController.setStatusBarColor(
        color = Color.Transparent,
        darkIcons = darkIcons
    )
}

@Composable
fun getTopSystemPadding(addExtraPadding: Boolean = false): Dp = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM -> {
        // Android 15+ - use native insets
        maxOf(
            WindowInsets.displayCutout.asPaddingValues().calculateTopPadding(),
            WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        )
    }

    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
        // Android 11–14 - use systemBars
        WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
    }

    else -> {
        // Android 10 and below - use systemBars with fallback
        val systemBarsPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
        if (systemBarsPadding > 0.dp) {
            systemBarsPadding
        } else {
            // Conservative fallback for problematic devices
            24.dp
        }
    }
} + if (addExtraPadding) 16.dp else 0.dp
