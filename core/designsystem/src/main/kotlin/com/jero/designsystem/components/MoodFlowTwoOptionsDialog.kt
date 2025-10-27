package com.jero.designsystem.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jero.core.designsystem.R

@Composable
fun MoodFlowTwoOptionsDialog(
    titleText: String,
    bodyText: String,
    onAccept: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        title = { Text(text = titleText)},
        text = { Text(text = bodyText)},
        onDismissRequest = { onCancel() },
        confirmButton = {
            TextButton(
                onClick = {
                    onAccept()
                }
            ) {
                Text(text = stringResource(R.string.accept_button))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onCancel()
                }
            ) {
                Text(text = stringResource(R.string.cancel_button))
            }
        }
    )
}
