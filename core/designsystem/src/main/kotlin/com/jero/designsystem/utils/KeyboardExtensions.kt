package com.jero.designsystem.utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp

@Composable
fun rememberKeyboardAsState(): MutableState<Boolean> {
    val imeBottomPadding = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    return remember(imeBottomPadding) {
        mutableStateOf(imeBottomPadding > 0.dp)
    }
}
