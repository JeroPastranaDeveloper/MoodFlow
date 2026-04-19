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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jero.core.designsystem.R
import com.jero.core.model.Tag
import com.jero.core.model.UNTAGGED_TAG_ID
import com.jero.core.screen.HandleActions
import com.jero.core.screen.getTopSystemPadding
import com.jero.designsystem.components.MoodFlowTransparentTextField
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
        onDeleteNote = { viewModel.sendIntent(UiIntent.OnDeleteNote) },
        onTitleChanged = { viewModel.sendIntent(UiIntent.OnTitleChanged(it)) },
        onDescriptionChanged = { viewModel.sendIntent(UiIntent.OnDescriptionChanged(it)) },
        onColorChanged = { viewModel.sendIntent(UiIntent.OnColorChanged(it)) },
        onToggleTagSelector = { viewModel.sendIntent(UiIntent.OnToggleTagSelector) },
        onToggleTag = { viewModel.sendIntent(UiIntent.OnToggleTag(it)) },
        onGoBack = { viewModel.sendIntent(UiIntent.OnGoBack) },
    )
}

@Composable
private fun SharedTransitionScope.Content(
    state: UiState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onPinChanged: () -> Unit,
    onDeleteNote: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onColorChanged: (Long) -> Unit,
    onToggleTagSelector: () -> Unit,
    onToggleTag: (String) -> Unit,
    onGoBack: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val titleFocusRequester = remember { FocusRequester() }
    val contentFocusRequester = remember { FocusRequester() }
    var contentFieldValue by remember { mutableStateOf(TextFieldValue(state.editedNote.content)) }

    // Sincronizar cuando se carga una nota distinta (nuevo id)
    LaunchedEffect(state.editedNote.id) {
        contentFieldValue = TextFieldValue(
            text = state.editedNote.content,
            selection = TextRange(state.editedNote.content.length),
        )
    }

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
                            onDeleteNote()
                        },
                        painter = painterResource(id = R.drawable.ic_trash),
                        contentDescription = null,
                    )
                }
            }
        },
        bottomBar = {
            Column {
                TagsRow(
                    assignedTagIds = state.editedNote.tagIds,
                    allTags = state.allTags,
                    backgroundColor = backgroundColor,
                    showSelector = state.showTagSelector,
                    onToggleSelector = onToggleTagSelector,
                    onToggleTag = onToggleTag,
                )
                NoteColorPicker(
                    selectedColor = state.editedNote.color,
                    backgroundColor = backgroundColor,
                    onColorSelected = onColorChanged,
                )
            }
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
                value = contentFieldValue,
                textFontSize = 16.sp,
                placeholder = stringResource(R.string.note),
                placeholderFontSize = 16.sp,
                focusRequester = contentFocusRequester,
            ) { newValue ->
                val result = applyListContinuation(newValue, contentFieldValue)
                contentFieldValue = result
                onDescriptionChanged(result.text)
            }
        }
    }
}

@Composable
private fun TagsRow(
    assignedTagIds: List<String>,
    allTags: List<Tag>,
    backgroundColor: Color,
    showSelector: Boolean,
    onToggleSelector: () -> Unit,
    onToggleTag: (String) -> Unit,
) {
    val assignedTags = allTags.filter { it.id in assignedTagIds && it.id != UNTAGGED_TAG_ID }
    val availableTags = allTags.filter { it.id != UNTAGGED_TAG_ID }
    val listState = rememberLazyListState()

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(assignedTags, key = { it.id }) { tag ->
            TagChip(
                modifier = Modifier.animateItem(),
                label = tag.name,
                onRemove = { onToggleTag(tag.id) },
            )
        }

        item {
            Box(modifier = Modifier.animateItem()) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color.Gray.copy(alpha = 0.2f), CircleShape)
                        .clickable { onToggleSelector() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.DarkGray,
                    )
                }

                DropdownMenu(
                    expanded = showSelector,
                    onDismissRequest = onToggleSelector,
                ) {
                    if (availableTags.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.no_tags_available)) },
                            onClick = {},
                            enabled = false,
                        )
                    } else {
                        availableTags.forEach { tag ->
                            val isAssigned = tag.id in assignedTagIds
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        tag.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                onClick = { onToggleTag(tag.id) },
                                trailingIcon = {
                                    if (isAssigned) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = Color.DarkGray,
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TagChip(
    modifier: Modifier = Modifier,
    label: String,
    onRemove: () -> Unit,
) {
    Row(
        modifier = modifier
            .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray,
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            modifier = Modifier
                .size(14.dp)
                .clickable { onRemove() },
            tint = Color.DarkGray,
        )
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

private fun applyListContinuation(newValue: TextFieldValue, oldValue: TextFieldValue): TextFieldValue {
    val newText = newValue.text
    val oldText = oldValue.text
    if (newText.length != oldText.length + 1) return newValue

    val insertPos = newText.indices.firstOrNull { i -> i >= oldText.length || newText[i] != oldText[i] }
        ?: return newValue
    if (newText[insertPos] != '\n') return newValue

    val lineStart = newText.lastIndexOf('\n', insertPos - 1) + 1
    val currentLine = newText.substring(lineStart, insertPos)

    return when {
        currentLine == "- " -> {
            val result = newText.substring(0, lineStart) + newText.substring(insertPos + 1)
            TextFieldValue(text = result, selection = TextRange(lineStart))
        }
        currentLine.startsWith("- ") -> {
            val result = newText.substring(0, insertPos + 1) + "- " + newText.substring(insertPos + 1)
            TextFieldValue(text = result, selection = TextRange(insertPos + 3))
        }
        else -> newValue
    }
}
