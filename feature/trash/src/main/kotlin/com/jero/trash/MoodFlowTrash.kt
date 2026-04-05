package com.jero.trash

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jero.core.designsystem.R
import com.jero.core.model.Note
import com.jero.core.screen.HandleActions
import com.jero.core.screen.SetStatusBarIconsColor
import com.jero.core.screen.getTopSystemPadding
import com.jero.designsystem.components.MoodFlowTwoOptionsDialog
import com.jero.trash.TrashViewContract.UiAction
import com.jero.trash.TrashViewContract.UiIntent
import com.jero.trash.TrashViewContract.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun MoodFlowTrash(
    viewModel: TrashViewModel = koinViewModel(),
    onGoBack: () -> Unit,
) {
    SetStatusBarIconsColor()
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    HandleActions(viewModel.actions) { action ->
        when (action) {
            UiAction.GoBack -> onGoBack()
            is UiAction.ShowToast -> Toast.makeText(context, action.message, Toast.LENGTH_SHORT).show()
        }
    }

    Content(
        state = state,
        onGoBack = { viewModel.sendIntent(UiIntent.OnGoBack) },
        onRestoreNote = { viewModel.sendIntent(UiIntent.OnRestoreNote(it)) },
        onRequestDeletePermanently = { viewModel.sendIntent(UiIntent.OnRequestDeletePermanently(it)) },
        onConfirmDeletePermanently = { viewModel.sendIntent(UiIntent.OnConfirmDeletePermanently) },
        onCancelDeletePermanently = { viewModel.sendIntent(UiIntent.OnCancelDeletePermanently) },
        onChangeEmptyTrashDialogVisibility = { viewModel.sendIntent(UiIntent.OnChangeEmptyTrashDialogVisibility) },
        onEmptyTrash = { viewModel.sendIntent(UiIntent.OnEmptyTrash) },
    )
}

@Composable
private fun Content(
    state: UiState,
    onGoBack: () -> Unit,
    onRestoreNote: (String) -> Unit,
    onRequestDeletePermanently: (String) -> Unit,
    onConfirmDeletePermanently: () -> Unit,
    onCancelDeletePermanently: () -> Unit,
    onChangeEmptyTrashDialogVisibility: () -> Unit,
    onEmptyTrash: () -> Unit,
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = getTopSystemPadding(), end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onGoBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back",
                    )
                }
                Text(
                    text = stringResource(R.string.trash),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )
                if (state.notes.isNotEmpty()) {
                    TextButton(onClick = onChangeEmptyTrashDialogVisibility) {
                        Text(stringResource(R.string.empty_trash))
                    }
                }
            }
        },
        bottomBar = {
            if (state.notes.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.trash_auto_delete),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .navigationBarsPadding(),
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (state.notes.isEmpty()) {
                Text(
                    text = stringResource(R.string.trash_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.notes, key = { it.id }) { note ->
                        TrashNoteItem(
                            modifier = Modifier.animateItem(),
                            note = note,
                            onRestoreNote = onRestoreNote,
                            onRequestDeletePermanently = onRequestDeletePermanently,
                        )
                        HorizontalDivider(modifier = Modifier.animateItem())
                    }
                }
            }
        }
    }

    if (state.noteIdPendingDeletion != null) {
        MoodFlowTwoOptionsDialog(
            titleText = stringResource(R.string.delete_permanently),
            bodyText = stringResource(R.string.delete_permanently_question),
            onAccept = onConfirmDeletePermanently,
            onCancel = onCancelDeletePermanently,
        )
    }

    if (state.showEmptyTrashDialog) {
        MoodFlowTwoOptionsDialog(
            titleText = stringResource(R.string.empty_trash),
            bodyText = stringResource(R.string.empty_trash_question),
            onAccept = onEmptyTrash,
            onCancel = onChangeEmptyTrashDialogVisibility,
        )
    }
}

@Composable
private fun TrashNoteItem(
    modifier: Modifier = Modifier,
    note: Note,
    onRestoreNote: (String) -> Unit,
    onRequestDeletePermanently: (String) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = note.title.ifBlank { stringResource(R.string.note) },
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (note.content.isNotBlank()) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Spacer(modifier = Modifier.padding(horizontal = 4.dp))

        IconButton(onClick = { onRestoreNote(note.id) }) {
            Icon(
                imageVector = Icons.Default.Restore,
                contentDescription = stringResource(R.string.restore),
            )
        }

        IconButton(onClick = { onRequestDeletePermanently(note.id) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete_permanently),
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}
