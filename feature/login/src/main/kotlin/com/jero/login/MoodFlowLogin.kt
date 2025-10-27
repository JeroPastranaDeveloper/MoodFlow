package com.jero.login

import android.app.Activity
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jero.core.designsystem.R
import com.jero.core.screen.HandleActions
import com.jero.core.screen.SetStatusBarIconsColor
import com.jero.designsystem.components.MoodFlowButton
import com.jero.designsystem.components.MoodFlowPasswordTextField
import com.jero.designsystem.components.MoodFlowTextField
import com.jero.designsystem.theme.MoodFlowColors
import com.jero.designsystem.utils.rememberKeyboardAsState
import com.jero.login.LoginViewContract.UiAction
import com.jero.login.LoginViewContract.UiIntent
import com.jero.navigation.MoodFLowScreen
import com.jero.navigation.currentComposeNavigator
import org.koin.androidx.compose.koinViewModel

@Composable
fun SharedTransitionScope.MoodFlowLogin(
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: LoginViewModel = koinViewModel(),
) {
    SetStatusBarIconsColor()
    val composeNavigator = currentComposeNavigator
    val isKeyboardOpen by rememberKeyboardAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val state by viewModel.state.collectAsStateWithLifecycle()

    BackHandler {
        if (!isKeyboardOpen) {
            (context as? Activity)?.finish()
        }
    }

    HandleActions(viewModel.actions) { action ->
        when (action) {
            UiAction.GoHome -> composeNavigator.navigate(MoodFLowScreen.Home)
            UiAction.GoRegister -> composeNavigator.navigate(MoodFLowScreen.Register)
            is UiAction.ShowToast -> Toast.makeText(context, action.message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(isKeyboardOpen) {
        if (!isKeyboardOpen) {
            focusManager.clearFocus(force = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MoodFlowColors.defaultLightColors().backGroundColor)
            .padding(horizontal = 32.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val emailFocusRequester = remember { FocusRequester() }
        val passwordFocusRequester = remember { FocusRequester() }

        Text(
            modifier = Modifier.padding(top = 32.dp),
            text = stringResource(R.string.login),
            fontSize = 40.sp,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MoodFlowTextField(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = state.email,
            placeHolder = stringResource(R.string.email),
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

        MoodFlowPasswordTextField(
            modifier = Modifier.padding(horizontal = 16.dp),
            password = state.password,
            placeHolder = stringResource(R.string.password),
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
            },
            isError = state.passwordError != null,
            errorMessage = state.passwordError,
        )

        Spacer(modifier = Modifier.height(16.dp))

        LoginButton {
            focusManager.clearFocus(force = true)
            viewModel.sendIntent(UiIntent.OnEmailLoginClicked)
        }

        /*Spacer(modifier = Modifier.height(16.dp))

        LoginWithGoogleButton {
            focusManager.clearFocus(force = true)
            viewModel.sendIntent(UiIntent.OnLoginWithGoogleClicked)
        }*/

        Spacer(modifier = Modifier.height(16.dp))

        NotAccountText {
            viewModel.sendIntent(UiIntent.OnSignUpClicked)
        }
    }
}

@Composable
private fun NotAccountText(onClick: () -> Unit) {
    Row {
        Text(
            text = stringResource(R.string.not_account_question),
            fontSize = 14.sp,
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            modifier = Modifier.clickable {
                onClick()
            },
            text = stringResource(R.string.sign_up),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun LoginButton(onClick: () -> Unit) {
    MoodFlowButton(
        text = stringResource(R.string.login),
        backgroundColor = MoodFlowColors.defaultLightColors().pastelBlue,
        textColor = Color.White
    ) { onClick() }
}

@Composable
private fun LoginWithGoogleButton(onClick: () -> Unit) {
    MoodFlowButton(
        text = stringResource(R.string.login_with_google),
        leadingIconRes = R.drawable.ic_google_logo,
        addBorder = true,
    ) { onClick() }
}
