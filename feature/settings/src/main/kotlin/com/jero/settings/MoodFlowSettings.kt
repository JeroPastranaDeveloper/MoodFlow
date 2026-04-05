package com.jero.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.jero.core.screen.HandleActions
import com.jero.core.screen.SetStatusBarIconsColor
import com.jero.core.screen.getTopSystemPadding
import com.jero.designsystem.components.MoodFlowButton
import com.jero.designsystem.components.MoodFlowTwoOptionsDialog
import com.jero.designsystem.theme.MoodFlowColors
import com.jero.settings.SettingsViewContract.UiAction
import com.jero.settings.SettingsViewContract.UiIntent
import com.jero.settings.SettingsViewContract.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun MoodFlowSettings(
    viewModel: SettingsViewModel = koinViewModel(),
    onGoLogin: () -> Unit,
    onGoBack: () -> Unit,
    onGoTags: () -> Unit,
) {
    SetStatusBarIconsColor()
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    HandleActions(viewModel.actions) { action ->
        when (action) {
            is UiAction.ShowToast -> Toast.makeText(context, action.message, Toast.LENGTH_SHORT).show()
            UiAction.GoLogin -> onGoLogin()
            UiAction.GoTagsScreen -> onGoTags()
            UiAction.GoBack -> onGoBack()
        }
    }

    Content(
        state = state,
        onChangeCloseSessionDialogVisibility = { viewModel.sendIntent(UiIntent.OnChangeCloseSessionDialogVisibility) },
        onCloseSession = { viewModel.sendIntent(UiIntent.OnCloseSession) },
        onGoTags = { viewModel.sendIntent(UiIntent.OnGoTagsScreen) },
        onGoBack = { viewModel.sendIntent(UiIntent.OnGoBack) },
    )
}

@Composable
private fun Content(
    state: UiState,
    onChangeCloseSessionDialogVisibility: () -> Unit,
    onCloseSession: () -> Unit,
    onGoBack: () -> Unit,
    onGoTags: () -> Unit,
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
                    text = stringResource(R.string.settings),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(top = getTopSystemPadding())
            ) {
                MoodFlowButton(
                    text = stringResource(R.string.tags),
                    addBorder = true,
                    textColor = Color.Black,
                    backgroundColor = MoodFlowColors.defaultLightColors().white,
                ) { onGoTags() }

                Spacer(modifier = Modifier.height(16.dp))

                Spacer(modifier = Modifier.weight(1f))

                MoodFlowButton(
                    text = stringResource(R.string.sign_out),
                    addBorder = true,
                    textColor = Color.White,
                    backgroundColor = MoodFlowColors.defaultLightColors().primary,
                ) { onChangeCloseSessionDialogVisibility() }

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (state.isCloseSessionDialogVisible) {
                MoodFlowTwoOptionsDialog(
                    titleText = stringResource(R.string.sign_out),
                    bodyText = stringResource(R.string.sign_out_question),
                    onAccept = { onCloseSession() },
                    onCancel = { onChangeCloseSessionDialogVisibility() }
                )
            }
        }
    }
}
