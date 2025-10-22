package com.jero.moodflow

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import com.jero.moodflow.ui.MoodFlowMain
import com.jero.navigation.AppComposeNavigator
import com.jero.navigation.LocalComposeNavigator
import com.jero.navigation.MoodFLowScreen
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : FragmentActivity() {
    private val composeNavigator: AppComposeNavigator<MoodFLowScreen> by inject()
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state by viewModel.state.collectAsState()
            CompositionLocalProvider(
                LocalComposeNavigator provides composeNavigator
            ) {
                MoodFlowMain(composeNavigator = composeNavigator, isLogged = state.isLogged)
            }
        }
    }
}
