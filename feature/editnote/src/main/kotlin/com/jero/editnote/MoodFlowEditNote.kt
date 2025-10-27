package com.jero.editnote

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
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
import com.jero.designsystem.theme.MoodFlowColors
import com.jero.editnote.EditNoteViewContract.UiAction
import com.jero.editnote.EditNoteViewContract.UiIntent
import com.jero.navigation.boundsTransform
import com.jero.navigation.currentComposeNavigator
import org.koin.androidx.compose.koinViewModel

@Composable
fun SharedTransitionScope.MoodFlowEditNote(
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: EditNoteViewModel = koinViewModel(),
) {
    val composeNavigator = currentComposeNavigator
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current
    val titleFocusRequester = remember { FocusRequester() }
    val contentFocusRequester = remember { FocusRequester() }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = getTopSystemPadding(true), end = 16.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .clickable { viewModel.sendIntent(UiIntent.OnGoBack) },
                    painter = rememberVectorPainter(image = Icons.AutoMirrored.Filled.ArrowBack),
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.weight(1f))

                Crossfade(
                    targetState = state.editedNote.pinned,
                    label = "Icon change"
                ) { pinned ->
                    Icon(
                        modifier = Modifier
                            .size(22.dp)
                            .offset(y = 2.dp)
                            .clickable { viewModel.sendIntent(UiIntent.OnPinChanged) },
                        painter = painterResource(
                            id = if (pinned) R.drawable.ic_pinned
                            else R.drawable.ic_not_pinned
                        ),
                        contentDescription = null
                    )
                }

                if (state.editedNote.id.isNotBlank()) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        modifier = Modifier
                            .clickable {
                                focusManager.clearFocus(force = true)
                                viewModel.sendIntent(UiIntent.OnChangeDeleteDialogVisibility)
                            },
                        painter = painterResource(id = R.drawable.ic_trash),
                        contentDescription = null,
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)
            .background(MoodFlowColors.defaultLightColors().backGroundColor)) {
            Column {
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
                    placeholder = "Title",
                    placeholderFontSize = 20.sp,
                    focusRequester = titleFocusRequester,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Unspecified,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { contentFocusRequester.requestFocus() }
                    ),
                ) { title ->
                    viewModel.sendIntent(UiIntent.OnTitleChanged(title))
                }

                Spacer(modifier = Modifier.height(4.dp))

                MoodFlowTransparentTextField(
                    modifier = Modifier.fillMaxSize().moodFlowSharedElement(
                        isLocalInspectionMode = LocalInspectionMode.current,
                        state = rememberSharedContentState(key = "content-${state.originalNote.id}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = boundsTransform,
                    ),
                    text = state.editedNote.content,
                    textFontSize = 16.sp,
                    placeholder = "Note",
                    placeholderFontSize = 16.sp,
                    focusRequester = contentFocusRequester,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Unspecified,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus(force = true) }
                    ),
                ) { description ->
                    viewModel.sendIntent(UiIntent.OnDescriptionChanged(description))
                }
            }
            if (state.showDeleteNoteDialog) {
                MoodFlowTwoOptionsDialog(
                    titleText = "Delete note",
                    bodyText = "Are you sure you want to delete this note?",
                    onAccept = {
                        viewModel.sendIntent(UiIntent.OnDeleteNote)
                    },
                    onCancel = {
                        viewModel.sendIntent(UiIntent.OnChangeDeleteDialogVisibility)
                    }
                )
            }
        }
    }

    BackHandler {
        viewModel.sendIntent(UiIntent.OnGoBack)
    }

    HandleActions(viewModel.actions) { action ->
        when (action) {
            UiAction.GoBack -> {
                focusManager.clearFocus(force = true)
                composeNavigator.navigateUp()
            }

            is UiAction.ShowToast -> Toast.makeText(
                context,
                action.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
