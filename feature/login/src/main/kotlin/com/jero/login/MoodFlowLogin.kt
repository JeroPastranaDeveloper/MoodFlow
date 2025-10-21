package com.jero.login

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.jero.designsystem.components.MoodFlowAppBar
import com.jero.designsystem.components.PasswordTextField
import com.jero.login.LoginViewContract.UiIntent
import org.koin.androidx.compose.koinViewModel

@Composable
fun SharedTransitionScope.MoodFlowLogin(
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: LoginViewModel = koinViewModel()
) {
    SetStatusBarIconsColor()

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { MoodFlowAppBar() },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Login",
                modifier = Modifier.padding(vertical = 32.dp)
            )

            TextField(
                modifier = Modifier.padding(16.dp),
                value = state.email,
                onValueChange = { email ->
                    viewModel.sendIntent(UiIntent.OnEmailChanged(email))
                },
                label = { Text("Email") },
            )

            PasswordTextField(
                password = state.password,
                isPasswordVisible = state.isPasswordVisible,
                onPasswordChanged = { password ->
                    viewModel.sendIntent(UiIntent.OnPasswordChanged(password))
                },
                onChangePasswordVisibility = { visible ->
                    viewModel.sendIntent(UiIntent.OnChangePasswordVisibility(visible))
                }
            )
        }
    }
}

@Composable
private fun SetStatusBarIconsColor() {

    val systemUiController = rememberSystemUiController()

    systemUiController.setStatusBarColor(
        color = Color.Transparent,
        darkIcons = false
    )
}
