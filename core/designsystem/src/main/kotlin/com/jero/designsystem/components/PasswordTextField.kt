package com.jero.designsystem.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PasswordTextField(
    password: String,
    isPasswordVisible: Boolean = false,
    onPasswordChanged: (String) -> Unit,
    onChangePasswordVisibility: (Boolean) -> Unit,
) {
    TextField(
        modifier = Modifier.padding(16.dp),
        value = password,
        onValueChange = { newPassword ->
            val filteredPassword = newPassword.filter { !it.isWhitespace() }
            if (filteredPassword != password) {
                onPasswordChanged(filteredPassword)
                if (filteredPassword.isBlank()) {
                    onChangePasswordVisibility(false)
                }
            }
        },
        label = { Text("Password") },
        visualTransformation = if (!isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            AnimatedVisibility(
                visible = password.isNotBlank(),
                enter = fadeIn(animationSpec = tween(200)) + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(200)
                ),
                exit = fadeOut(animationSpec = tween(200)) + scaleOut(
                    targetScale = 0.8f,
                    animationSpec = tween(200)
                )
            ) {
                AnimatedContent(
                    targetState = isPasswordVisible,
                    transitionSpec = {
                        slideInHorizontally(animationSpec = tween(200)) { -it / 2 } + fadeIn() togetherWith
                                slideOutHorizontally(animationSpec = tween(200)) { it / 2 } + fadeOut()
                    },
                    label = "password_visibility_toggle"
                ) { isVisible ->
                    Icon(
                        modifier = Modifier.clickable {
                            onChangePasswordVisibility(!isVisible)
                        },
                        imageVector = if (isVisible) Icons.Filled.HideSource else Icons.Filled.Visibility,
                        contentDescription = "Toggle password visibility"
                    )
                }
            }
        }
    )
}