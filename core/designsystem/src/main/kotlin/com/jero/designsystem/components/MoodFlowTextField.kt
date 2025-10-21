package com.jero.designsystem.components

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jero.designsystem.theme.MoodFlowColors

@Composable
fun MoodFlowTextField(
    modifier: Modifier = Modifier,
    text: String,
    placeHolder: String,
    height: Dp = 56.dp,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester = FocusRequester(),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    onTextChange: (String) -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is FocusInteraction.Focus -> isFocused = true
                is FocusInteraction.Unfocus -> isFocused = false
            }
        }
    }

    val currentBorderColor = when {
        isError -> Color.Red
        isFocused -> Color.Blue
        else -> Color.Gray
    }

    val currentLabelColor = when {
        isError -> Color.Red
        isFocused -> Color.Blue
        else -> Color.Gray
    }

    OutlinedTextField(
        modifier = modifier
            .height(height)
            .focusRequester(focusRequester)
            .fillMaxWidth(),
        value = text,
        onValueChange = onTextChange,
        label = { Text(placeHolder, color = currentLabelColor) },
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,

            focusedLabelColor = currentLabelColor,
            unfocusedLabelColor = currentLabelColor,

            focusedTextColor = currentLabelColor,
            unfocusedTextColor = currentLabelColor,
            errorTextColor = currentLabelColor,
            disabledTextColor = currentLabelColor,

            focusedBorderColor = currentBorderColor,
            unfocusedBorderColor = Color.White,
            disabledBorderColor = currentBorderColor,
            errorBorderColor = currentBorderColor,

            cursorColor = MoodFlowColors.defaultLightColors().pastelBlue,
            errorCursorColor = MoodFlowColors.defaultLightColors().pastelBlue,
        ),
        interactionSource = interactionSource,
        singleLine = true,
    )
}
