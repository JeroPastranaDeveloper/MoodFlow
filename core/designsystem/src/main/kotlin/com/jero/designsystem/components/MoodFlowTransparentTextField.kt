package com.jero.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit

@Composable
fun MoodFlowTransparentTextField(
    modifier: Modifier = Modifier,
    text: String,
    textFontSize: TextUnit = TextUnit.Unspecified,
    textFontWeight: FontWeight = FontWeight.Normal,
    placeholder: String,
    placeholderFontSize: TextUnit = TextUnit.Unspecified,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester = FocusRequester(),
    onTextChange: (String) -> Unit,
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = text,
        onValueChange = onTextChange,
        textStyle = TextStyle(
            fontSize = textFontSize,
            fontWeight = textFontWeight,
        ),
        placeholder = {
            Text(
                text = placeholder,
                color = Color.Gray,
                fontSize = placeholderFontSize,
                fontWeight = FontWeight.Normal
            )
        },
        keyboardOptions = keyboardOptions.copy(
            capitalization = KeyboardCapitalization.Sentences
        ),
        keyboardActions = keyboardActions,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color.Gray
        )
    )
}

@Composable
fun MoodFlowTransparentTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    textFontSize: TextUnit = TextUnit.Unspecified,
    textFontWeight: FontWeight = FontWeight.Normal,
    placeholder: String,
    placeholderFontSize: TextUnit = TextUnit.Unspecified,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester = FocusRequester(),
    onValueChange: (TextFieldValue) -> Unit,
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            fontSize = textFontSize,
            fontWeight = textFontWeight,
        ),
        placeholder = {
            Text(
                text = placeholder,
                color = Color.Gray,
                fontSize = placeholderFontSize,
                fontWeight = FontWeight.Normal
            )
        },
        keyboardOptions = keyboardOptions.copy(
            capitalization = KeyboardCapitalization.Sentences
        ),
        keyboardActions = keyboardActions,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color.Gray
        )
    )
}
