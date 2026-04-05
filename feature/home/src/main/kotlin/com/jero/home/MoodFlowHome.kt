package com.jero.home

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jero.core.designsystem.R
import com.jero.core.model.Note
import com.jero.core.screen.HandleActions
import com.jero.core.screen.SetStatusBarIconsColor
import com.jero.core.screen.getTopSystemPadding
import com.jero.core.utils.emptyString
import com.jero.designsystem.components.MoodFlowNote
import com.jero.designsystem.components.MoodFlowTextField
import com.jero.designsystem.theme.MoodFlowColors
import com.jero.designsystem.utils.rememberKeyboardAsState
import com.jero.home.HomeViewContract.UiAction
import com.jero.home.HomeViewContract.UiIntent
import com.jero.home.HomeViewContract.UiState
import com.jero.navigation.utils.boundsTransform
import org.koin.androidx.compose.koinViewModel

@Composable
fun SharedTransitionScope.MoodFlowHome(
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = koinViewModel(),
    onGoLogin: () -> Unit,
    onGoEditNote: (String) -> Unit,
    onGoSettings: () -> Unit,
    onGoTrash: () -> Unit,
) {
    SetStatusBarIconsColor()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val isKeyboardOpen by rememberKeyboardAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isKeyboardOpen) {
        if (!isKeyboardOpen) focusManager.clearFocus(force = true)
    }

    HandleActions(viewModel.actions) { action ->
        when (action) {
            UiAction.GoLogin -> onGoLogin()
            UiAction.GoSettingsScreen -> onGoSettings()
            UiAction.GoTrashScreen -> onGoTrash()
            is UiAction.ShowToast -> Toast.makeText(context, action.message, Toast.LENGTH_SHORT).show()
            is UiAction.GoEditNoteScreen -> onGoEditNote(action.noteId)
        }
    }

    BackHandler {
        when {
            state.showMoreMenu -> viewModel.sendIntent(UiIntent.OnChangeMoreMenuVisibility)
            isKeyboardOpen -> focusManager.clearFocus(force = true)
            state.query.isNotBlank() -> {
                viewModel.sendIntent(UiIntent.OnSearchQueryChanged(emptyString()))
                focusManager.clearFocus(force = true)
            }
            state.notesCanBeSelected -> viewModel.sendIntent(UiIntent.OnChangeMultipleSelectorUIVisibility)
            else -> (context as? Activity)?.finish()
        }
    }

    Content(
        state = state,
        animatedVisibilityScope = animatedVisibilityScope,
        isKeyboardOpen = isKeyboardOpen,
        onSearchQueryChanged = { viewModel.sendIntent(UiIntent.OnSearchQueryChanged(it)) },
        onSearchFilterChanged = { viewModel.sendIntent(UiIntent.OnSearchFilterChanged(it)) },
        onChangeMoreMenuVisibility = { viewModel.sendIntent(UiIntent.OnChangeMoreMenuVisibility) },
        onPinOrUnpinSelectedNotes = { viewModel.sendIntent(UiIntent.OnPinOrUnpinSelectedNotes) },
        onChangeMultipleSelectorUIVisibility = { viewModel.sendIntent(UiIntent.OnChangeMultipleSelectorUIVisibility) },
        onGoEditNoteScreen = { viewModel.sendIntent(UiIntent.OnGoEditNoteScreen(it)) },
        onDeleteMultipleNotes = { viewModel.sendIntent(UiIntent.OnDeleteMultipleNotes) },
        onGoSettingsScreen = { viewModel.sendIntent(UiIntent.OnGoSettingsScreen) },
        onGoTrashScreen = { viewModel.sendIntent(UiIntent.OnGoTrashScreen) },
        onSelectNote = { noteId, isChecked -> viewModel.sendIntent(UiIntent.OnSelectNote(noteId, isChecked)) },
    )
}

