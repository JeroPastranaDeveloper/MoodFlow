package com.jero.register

import android.widget.Toast
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
import com.jero.core.screen.getTopSystemPadding
import com.jero.designsystem.components.MoodFlowButton
import com.jero.designsystem.components.MoodFlowPasswordTextField
import com.jero.designsystem.components.MoodFlowTextField
import com.jero.designsystem.theme.MoodFlowColors
import com.jero.designsystem.utils.rememberKeyboardAsState
import com.jero.register.RegisterViewContract.UiAction
import com.jero.register.RegisterViewContract.UiIntent
import com.jero.register.RegisterViewContract.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun MoodFlowRegister(
    viewModel: RegisterViewModel = koinViewModel(),
    onGoHome: () -> Unit,
    onGoBack: () -> Unit,
) {
    SetStatusBarIconsColor()
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    HandleActions(viewModel.actions) { action ->
        when (action) {
            UiAction.GoBack -> onGoBack()
            UiAction.GoHome -> onGoHome()
            is UiAction.ShowToast -> Toast.makeText(context, action.message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    Content(
        state = state,
        onEmailChanged = { viewModel.sendIntent(UiIntent.OnEmailChanged(it)) },
        onPasswordChanged = { viewModel.sendIntent(UiIntent.OnPasswordChanged(it)) },
        onChangePasswordVisibility = { viewModel.sendIntent(UiIntent.OnChangePasswordVisibility(it)) },
        onRepeatPasswordChanged = { viewModel.sendIntent(UiIntent.OnRepeatPasswordChanged(it)) },
        onChangeRepeatPasswordVisibility = {
            viewModel.sendIntent(
                UiIntent.OnChangeRepeatPasswordVisibility(
                    it
                )
            )
        },
        onSignUpClicked = { viewModel.sendIntent(UiIntent.OnSignUpClicked) },
        onGoBack = { viewModel.sendIntent(UiIntent.OnGoBack) },
    )
}

@Composable
private fun Content(
    state: UiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onChangePasswordVisibility: (Boolean) -> Unit,
    onRepeatPasswordChanged: (String) -> Unit,
    onChangeRepeatPasswordVisibility: (Boolean) -> Unit,
    onSignUpClicked: () -> Unit,
    onGoBack: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val isKeyboardOpen by rememberKeyboardAsState()
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val repeatPasswordFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isKeyboardOpen) {
        if (!isKeyboardOpen) focusManager.clearFocus(force = true)
    }

    Scaffold(
        topBar = {
            Icon(
                modifier = Modifier
                    .padding(start = 16.dp, top = getTopSystemPadding())
                    .clickable { onGoBack() },
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
            Text(
                modifier = Modifier.padding(top = 32.dp),
                text = stringResource(R.string.register),
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
            ) { onEmailChanged(it) }

            Spacer(modifier = Modifier.height(8.dp))

            MoodFlowPasswordTextField(
                modifier = Modifier.padding(horizontal = 16.dp),
                password = state.password,
                placeHolder = stringResource(R.string.password),
                isPasswordVisible = state.isPasswordVisible,
                focusRequester = passwordFocusRequester,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { repeatPasswordFocusRequester.requestFocus() }
                ),
                onPasswordChanged = { onPasswordChanged(it) },
                onChangePasswordVisibility = { onChangePasswordVisibility(it) },
                isError = state.passwordError != null,
                errorMessage = state.passwordError,
            )

            Spacer(modifier = Modifier.height(16.dp))

            MoodFlowPasswordTextField(
                modifier = Modifier.padding(horizontal = 16.dp),
                password = state.repeatPassword,
                placeHolder = stringResource(R.string.confirm_password),
                isPasswordVisible = state.isRepeatPasswordVisible,
                focusRequester = repeatPasswordFocusRequester,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus(force = true) }
                ),
                onPasswordChanged = { onRepeatPasswordChanged(it) },
                onChangePasswordVisibility = { onChangeRepeatPasswordVisibility(it) },
                isError = state.repeatPasswordError != null,
                errorMessage = state.repeatPasswordError,
            )

            Spacer(modifier = Modifier.height(16.dp))

            RegisterButton {
                focusManager.clearFocus(force = true)
                onSignUpClicked()
            }

            /*Spacer(modifier = Modifier.height(16.dp))

            LoginWithGoogleButton {
                viewModel.sendIntent(UiIntent.OnLoginWithGoogleClicked)
            }*/

            Spacer(modifier = Modifier.height(16.dp))

            NotAccountText {
                focusManager.clearFocus(force = true)
                onGoBack()
            }
        }
    }
}

@Composable
private fun NotAccountText(onClick: () -> Unit) {
    Row {
        Text(
            text = stringResource(R.string.have_account_question),
            fontSize = 14.sp,
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            modifier = Modifier.clickable { onClick() },
            text = stringResource(R.string.sign_in),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun RegisterButton(onClick: () -> Unit) {
    MoodFlowButton(
        text = stringResource(R.string.sign_up),
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
