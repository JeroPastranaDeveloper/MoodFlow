package com.jero.home

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.jero.core.screen.HandleActions
import com.jero.core.screen.SetStatusBarIconsColor
import com.jero.core.screen.getTopSystemPadding
import com.jero.core.utils.emptyString
import com.jero.designsystem.components.MoodFlowNote
import com.jero.designsystem.components.MoodFlowTextField
import com.jero.designsystem.components.MoodFlowTwoOptionsDialog
import com.jero.designsystem.theme.MoodFlowColors
import com.jero.designsystem.utils.rememberKeyboardAsState
import com.jero.home.HomeViewContract.UiAction
import com.jero.home.HomeViewContract.UiIntent
import com.jero.navigation.MoodFLowScreen
import com.jero.navigation.boundsTransform
import com.jero.navigation.currentComposeNavigator
import org.koin.androidx.compose.koinViewModel

@Composable
fun SharedTransitionScope.MoodFlowHome(
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = koinViewModel(),
) {
    SetStatusBarIconsColor()
    val composeNavigator = currentComposeNavigator
    val state by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val isKeyboardOpen by rememberKeyboardAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isKeyboardOpen) {
        if (!isKeyboardOpen) focusManager.clearFocus(force = true)
    }

    val localInspectionMode = LocalInspectionMode.current
    val gridState = rememberLazyStaggeredGridState()
    val query = remember(state.query) { state.query }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
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
                    if (!state.canBeSelected) {
                        AnimatedVisibility(visible = !isKeyboardOpen) {
                            IconButton(onClick = {
                                viewModel.sendIntent(UiIntent.OnChangeMoreMenuVisibility)
                            }) {
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
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Buscar"
                                )
                            },
                            trailingIcon = {
                                AnimatedVisibility(
                                    visible = query.isNotBlank(),
                                    enter = fadeIn(animationSpec = tween(100)) + scaleIn(
                                        initialScale = 0.8f,
                                        animationSpec = tween(100)
                                    ),
                                    exit = fadeOut(animationSpec = tween(100)) + scaleOut(
                                        targetScale = 0.8f,
                                        animationSpec = tween(100)
                                    )
                                ) {
                                    IconButton(onClick = {
                                        viewModel.sendIntent(
                                            UiIntent.OnSearchQueryChanged(
                                                emptyString()
                                            )
                                        )
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Borrar búsqueda"
                                        )
                                    }
                                }
                            },
                        ) { newQuery ->
                            viewModel.sendIntent(UiIntent.OnSearchQueryChanged(newQuery))
                        }
                    } else {
                        IconButton(onClick = {
                            viewModel.sendIntent(UiIntent.OnChangeMultipleSelectorUIVisibility)
                        }) {
                            Icon(
                                modifier = Modifier.size(32.dp),
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Hide multiple selector"
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(onClick = {
                            viewModel.sendIntent(UiIntent.OnChangeDeleteNotesDialogVisibility)
                        }) {
                            Icon(
                                modifier = Modifier.size(32.dp),
                                painter = painterResource(id = R.drawable.ic_trash),
                                contentDescription = "Delete multiple notes"
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
                        .graphicsLayer {
                            renderEffect = null
                        }
                        .background(MoodFlowColors.defaultLightColors().backGroundColor)
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),

                    verticalItemSpacing = 8.dp,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (query.isNotBlank()) {
                        items(
                            items = state.filteredNotes,
                            key = { it.id }
                        ) { note ->
                            val isChecked = state.selectedNotes.contains(note.id)
                            MoodFlowNote(
                                modifier = Modifier.animateItem(),
                                note = note,
                                titleState = rememberSharedContentState(key = "title-${note.id}"),
                                contentState = rememberSharedContentState(key = "content-${note.id}"),
                                localInspectionMode = localInspectionMode,
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = boundsTransform,
                                canBeSelected = state.canBeSelected,
                                isSelected = isChecked,
                                onClick = { noteId ->
                                    viewModel.sendIntent(UiIntent.OnGoEditNoteScreen(noteId))
                                },
                                onCheck = { noteId, isChecked ->
                                    if (!state.canBeSelected) {
                                        viewModel.sendIntent(UiIntent.OnChangeMultipleSelectorUIVisibility)
                                    }
                                    viewModel.sendIntent(UiIntent.OnSelectNote(note.id, isChecked))
                                },
                            )
                        }
                    } else {

                        if (state.pinnedNotes.isNotEmpty()) {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                Text(stringResource(R.string.pinned_notes))
                            }

                            items(
                                items = state.pinnedNotes,
                                key = { it.id }
                            ) { note ->
                                val isChecked = state.selectedNotes.contains(note.id)
                                MoodFlowNote(
                                    modifier = Modifier.animateItem(),
                                    note = note,
                                    titleState = rememberSharedContentState(key = "title-${note.id}"),
                                    contentState = rememberSharedContentState(key = "content-${note.id}"),
                                    localInspectionMode = localInspectionMode,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = boundsTransform,
                                    canBeSelected = state.canBeSelected,
                                    isSelected = isChecked,
                                    onClick = { noteId ->
                                        viewModel.sendIntent(UiIntent.OnGoEditNoteScreen(noteId))
                                    },
                                    onCheck = { noteId, isChecked ->
                                        if (!state.canBeSelected) {
                                            viewModel.sendIntent(UiIntent.OnChangeMultipleSelectorUIVisibility)
                                        }
                                        viewModel.sendIntent(UiIntent.OnSelectNote(note.id, isChecked))
                                    },
                                )
                            }

                            item(span = StaggeredGridItemSpan.FullLine) {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        item(span = StaggeredGridItemSpan.FullLine) {
                            Text(stringResource(if (state.pinnedNotes.isEmpty()) R.string.notes else R.string.other_notes))
                        }

                        items(
                            items = state.notes,
                            key = { it.id }
                        ) { note ->
                            val isChecked = state.selectedNotes.contains(note.id)
                            MoodFlowNote(
                                modifier = Modifier.animateItem(),
                                note = note,
                                titleState = rememberSharedContentState(key = "title-${note.id}"),
                                contentState = rememberSharedContentState(key = "content-${note.id}"),
                                localInspectionMode = localInspectionMode,
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = boundsTransform,
                                canBeSelected = state.canBeSelected,
                                isSelected = isChecked,
                                onClick = { noteId ->
                                    viewModel.sendIntent(UiIntent.OnGoEditNoteScreen(noteId))
                                },
                                onCheck = { noteId, isChecked ->
                                    if (!state.canBeSelected) {
                                        viewModel.sendIntent(UiIntent.OnChangeMultipleSelectorUIVisibility)
                                    }
                                    viewModel.sendIntent(UiIntent.OnSelectNote(note.id, isChecked))
                                },
                            )
                        }

                        item(span = StaggeredGridItemSpan.FullLine) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                if (!state.canBeSelected) {
                    FloatingActionButton(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 32.dp),
                        onClick = {
                            viewModel.sendIntent(UiIntent.OnGoEditNoteScreen(null))
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Add, null)
                    }
                }

                if (state.showDeleteNotesDialog) {
                    MoodFlowTwoOptionsDialog(
                        titleText = stringResource(R.string.delete_selection),
                        bodyText = stringResource(R.string.delete_selection_question),
                        onAccept = {
                            viewModel.sendIntent(UiIntent.OnDeleteMultipleNotes)
                        },
                        onCancel = {
                            viewModel.sendIntent(UiIntent.OnChangeDeleteNotesDialogVisibility)
                        }
                    )
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
                        indication = null
                    ) {
                        viewModel.sendIntent(UiIntent.OnChangeMoreMenuVisibility)
                    }
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
                    .padding(vertical = 16.dp)
            ) {

                Spacer(modifier = Modifier.weight(1f))

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 16.dp)
                    .clickable {
                        viewModel.sendIntent(UiIntent.OnGoSettingsScreen)
                    }
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

    BackHandler {
        when {
            isKeyboardOpen -> focusManager.clearFocus(force = true)
            query.isNotBlank() -> {
                viewModel.sendIntent(UiIntent.OnSearchQueryChanged(emptyString()))
                focusManager.clearFocus(force = true)
            }
            state.canBeSelected -> viewModel.sendIntent(UiIntent.OnChangeMultipleSelectorUIVisibility)

            else -> (context as? Activity)?.finish()
        }
    }

    HandleActions(viewModel.actions) { action ->
        when (action) {
            UiAction.GoHome -> composeNavigator.navigateAndClearBackStack(MoodFLowScreen.Login)
            UiAction.GoSettingsScreen -> composeNavigator.navigate(MoodFLowScreen.Settings)

            is UiAction.ShowToast -> Toast.makeText(context, action.message, Toast.LENGTH_SHORT)
                .show()

            is UiAction.GoEditNoteScreen -> composeNavigator.navigate(MoodFLowScreen.EditNote(action.note))
        }
    }
}
