package com.jero.editnote

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jero.core.designsystem.R
import com.jero.core.screen.HandleActions
import com.jero.core.screen.getTopSystemPadding
import com.jero.designsystem.components.MoodFlowTransparentTextField
import com.jero.designsystem.components.MoodFlowTwoOptionsDialog
import com.jero.designsystem.components.moodFlowSharedElement
import com.jero.designsystem.theme.NoteColors
import com.jero.editnote.EditNoteViewContract.UiAction
import com.jero.editnote.EditNoteViewContract.UiIntent
import com.jero.editnote.EditNoteViewContract.UiState
import com.jero.navigation.utils.boundsTransform
import org.koin.androidx.compose.koinViewModel

@Composable
fun SharedTransitionScope.MoodFlowEditNote(
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: EditNoteViewModel = koinViewModel(),
    noteId: String,
    onGoBack: () -> Unit,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(noteId) {
        viewModel.sendIntent(UiIntent.OnFetchNoteDetails(noteId))
    }

    BackHandler { viewModel.sendIntent(UiIntent.OnGoBack) }

    HandleActions(viewModel.actions) { action ->
        when (action) {
            UiAction.GoBack -> {
                focusManager.clearFocus(force = true)
                onGoBack()
            }

            is UiAction.ShowToast -> Toast.makeText(context, action.message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    Content(
        state = state,
        animatedVisibilityScope = animatedVisibilityScope,
        onPinChanged = { viewModel.sendIntent(UiIntent.OnPinChanged) },
        onChangeDeleteDialogVisibility = { viewModel.sendIntent(UiIntent.OnChangeDeleteDialogVisibility) },
        onDeleteNote = { viewModel.sendIntent(UiIntent.OnDeleteNote) },
        onTitleChanged = { viewModel.sendIntent(UiIntent.OnTitleChanged(it)) },
        onDescriptionChanged = { viewModel.sendIntent(UiIntent.OnDescriptionChanged(it)) },
        onColorChanged = { viewModel.sendIntent(UiIntent.OnColorChanged(it)) },
        onGoBack = { viewModel.sendIntent(UiIntent.OnGoBack) },
    )
}

@Composable
private fun SharedTransitionScope.Content(
    state: UiState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onPinChanged: () -> Unit,
    onChangeDeleteDialogVisibility: () -> Unit,
    onDeleteNote: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onColorChanged: (Long) -> Unit,
    onGoBack: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val titleFocusRequester = remember { FocusRequester() }
    val contentFocusRequester = remember { FocusRequester() }

    val backgroundColor by animateColorAsState(
        targetValue = NoteColors.toComposeColor(state.editedNote.color),
        animationSpec = tween(durationMillis = 400),
        label = "backgroundColor",
    )

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(start = 16.dp, top = getTopSystemPadding(true), end = 16.dp)
            ) {
                Icon(
                    modifier = Modifier.clickable { onGoBack() },
                    painter = rememberVectorPainter(image = Icons.AutoMirrored.Filled.ArrowBack),
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.weight(1f))

                Crossfade(targetState = state.editedNote.pinned, label = "Icon change") { pinned ->
                    Icon(
                        modifier = Modifier
                            .size(22.dp)
                            .offset(y = 2.dp)
                            .clickable { onPinChanged() },
                        painter = painterResource(id = if (pinned) R.drawable.ic_pinned else R.drawable.ic_not_pinned),
                        contentDescription = null
                    )
                }

                if (state.editedNote.id.isNotBlank()) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        modifier = Modifier.clickable {
                            focusManager.clearFocus(force = true)
                            onChangeDeleteDialogVisibility()
                        },
                        painter = painterResource(id = R.drawable.ic_trash),
                        contentDescription = null,
                    )
                }
            }
        },
        bottomBar = {
            NoteColorPicker(
                selectedColor = state.editedNote.color,
                backgroundColor = backgroundColor,
                onColorSelected = onColorChanged,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            MoodFlowTransparentTextField(
                modifier = Modifier.moodFlowSharedElement(
                    isLocalInspectionMode = LocalInspectionMode.current,
                    state = rememberSharedContentState(key = "title-${state.originalNote.id}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = boundsTransform,
                ),
                text = state.editedNote.title,
                textFontSize = 22.sp,
                textFontWeight = FontWeight.Bold,
                placeholder = stringResource(R.string.title),
                placeholderFontSize = 20.sp,
                focusRequester = titleFocusRequester,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Unspecified,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { contentFocusRequester.requestFocus() }
                ),
            ) { onTitleChanged(it) }

            Spacer(modifier = Modifier.height(4.dp))

            MoodFlowTransparentTextField(
                modifier = Modifier
                    .fillMaxSize()
                    .moodFlowSharedElement(
                        isLocalInspectionMode = LocalInspectionMode.current,
                        state = rememberSharedContentState(key = "content-${state.originalNote.id}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = boundsTransform,
                    ),
                text = state.editedNote.content,
                textFontSize = 16.sp,
                placeholder = stringResource(R.string.note),
                placeholderFontSize = 16.sp,
                focusRequester = contentFocusRequester,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Unspecified,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus(force = true) }
                ),
            ) { onDescriptionChanged(it) }
        }

        if (state.showDeleteNoteDialog) {
            MoodFlowTwoOptionsDialog(
                titleText = stringResource(R.string.delete_selection),
                bodyText = stringResource(R.string.delete_selection_question),
                onAccept = onDeleteNote,
                onCancel = onChangeDeleteDialogVisibility,
            )
        }
    }
}

@Composable
private fun NoteColorPicker(
    selectedColor: Long,
    backgroundColor: Color,
    onColorSelected: (Long) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(NoteColors.palette) { colorLong ->
            val isSelected = colorLong == selectedColor
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(NoteColors.toComposeColor(colorLong), CircleShape)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Color.DarkGray else Color.LightGray,
                        shape = CircleShape,
                    )
                    .clickable { onColorSelected(colorLong) },
                contentAlignment = Alignment.Center,
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.DarkGray,
                    )
                }
            }
        }
    }
}
