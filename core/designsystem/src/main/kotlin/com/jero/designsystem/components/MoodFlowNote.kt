package com.jero.designsystem.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.SharedContentState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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

context(SharedTransitionScope)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MoodFlowNote(
    modifier: Modifier = Modifier,
    note: Note,
    titleState: SharedContentState,
    contentState: SharedContentState,
    localInspectionMode: Boolean,
    animatedVisibilityScope: AnimatedVisibilityScope,
    boundsTransform: BoundsTransform,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
) {
    Card(
        modifier = modifier
            .combinedClickable(
                onClick = { onClick(note.id) },
                onLongClick = { onLongClick(note.id) }
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                modifier = Modifier.moodFlowSharedElement(
                    isLocalInspectionMode = localInspectionMode,
                    state = titleState,
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = boundsTransform,
                ),
                text = note.title,
                fontWeight = FontWeight.Bold
            )
            if (note.content.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    modifier = Modifier.moodFlowSharedElement(
                        isLocalInspectionMode = localInspectionMode,
                        state = contentState,
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = boundsTransform,
                    ),
                    text = note.content,
                    color = Color.Gray
                )
            }
        }
    }
}
