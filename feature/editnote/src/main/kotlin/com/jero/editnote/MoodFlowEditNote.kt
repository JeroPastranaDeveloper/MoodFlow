package com.jero.editnote

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AirplaneTicket
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.BatteryUnknown
import androidx.compose.material.icons.automirrored.filled._360
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jero.core.screen.HandleActions
import com.jero.core.screen.getTopSystemPadding
import com.jero.designsystem.components.MoodFlowTransparentTextField
import com.jero.designsystem.components.MoodFlowTwoOptionsDialog
import com.jero.editnote.EditNoteViewContract.UiAction
import com.jero.editnote.EditNoteViewContract.UiIntent
import com.jero.navigation.currentComposeNavigator
import org.koin.androidx.compose.koinViewModel

@Composable
fun SharedTransitionScope.MoodFlowEditNote(
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: EditNoteViewModel = koinViewModel(),
) {
    val composeNavigator = currentComposeNavigator
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = getTopSystemPadding(), end = 16.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .clickable { viewModel.sendIntent(UiIntent.OnGoBack) },
                    painter = rememberVectorPainter(image = Icons.AutoMirrored.Filled.ArrowBack),
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.weight(1f))

                // TODO: CAMBIAR ICONOS Y HACER ANIMACIÓN PARA PASAR DE LA HOME A LA EDIT NOTE
                Crossfade(
                    targetState = state.editedNote.pinned,
                    label = "Icon change"
                ) { pinned ->
                    Icon(
                        modifier = Modifier
                            .clickable { viewModel.sendIntent(UiIntent.OnPinChanged) },
                        painter = rememberVectorPainter(
                            image = if (pinned) Icons.AutoMirrored.Filled._360
                            else Icons.AutoMirrored.Filled.AirplaneTicket
                        ),
                        contentDescription = null
                    )
                }


                if (state.editedNote.id.isNotBlank()) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        modifier = Modifier
                            .clickable { viewModel.sendIntent(UiIntent.OnChangeDeleteDialogVisibility) },
                        painter = rememberVectorPainter(image = Icons.AutoMirrored.Filled.BatteryUnknown),
                        contentDescription = null,
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                MoodFlowTransparentTextField(
                    text = state.editedNote.title,
                    textFontSize = 20.sp,
                    textFontWeight = FontWeight.Bold,
                    placeholder = "Title",
                    placeholderFontSize = 20.sp
                ) { title ->
                    viewModel.sendIntent(UiIntent.OnTitleChanged(title))
                }

                Spacer(modifier = Modifier.height(4.dp))

                MoodFlowTransparentTextField(
                    text = state.editedNote.content,
                    placeholder = "Note",
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
            UiAction.GoBack -> composeNavigator.navigateUp()

            is UiAction.ShowToast -> Toast.makeText(
                context,
                action.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
