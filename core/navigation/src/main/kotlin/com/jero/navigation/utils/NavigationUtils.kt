package com.jero.navigation.utils

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.jero.navigation.MoodFLowScreen

fun NavBackStack<NavKey>.clearAndNavigateTo(
    screen: MoodFLowScreen,
) {
    clear()
    add(screen)
}

fun NavBackStack<NavKey>.goBack() {
    removeLastOrNull()
}
