package com.jero.home

import android.app.Activity
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jero.core.screen.HandleActions
import com.jero.core.screen.SetStatusBarIconsColor
import com.jero.core.screen.getTopSystemPadding
import com.jero.core.utils.emptyString
import com.jero.designsystem.components.MoodFlowTextField
import com.jero.designsystem.theme.MoodFlowColors
import com.jero.designsystem.utils.rememberKeyboardAsState
import com.jero.home.HomeViewContract.UiIntent
import com.jero.navigation.currentComposeNavigator
import org.koin.androidx.compose.koinViewModel

@Composable
fun SharedTransitionScope.MoodFlowHome(
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = koinViewModel(),
) {
    SetStatusBarIconsColor(darkIcons = true)
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
                modifier = Modifier.padding(start = 16.dp, top = getTopSystemPadding(), end = 16.dp),
                text = state.query,
                placeHolder = "Buscar... ",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MoodFlowColors.defaultLightColors().backGroundColor)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Hola")
            LazyVerticalStaggeredGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                columns = StaggeredGridCells.Fixed(2),
                verticalItemSpacing = 4.dp,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                content = {
                    items(state.notes.size) { index ->
                        Text(text = state.notes[index].title)
                    }
                }
            )
        }
    }

    BackHandler {
        (context as? Activity)?.finish()
    }

    HandleActions(viewModel.actions) { action ->

    }
}