@Composable
private fun SharedTransitionScope.Content(
    state: UiState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    isKeyboardOpen: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onSearchFilterChanged: (SearchFilter) -> Unit,
    onChangeMoreMenuVisibility: () -> Unit,
    onPinOrUnpinSelectedNotes: () -> Unit,
    onChangeMultipleSelectorUIVisibility: () -> Unit,
    onGoEditNoteScreen: (String?) -> Unit,
    onDeleteMultipleNotes: () -> Unit,
    onGoSettingsScreen: () -> Unit,
    onGoTrashScreen: () -> Unit,
    onSelectNote: (String, Boolean) -> Unit,
) {
    val localInspectionMode = LocalInspectionMode.current
    val gridState = rememberLazyStaggeredGridState()
    val query = remember(state.query) { state.query }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                AnimatedContent(
                    targetState = state.notesCanBeSelected,
                    transitionSpec = {
                        fadeIn(tween(200)) + slideInVertically(initialOffsetY = { it / 2 }) togetherWith
                                fadeOut(tween(200)) + slideOutVertically(targetOffsetY = { it / 2 })
                    },
                    label = "TopBarTransition"
                ) { canSelectNotes ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                top = getTopSystemPadding(),
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!canSelectNotes) {
                            SearchBar(
                                isKeyboardOpen = isKeyboardOpen,
                                query = query,
                                searchFilter = state.searchFilter,
                                onQueryChanged = onSearchQueryChanged,
                                onFilterChanged = onSearchFilterChanged,
                                onChangeMoreMenuVisibility = onChangeMoreMenuVisibility,
                            )
                        } else {
                            MultipleUiSelector(
                                selectedNotes = state.selectedNotes,
                                allNotes = state.allNotes,
                                onPinOrUnpinSelectedNotes = onPinOrUnpinSelectedNotes,
                                onChangeMultipleSelectorUIVisibility = onChangeMultipleSelectorUIVisibility,
                                onDeleteMultipleNotes = onDeleteMultipleNotes,
                            )
                        }
                    }
                }
            },
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    state = gridState,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { renderEffect = null }
                        .background(MoodFlowColors.defaultLightColors().backGroundColor)
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalItemSpacing = 8.dp,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (query.isNotBlank()) {
                        items(items = state.filteredNotes, key = { it.id }) { note ->
                            NoteItem(
                                modifier = Modifier.animateItem(),
                                note = note,
                                state = state,
                                localInspectionMode = localInspectionMode,
                                animatedVisibilityScope = animatedVisibilityScope,
                                onGoEditNoteScreen = onGoEditNoteScreen,
                                onChangeMultipleSelectorUIVisibility = onChangeMultipleSelectorUIVisibility,
                                onSelectNote = onSelectNote,
                            )
                        }
                    } else {
                        if (state.pinnedNotes.isNotEmpty()) {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                Text(stringResource(R.string.pinned_notes))
                            }

                            items(items = state.pinnedNotes, key = { it.id }) { note ->
                                NoteItem(
                                    modifier = Modifier.animateItem(),
                                    note = note,
                                    state = state,
                                    localInspectionMode = localInspectionMode,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    onGoEditNoteScreen = onGoEditNoteScreen,
                                    onChangeMultipleSelectorUIVisibility = onChangeMultipleSelectorUIVisibility,
                                    onSelectNote = onSelectNote,
                                )
                            }

                            item(span = StaggeredGridItemSpan.FullLine) {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        if (state.notes.isNotEmpty()) {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                Text(stringResource(if (state.pinnedNotes.isEmpty()) R.string.notes else R.string.other_notes))
                            }

                            items(items = state.notes, key = { it.id }) { note ->
                                NoteItem(
                                    modifier = Modifier.animateItem(),
                                    note = note,
                                    state = state,
                                    localInspectionMode = localInspectionMode,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    onGoEditNoteScreen = onGoEditNoteScreen,
                                    onChangeMultipleSelectorUIVisibility = onChangeMultipleSelectorUIVisibility,
                                    onSelectNote = onSelectNote,
                                )
                            }
                        }

                        item(span = StaggeredGridItemSpan.FullLine) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                FloatingActionButton(notesCanBeSelected = !state.notesCanBeSelected) {
                    onGoEditNoteScreen(null)
                }

            }
        }

        AnimatedVisibility(
            visible = state.showMoreMenu,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onChangeMoreMenuVisibility,
                    )
            )
        }

        AnimatedVisibility(
            visible = state.showMoreMenu,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(durationMillis = 300)
            ) + fadeIn(animationSpec = tween(300)),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(durationMillis = 300)
            ) + fadeOut(animationSpec = tween(300))
        ) {
            Column(
                modifier = Modifier
                    .width(250.dp)
                    .fillMaxHeight()
                    .background(Color.White)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    )
                    .padding(vertical = 16.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onGoTrashScreen() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_trash),
                            contentDescription = "Go trash",
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(stringResource(R.string.trash))
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 16.dp)
                        .clickable { onGoSettingsScreen() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Go settings",
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(stringResource(R.string.settings))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SharedTransitionScope.NoteItem(
    modifier: Modifier = Modifier,
    note: Note,
    state: UiState,
    localInspectionMode: Boolean,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onGoEditNoteScreen: (String?) -> Unit,
    onChangeMultipleSelectorUIVisibility: () -> Unit,
    onSelectNote: (String, Boolean) -> Unit,
) {
    val isChecked = state.selectedNotes.contains(note.id)
    MoodFlowNote(
        modifier = modifier,
        note = note,
        titleState = rememberSharedContentState(key = "title-${note.id}"),
        contentState = rememberSharedContentState(key = "content-${note.id}"),
        localInspectionMode = localInspectionMode,
        animatedVisibilityScope = animatedVisibilityScope,
        boundsTransform = boundsTransform,
        canBeSelected = state.notesCanBeSelected,
        isSelected = isChecked,
        onClick = { noteId -> onGoEditNoteScreen(noteId) },
        onCheck = { noteId, checked ->
            if (!state.notesCanBeSelected) onChangeMultipleSelectorUIVisibility()
            onSelectNote(noteId, checked)
        },
    )
}

@Composable
private fun BoxScope.FloatingActionButton(
    notesCanBeSelected: Boolean,
    onGoEditNoteScreen: () -> Unit,
) {
    AnimatedVisibility(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 16.dp, bottom = 32.dp),
        visible = notesCanBeSelected,
        enter = fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ),
        exit = fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing)) + scaleOut(
            targetScale = 0.8f,
            animationSpec = tween(200, easing = FastOutSlowInEasing)
        )
    ) {
        FloatingActionButton(onClick = onGoEditNoteScreen) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }
}

