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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.jero.navigation.currentComposeNavigator
import org.koin.androidx.compose.koinViewModel

@Composable
fun SharedTransitionScope.MoodFlowHome(
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = koinViewModel(),
) {
    SetStatusBarIconsColor()
    val composeNavigator = currentComposeNavigator
    val state by viewModel.state.collectAsState()

    val context = LocalContext.current
    val isKeyboardOpen by rememberKeyboardAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isKeyboardOpen) {
        if (!isKeyboardOpen) focusManager.clearFocus(force = true)
    }

    Scaffold(
        topBar = {
            MoodFlowTextField(
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = getTopSystemPadding(),
                    end = 16.dp,
                    bottom = 16.dp
                ),
                text = state.query,
                placeHolder = "Buscar...",
                placeHolderFontSize = 20.sp,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = state.query.isNotBlank(),
                        enter = fadeIn(animationSpec = tween(200)) + scaleIn(
                            initialScale = 0.8f,
                            animationSpec = tween(200)
                        ),
                        exit = fadeOut(animationSpec = tween(200)) + scaleOut(
                            targetScale = 0.8f,
                            animationSpec = tween(200)
                        )
                    ) {
                        IconButton(onClick = {
                            viewModel.sendIntent(UiIntent.OnSearchQueryChanged(emptyString()))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Borrar búsqueda"
                            )
                        }
                    }
                },
            ) { query ->
                viewModel.sendIntent(UiIntent.OnSearchQueryChanged(query))
            }
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .background(MoodFlowColors.defaultLightColors().backGroundColor)
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (state.query.isNotBlank()) {
                    items(
                        items = state.filteredNotes,
                        key = { it.id }
                    ) { note ->
                        MoodFlowNote(
                            modifier = Modifier.animateItem(),
                            note = note,
                            onClick = { noteId ->
                                viewModel.sendIntent(UiIntent.OnGoEditNoteScreen(noteId))
                            },
                            onLongClick = { noteId ->
                                viewModel.sendIntent(UiIntent.OnShowDeleteNoteDialog(noteId))
                            }
                        )
                    }
                } else {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Text(
                            modifier = Modifier.clickable {
                                viewModel.sendIntent(UiIntent.OnCloseSession)
                            },
                            text = "Cerrar sesión"
                        )
                    }

                    if (state.pinnedNotes.isNotEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        item(span = StaggeredGridItemSpan.FullLine) {
                            Text("Pinned notes")
                        }

                        items(
                            items = state.pinnedNotes,
                            key = { it.id }
                        ) { note ->
                            MoodFlowNote(
                                modifier = Modifier.animateItem(),
                                note = note,
                                onClick = { noteId ->
                                    viewModel.sendIntent(UiIntent.OnGoEditNoteScreen(noteId))
                                },
                                onLongClick = { noteId ->
                                    viewModel.sendIntent(UiIntent.OnShowDeleteNoteDialog(noteId))
                                }
                            )
                        }

                        item(span = StaggeredGridItemSpan.FullLine) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    item(span = StaggeredGridItemSpan.FullLine) {
                        Text(if (state.pinnedNotes.isEmpty()) "Notes" else "Other notes")
                    }

                    items(
                        items = state.notes,
                        key = { it.id }
                    ) { note ->
                        MoodFlowNote(
                            modifier = Modifier.animateItem(),
                            note = note,
                            onClick = { noteId ->
                                viewModel.sendIntent(UiIntent.OnGoEditNoteScreen(noteId))
                            },
                            onLongClick = { noteId ->
                                viewModel.sendIntent(UiIntent.OnShowDeleteNoteDialog(noteId))
                            }
                        )
                    }

                    item(span = StaggeredGridItemSpan.FullLine) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

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

            if (state.showDeleteNoteDialog) {
                MoodFlowTwoOptionsDialog(
                    titleText = "Delete note",
                    bodyText = "Are you sure you want to delete this note?",
                    onAccept = {
                        viewModel.sendIntent(UiIntent.OnDeleteNote)
                    },
                    onCancel = {
                        viewModel.sendIntent(UiIntent.OnChangeDeleteNoteDialogVisibility)
                    }
                )
            }
        }
    }

    BackHandler {
        when {
            isKeyboardOpen -> focusManager.clearFocus(force = true)
            state.query.isNotBlank() -> {
                viewModel.sendIntent(UiIntent.OnSearchQueryChanged(emptyString()))
                focusManager.clearFocus(force = true)
            }
            else -> (context as? Activity)?.finish()
        }
    }

    HandleActions(viewModel.actions) { action ->
        when (action) {
            UiAction.GoHome -> composeNavigator.navigate(MoodFLowScreen.Login)

            is UiAction.ShowToast -> Toast.makeText(context, action.message, Toast.LENGTH_SHORT)
                .show()
            is UiAction.GoEditNoteScreen -> composeNavigator.navigate(MoodFLowScreen.EditNote(action.note))
        }
    }
}
