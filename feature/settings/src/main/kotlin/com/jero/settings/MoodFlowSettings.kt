package com.jero.settings

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jero.core.designsystem.R
import com.jero.core.screen.HandleActions
import com.jero.core.screen.SetStatusBarIconsColor
import com.jero.core.screen.getTopSystemPadding
import com.jero.designsystem.components.MoodFlowButton
import com.jero.designsystem.components.MoodFlowTwoOptionsDialog
import com.jero.designsystem.theme.MoodFlowColors
import com.jero.navigation.MoodFLowScreen
import com.jero.navigation.currentComposeNavigator
import com.jero.settings.SettingsViewContract.UiAction
import com.jero.settings.SettingsViewContract.UiIntent
import org.koin.androidx.compose.koinViewModel

@Composable
fun SharedTransitionScope.MoodFlowSettings(
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    SetStatusBarIconsColor()
    val context = LocalContext.current
    val composeNavigator = currentComposeNavigator
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Icon(
                modifier = Modifier
                    .padding(start = 16.dp, top = getTopSystemPadding())
                    .clickable { viewModel.sendIntent(UiIntent.OnGoBack) },
                painter = rememberVectorPainter(image = Icons.AutoMirrored.Filled.ArrowBack),
                contentDescription = null,
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(top = getTopSystemPadding())
            ) {
                Spacer(modifier = Modifier.weight(1f))

                MoodFlowButton(
                    text = stringResource(R.string.sign_out),
                    addBorder = true,
                    textColor = Color.White,
                    backgroundColor = MoodFlowColors.defaultLightColors().primary,
                ) {
                    viewModel.sendIntent(UiIntent.OnChangeCloseSessionDialogVisibility)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (state.isCloseSessionDialogVisible) {
                MoodFlowTwoOptionsDialog(
                    titleText = stringResource(R.string.sign_out),
                    bodyText = stringResource(R.string.sign_out_question),
                    onAccept = { viewModel.sendIntent(UiIntent.OnCloseSession) },
                    onCancel = { viewModel.sendIntent(UiIntent.OnChangeCloseSessionDialogVisibility) }
                )
            }
        }
    }

    HandleActions(viewModel.actions) { action ->
        when (action) {
            UiAction.GoLogin -> composeNavigator.navigateAndClearBackStack(MoodFLowScreen.Login)
            UiAction.GoBack -> composeNavigator.navigateUp()

            is UiAction.ShowToast -> Toast.makeText(context, action.message, Toast.LENGTH_SHORT)
                .show()
        }
    }
}