@Composable
private fun RowScope.MultipleUiSelector(
    selectedNotes: List<String>,
    allNotes: List<Note>,
    onPinOrUnpinSelectedNotes: () -> Unit = {},
    onChangeMultipleSelectorUIVisibility: () -> Unit = {},
    onDeleteMultipleNotes: () -> Unit = {},
) {
    IconButton(onClick = onChangeMultipleSelectorUIVisibility) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Hide multiple selector"
        )
    }

    Spacer(modifier = Modifier.weight(1f))

    val allNotesArePinned = selectedNotes
        .mapNotNull { id -> allNotes.find { it.id == id } }
        .all { it.pinned }

    val allNotesAreUnpinned = selectedNotes
        .mapNotNull { id -> allNotes.find { it.id == id } }
        .all { !it.pinned }

    AnimatedVisibility(
        visible = allNotesArePinned || allNotesAreUnpinned,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = 200,
                easing = FastOutSlowInEasing
            )
        ) + scaleOut(
            targetScale = 0.8f,
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
        )
    ) {
        Icon(
            modifier = Modifier
                .size(22.dp)
                .offset(y = 2.dp)
                .clickable { onPinOrUnpinSelectedNotes() },
            painter = painterResource(id = if (allNotesArePinned) R.drawable.ic_pinned else R.drawable.ic_not_pinned),
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(16.dp))
    }

    IconButton(onClick = onDeleteMultipleNotes) {
        Icon(
            painter = painterResource(id = R.drawable.ic_trash),
            contentDescription = "Delete multiple notes"
        )
    }
}

@Composable
private fun RowScope.SearchBar(
    isKeyboardOpen: Boolean,
    query: String,
    searchFilter: SearchFilter,
    onQueryChanged: (String) -> Unit = {},
    onFilterChanged: (SearchFilter) -> Unit = {},
    onChangeMoreMenuVisibility: () -> Unit,
) {
    val showFilters = isKeyboardOpen || query.isNotBlank()

    Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedVisibility(visible = !isKeyboardOpen) {
                IconButton(onClick = onChangeMoreMenuVisibility) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(id = R.drawable.ic_more_menu),
                        contentDescription = "More menu"
                    )
                }
            }

            AnimatedVisibility(visible = !isKeyboardOpen) {
                Spacer(modifier = Modifier.width(16.dp))
            }

            MoodFlowTextField(
                modifier = Modifier.weight(1f),
                text = query,
                placeHolder = stringResource(R.string.search_placeholder),
                placeHolderFontSize = 20.sp,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = query.isNotBlank(),
                        enter = fadeIn(tween(100)) + scaleIn(initialScale = 0.8f, animationSpec = tween(100)),
                        exit = fadeOut(tween(100)) + scaleOut(targetScale = 0.8f, animationSpec = tween(100))
                    ) {
                        IconButton(onClick = { onQueryChanged(emptyString()) }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
            ) { newQuery -> onQueryChanged(newQuery) }
        }

        AnimatedVisibility(
            visible = showFilters,
            enter = fadeIn(tween(200)) + slideInVertically(initialOffsetY = { -it / 2 }),
            exit = fadeOut(tween(150)) + slideOutVertically(targetOffsetY = { -it / 2 }),
        ) {
            SearchFilterChips(
                filter = searchFilter,
                onFilterChanged = onFilterChanged,
            )
        }
    }
}

@Composable
private fun SearchFilterChips(
    filter: SearchFilter,
    onFilterChanged: (SearchFilter) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = filter.onlyPinned,
            onClick = { onFilterChanged(filter.copy(onlyPinned = !filter.onlyPinned)) },
            label = { Text(stringResource(R.string.only_pinned)) },
        )
        FilterChip(
            selected = filter.sortOrder == SortOrder.DATE_ASC,
            onClick = {
                val newOrder = if (filter.sortOrder == SortOrder.DATE_ASC) SortOrder.DATE_DESC else SortOrder.DATE_ASC
                onFilterChanged(filter.copy(sortOrder = newOrder))
            },
            label = {
                Text(stringResource(if (filter.sortOrder == SortOrder.DATE_ASC) R.string.sort_oldest else R.string.sort_newest))
            },
        )
        FilterChip(
            selected = filter.sortOrder == SortOrder.TITLE_ASC,
            onClick = {
                val newOrder = if (filter.sortOrder == SortOrder.TITLE_ASC) SortOrder.DATE_DESC else SortOrder.TITLE_ASC
                onFilterChanged(filter.copy(sortOrder = newOrder))
            },
            label = { Text(stringResource(R.string.sort_a_z)) },
        )
    }
}
