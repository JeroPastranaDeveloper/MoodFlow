package com.jero.login

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jero.core.designsystem.R
import com.jero.core.screen.HandleActions
import com.jero.core.screen.SetStatusBarIconsColor
import com.jero.designsystem.components.MoodFlowButton
import com.jero.designsystem.components.MoodFlowTextField
import com.jero.designsystem.components.PasswordTextField
import com.jero.designsystem.theme.MoodFlowColors
import com.jero.designsystem.utils.rememberKeyboardAsState
import com.jero.login.LoginViewContract.UiAction
import com.jero.login.LoginViewContract.UiIntent
import org.koin.androidx.compose.koinViewModel

@Composable
fun SharedTransitionScope.MoodFlowLogin(
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: LoginViewModel = koinViewModel()
) {
    SetStatusBarIconsColor(darkIcons = true)
    val isKeyboardOpen by rememberKeyboardAsState()
    val focusManager = LocalFocusManager.current

    val state by viewModel.state.collectAsState()

    BackHandler {
        if (!isKeyboardOpen) {
            viewModel.sendIntent(UiIntent.OnBack)
        }
    }

    HandleActions(viewModel.actions) { action ->
        when(action) {
            UiAction.GoRegister -> {}
        }
    }

    LaunchedEffect(isKeyboardOpen) {
        if (!isKeyboardOpen) {
            focusManager.clearFocus(force = true)
        }
    }

    // Animaciones de entrada
    val titleAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 600, delayMillis = 100)
    )
    val titleOffsetY by animateDpAsState(
        targetValue = 0.dp,
        animationSpec = tween(durationMillis = 600, delayMillis = 100, easing = FastOutSlowInEasing)
    )

    val emailAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500, delayMillis = 300)
    )
    val emailOffsetY by animateDpAsState(
        targetValue = 0.dp,
        animationSpec = tween(durationMillis = 500, delayMillis = 300, easing = FastOutSlowInEasing)
    )

    val passwordAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500, delayMillis = 450)
    )
    val passwordOffsetY by animateDpAsState(
        targetValue = 0.dp,
        animationSpec = tween(durationMillis = 500, delayMillis = 450, easing = FastOutSlowInEasing)
    )

    val loginButtonAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500, delayMillis = 600)
    )
    val loginButtonScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = 600,
            easing = FastOutSlowInEasing
        )
    )

    val googleButtonAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500, delayMillis = 750)
    )
    val googleButtonScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = 750,
            easing = FastOutSlowInEasing
        )
    )

    val signUpAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500, delayMillis = 900)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MoodFlowColors.defaultLightColors().backGroundColor)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val emailFocusRequester = remember { FocusRequester() }
        val passwordFocusRequester = remember { FocusRequester() }

        Text(
            text = "Login",
            fontSize = 40.sp,
            modifier = Modifier
                .padding(top = 32.dp)
                .offset(y = titleOffsetY - 30.dp)
                .alpha(titleAlpha)
        )

        Spacer(modifier = Modifier.height(16.dp))

        MoodFlowTextField(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .offset(y = emailOffsetY - 20.dp)
                .alpha(emailAlpha),
            text = state.email,
            placeHolder = "Email",
            focusRequester = emailFocusRequester,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { passwordFocusRequester.requestFocus() }
            )
        ) { email ->
            viewModel.sendIntent(UiIntent.OnEmailChanged(email))
        }

        Spacer(modifier = Modifier.height(16.dp))

        PasswordTextField(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .offset(y = passwordOffsetY - 20.dp)
                .alpha(passwordAlpha),
            password = state.password,
            isPasswordVisible = state.isPasswordVisible,
            focusRequester = passwordFocusRequester,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus(force = true) }
            ),
            onPasswordChanged = { password ->
                viewModel.sendIntent(UiIntent.OnPasswordChanged(password))
            },
            onChangePasswordVisibility = { visible ->
                viewModel.sendIntent(UiIntent.OnChangePasswordVisibility(visible))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .scale(loginButtonScale)
                .alpha(loginButtonAlpha)
        ) {
            LoginButton {
                viewModel.sendIntent(UiIntent.OnEmailLoginClicked)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .scale(googleButtonScale)
                .alpha(googleButtonAlpha)
        ) {
            LoginWithGoogleButton {
                viewModel.sendIntent(UiIntent.OnLoginWithGoogleClicked)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        NotAccountText(
            modifier = Modifier.alpha(signUpAlpha)
        ) {
            viewModel.sendIntent(UiIntent.OnSignUpClicked)
        }
    }
}

@Composable
private fun NotAccountText(modifier: Modifier, onClick: () -> Unit) {
    Row(modifier = Modifier) {
        Text(
            text = "Don't have an account?",
            fontSize = 14.sp,
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            modifier = Modifier.clickable {
                onClick()
            },
            text = "Sign up",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun LoginButton(onClick: () -> Unit) {
    MoodFlowButton(
        text = "Login",
        backgroundColor = MoodFlowColors.defaultLightColors().pastelBlue,
        textColor = Color.White
    ) { onClick() }
}

@Composable
private fun LoginWithGoogleButton(onClick: () -> Unit) {
    MoodFlowButton(
        text = "Login with Google",
        leadingIconRes = R.drawable.ic_google_logo,
    ) { onClick() }
}
