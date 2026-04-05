package com.jero.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jero.core.designsystem.R
import com.jero.core.model.Tag
import com.jero.core.model.UNTAGGED_TAG_ID
import com.jero.core.screen.HandleActions
import com.jero.core.screen.SetStatusBarIconsColor
import com.jero.core.screen.getTopSystemPadding
import com.jero.designsystem.components.MoodFlowTwoOptionsDialog
import com.jero.designsystem.theme.MoodFlowColors
import com.jero.settings.TagsViewContract.UiAction
import com.jero.settings.TagsViewContract.UiIntent
import com.jero.settings.TagsViewContract.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun MoodFlowTags(
    viewModel: TagsViewModel = koinViewModel(),
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
        onShowCreateDialog = { viewModel.sendIntent(UiIntent.OnShowCreateDialog) },
        onDismissCreateDialog = { viewModel.sendIntent(UiIntent.OnDismissCreateDialog) },
        onNewTagNameChanged = { viewModel.sendIntent(UiIntent.OnNewTagNameChanged(it)) },
        onCreateTag = { viewModel.sendIntent(UiIntent.OnCreateTag) },
        onStartEditTag = { viewModel.sendIntent(UiIntent.OnStartEditTag(it)) },
        onEditTagNameChanged = { viewModel.sendIntent(UiIntent.OnEditTagNameChanged(it)) },
        onConfirmEditTag = { viewModel.sendIntent(UiIntent.OnConfirmEditTag) },
        onDismissEditTag = { viewModel.sendIntent(UiIntent.OnDismissEditTag) },
        onRequestDeleteTag = { viewModel.sendIntent(UiIntent.OnRequestDeleteTag(it)) },
        onConfirmDeleteTag = { viewModel.sendIntent(UiIntent.OnConfirmDeleteTag) },
        onDismissDeleteTag = { viewModel.sendIntent(UiIntent.OnDismissDeleteTag) },
        onGoBack = { viewModel.sendIntent(UiIntent.OnGoBack) },
    )
}

@Composable
private fun Content(
    state: UiState,
    onShowCreateDialog: () -> Unit,
    onDismissCreateDialog: () -> Unit,
    onNewTagNameChanged: (String) -> Unit,
    onCreateTag: () -> Unit,
    onStartEditTag: (Tag) -> Unit,
    onEditTagNameChanged: (String) -> Unit,
    onConfirmEditTag: () -> Unit,
    onDismissEditTag: () -> Unit,
    onRequestDeleteTag: (Tag) -> Unit,
    onConfirmDeleteTag: () -> Unit,
    onDismissDeleteTag: () -> Unit,
    onGoBack: () -> Unit,
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
                        painter = rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowBack),
                        contentDescription = null,
                    )
                }
                Text(
                    text = stringResource(R.string.tags),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onShowCreateDialog,
                containerColor = MoodFlowColors.defaultLightColors().primary,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.create_tag),
                    tint = Color.White,
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            items(state.tags.filter { it.id != UNTAGGED_TAG_ID }, key = { it.id }) { tag ->
                TagItem(
                    tag = tag,
                    onEdit = { onStartEditTag(tag) },
                    onDelete = { onRequestDeleteTag(tag) },
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (state.showCreateDialog) {
        TagNameDialog(
            title = stringResource(R.string.create_tag),
            name = state.newTagName,
            onNameChanged = onNewTagNameChanged,
            onConfirm = onCreateTag,
            onDismiss = onDismissCreateDialog,
        )
    }

    state.editingTag?.let { tag ->
        TagNameDialog(
            title = stringResource(R.string.edit_tag),
            name = tag.name,
            onNameChanged = onEditTagNameChanged,
            onConfirm = onConfirmEditTag,
            onDismiss = onDismissEditTag,
        )
    }

    state.tagToDelete?.let { tag ->
        MoodFlowTwoOptionsDialog(
            titleText = stringResource(R.string.delete_tag),
            bodyText = stringResource(R.string.delete_tag_confirmation, tag.name),
            onAccept = onConfirmDeleteTag,
            onCancel = onDismissDeleteTag,
        )
    }
}

@Composable
private fun TagItem(
    tag: Tag,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val isProtected = tag.id == UNTAGGED_TAG_ID

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = tag.name,
            style = MaterialTheme.typography.bodyLarge,
        )

        if (!isProtected) {
            IconButton(onClick = onEdit, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_tag),
                    tint = Color.Gray,
                )
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_tag),
                    tint = Color.Gray,
                )
            }
        }
    }
}

@Composable
private fun TagNameDialog(
    title: String,
    name: String,
    onNameChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChanged,
                label = { Text(stringResource(R.string.tag_name)) },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = name.isNotBlank()) {
                Text(stringResource(R.string.accept_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_button))
            }
        }
    )
}
