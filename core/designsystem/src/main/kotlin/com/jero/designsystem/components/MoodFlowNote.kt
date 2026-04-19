package com.jero.designsystem.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.SharedContentState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jero.core.model.Note
import com.jero.core.utils.htmlToPlainText
import com.jero.designsystem.theme.MoodFlowColors
import com.jero.designsystem.theme.NoteColors

context(scope: SharedTransitionScope)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MoodFlowNote(
    modifier: Modifier = Modifier,
    note: Note,
    canBeSelected: Boolean = false,
    isSelected: Boolean = false,
    titleState: SharedContentState,
    contentState: SharedContentState,
    localInspectionMode: Boolean,
    animatedVisibilityScope: AnimatedVisibilityScope,
    boundsTransform: BoundsTransform,
    onClick: (String) -> Unit,
    onCheck: (String, Boolean) -> Unit,
) {
    val noteTitle = remember(note.id) { note.title }
    val noteContent = remember(note.content) { note.content.htmlToPlainText() }

    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 1.dp,
        animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MoodFlowColors.defaultLightColors().pastelBlue else Color.LightGray,
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
    )

    Card(
        modifier = modifier
            .combinedClickable(
                onClick = {
                    if (!canBeSelected) onClick(note.id) else onCheck(note.id, !isSelected)
                },
                onLongClick = { if (!canBeSelected) onCheck(note.id, !isSelected) }
            ),
        colors = CardDefaults.cardColors(containerColor = NoteColors.toComposeColor(note.color)),
        border = BorderStroke(borderWidth, borderColor),
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
                text = noteTitle,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
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
                    text = noteContent,
                    color = Color.Gray,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
