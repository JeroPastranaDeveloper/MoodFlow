package com.jero.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jero.core.model.Note

@Composable
fun MoodFlowNote(
    modifier: Modifier = Modifier,
    note: Note,
    onClick: (String) -> Unit,
) {
    Card(
        modifier = modifier.clickable {
            onClick(note.id)
        },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = note.title, fontWeight = FontWeight.Bold)
            if (note.content.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = note.content, color = Color.Gray)
            }
        }
    }
}
