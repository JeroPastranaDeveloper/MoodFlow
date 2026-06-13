package com.jero.login

import android.content.Context
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
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
import com.jero.login.LoginViewContract.UiState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MoodFlowLogin(
    viewModel: LoginViewModel = koinViewModel(),
    onGoHome: () -> Unit,
    onGoRegister: () -> Unit,
) {
    SetStatusBarIconsColor()
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    HandleActions(viewModel.actions) { action ->
        when (action) {
            UiAction.GoHome -> onGoHome()
            UiAction.GoRegister -> onGoRegister()
            UiAction.LaunchGoogleSignIn -> coroutineScope.launch {
                try {
                    val idToken = getGoogleIdToken(context) ?: return@launch
                    viewModel.sendIntent(UiIntent.OnGoogleIdTokenReceived(idToken))
                } catch (_: GetCredentialCancellationException) {
                    // user dismissed the picker
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
            is UiAction.ShowToast -> Toast.makeText(context, action.message, Toast.LENGTH_SHORT).show()
        }
    }

    Content(
        state = state,
        onEmailChanged = { viewModel.sendIntent(UiIntent.OnEmailChanged(it)) },
        onPasswordChanged = { viewModel.sendIntent(UiIntent.OnPasswordChanged(it)) },
        onChangePasswordVisibility = { viewModel.sendIntent(UiIntent.OnChangePasswordVisibility(it)) },
        onEmailLoginClicked = { viewModel.sendIntent(UiIntent.OnEmailLoginClicked) },
        onLoginWithGoogleClicked = { viewModel.sendIntent(UiIntent.OnLoginWithGoogleClicked) },
        onSignUpClicked = { viewModel.sendIntent(UiIntent.OnSignUpClicked) },
    )
}

@Composable
private fun Content(
    state: UiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onChangePasswordVisibility: (Boolean) -> Unit,
    onEmailLoginClicked: () -> Unit,
    onLoginWithGoogleClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val isKeyboardOpen by rememberKeyboardAsState()
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isKeyboardOpen) {
        if (!isKeyboardOpen) focusManager.clearFocus(force = true)
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
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus(force = true) }
            ),
            onPasswordChanged = { onPasswordChanged(it) },
            onChangePasswordVisibility = { onChangePasswordVisibility(it) },
            isError = state.passwordError != null,
            errorMessage = state.passwordError,
        )

        Spacer(modifier = Modifier.height(16.dp))

        LoginButton {
            focusManager.clearFocus(force = true)
            onEmailLoginClicked()
        }

        Spacer(modifier = Modifier.height(16.dp))

        LoginWithGoogleButton {
            focusManager.clearFocus(force = true)
            onLoginWithGoogleClicked()
        }

        Spacer(modifier = Modifier.height(16.dp))

        NotAccountText {
            focusManager.clearFocus(force = true)
            onSignUpClicked()
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
            modifier = Modifier.clickable { onClick() },
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

private suspend fun getGoogleIdToken(context: Context): String? {
    val webClientId = context.getString(R.string.default_web_client_id)
    if (webClientId.isBlank()) return null

    val credentialManager = CredentialManager.create(context)
    val googleSignInOption = GetSignInWithGoogleOption.Builder(webClientId).build()
    val request = GetCredentialRequest(listOf(googleSignInOption))
    val result = credentialManager.getCredential(context, request)
    val credential = result.credential
    return if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        GoogleIdTokenCredential.createFrom(credential.data).idToken
    } else null
}
