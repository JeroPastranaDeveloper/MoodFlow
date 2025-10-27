package com.jero.register

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jero.core.designsystem.R
import com.jero.core.screen.HandleActions
import com.jero.core.screen.SetStatusBarIconsColor
import com.jero.core.screen.getTopSystemPadding
import com.jero.designsystem.components.MoodFlowButton
import com.jero.designsystem.components.MoodFlowTextField
import com.jero.designsystem.components.PasswordTextField
import com.jero.designsystem.theme.MoodFlowColors
import com.jero.designsystem.utils.rememberKeyboardAsState
import com.jero.navigation.MoodFLowScreen
import com.jero.navigation.currentComposeNavigator
import com.jero.register.RegisterViewContract.UiAction
import com.jero.register.RegisterViewContract.UiIntent
import org.koin.androidx.compose.koinViewModel

@Composable
fun SharedTransitionScope.MoodFlowRegister(
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: RegisterViewModel = koinViewModel()
) {
    SetStatusBarIconsColor()
    val composeNavigator = currentComposeNavigator
    val isKeyboardOpen by rememberKeyboardAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val state by viewModel.state.collectAsStateWithLifecycle()

    BackHandler {
        if (!isKeyboardOpen) {
            viewModel.sendIntent(UiIntent.OnGoBack)
        }
    }

    HandleActions(viewModel.actions) { action ->
        when(action) {
            UiAction.GoBack -> composeNavigator.navigateUp()
            UiAction.GoHome -> composeNavigator.navigate(MoodFLowScreen.Home)
            is UiAction.ShowToast -> Toast.makeText(context, action.message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(isKeyboardOpen) {
        if (!isKeyboardOpen) {
            focusManager.clearFocus(force = true)
        }
    }

    Scaffold(
        topBar = {
            Icon(
                modifier = Modifier
                    .padding(start = 16.dp, top = getTopSystemPadding())
                    .clickable { viewModel.sendIntent(UiIntent.OnGoBack) },
                painter = rememberVectorPainter(image = Icons.AutoMirrored.Filled.ArrowBack),
                contentDescription = null,
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MoodFlowColors.defaultLightColors().backGroundColor)
                .padding(paddingValues)
                .padding(horizontal = 32.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            val emailFocusRequester = remember { FocusRequester() }
            val passwordFocusRequester = remember { FocusRequester() }
            val repeatPasswordFocusRequester = remember { FocusRequester() }

            Text(
                modifier = Modifier.padding(top = 32.dp),
                text = "Register",
                fontSize = 40.sp,
            )

            Spacer(modifier = Modifier.height(16.dp))

            MoodFlowTextField(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = state.email,
                placeHolder = "Email",
                focusRequester = emailFocusRequester,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequester.requestFocus() }
                ),
                isError = state.emailError != null,
                errorMessage = state.emailError,
            ) { email ->
                viewModel.sendIntent(UiIntent.OnEmailChanged(email))
            }

            Spacer(modifier = Modifier.height(8.dp))

            PasswordTextField(
                modifier = Modifier.padding(horizontal = 16.dp),
                password = state.password,
                placeHolder = "Password",
                isPasswordVisible = state.isPasswordVisible,
                focusRequester = passwordFocusRequester,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { repeatPasswordFocusRequester.requestFocus() }
                ),
                onPasswordChanged = { password ->
                    viewModel.sendIntent(UiIntent.OnPasswordChanged(password))
                },
                onChangePasswordVisibility = { visible ->
                    viewModel.sendIntent(UiIntent.OnChangePasswordVisibility(visible))
                },
                isError = state.passwordError != null,
                errorMessage = state.passwordError,
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordTextField(
                modifier = Modifier.padding(horizontal = 16.dp),
                password = state.repeatPassword,
                placeHolder = "Confirm Password",
                isPasswordVisible = state.isRepeatPasswordVisible,
                focusRequester = repeatPasswordFocusRequester,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus(force = true) }
                ),
                onPasswordChanged = { password ->
                    viewModel.sendIntent(UiIntent.OnRepeatPasswordChanged(password))
                },
                onChangePasswordVisibility = { visible ->
                    viewModel.sendIntent(UiIntent.OnChangeRepeatPasswordVisibility(visible))
                },
                isError = state.repeatPasswordError != null,
                errorMessage = state.repeatPasswordError,
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginButton {
                viewModel.sendIntent(UiIntent.OnSignUpClicked)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LoginWithGoogleButton {
                viewModel.sendIntent(UiIntent.OnLoginWithGoogleClicked)
            }

            Spacer(modifier = Modifier.height(16.dp))

            NotAccountText {
                viewModel.sendIntent(UiIntent.OnGoBack)
            }
        }
    }
}

@Composable
private fun NotAccountText(onClick: () -> Unit) {
    Row {
        Text(
            text = "Have an account?",
            fontSize = 14.sp,
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            modifier = Modifier.clickable {
                onClick()
            },
            text = "Sign in",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun LoginButton(onClick: () -> Unit) {
    MoodFlowButton(
        text = "Register",
        backgroundColor = MoodFlowColors.defaultLightColors().pastelBlue,
        textColor = Color.White
    ) { onClick() }
}

@Composable
private fun LoginWithGoogleButton(onClick: () -> Unit) {
    MoodFlowButton(
        text = "Login with Google",
        leadingIconRes = R.drawable.ic_google_logo,
        addBorder = true,
    ) { onClick() }
}
