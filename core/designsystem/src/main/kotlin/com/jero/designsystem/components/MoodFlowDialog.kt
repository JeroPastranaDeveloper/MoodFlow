package com.jero.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.jero.core.model.Note
import com.jero.designsystem.theme.MoodFlowColors

@Composable
fun MoodFlowDialog(
    note: Note,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onPinChanged: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Dialog(
        onDismissRequest = { onCancel() },
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MoodFlowColors.defaultLightColors().backGroundColor
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                MoodFlowTextField(
                    text = note.title,
                    placeHolder = "Título",
                    placeHolderFontSize = 20.sp,
                ) {
                    onTitleChanged(it)
                }

                Spacer(modifier = Modifier.height(8.dp))

                MoodFlowTextField(
                    text = note.content,
                    placeHolder = "Descripción",
                    placeHolderFontSize = 20.sp,
                ) {
                    onDescriptionChanged(it)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pin note")

                    Checkbox(checked = note.pinned, onCheckedChange = {
                        onPinChanged(it)
                    })
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MoodFlowButton(
                        modifier = Modifier.weight(1f),
                        text = "Cancel",
                        addBorder = true,
                        horizontalPadding = 0.dp,
                    ) {
                        onCancel()
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    MoodFlowButton(
                        modifier = Modifier.weight(1f),
                        text = if (note.id.isBlank()) "Create" else "Edit",
                        addBorder = true,
                        horizontalPadding = 0.dp,
                    ) {
                        onConfirm()
                    }
                }
            }
        }
    }
}