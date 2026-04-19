package com.jero.moodflow.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.glance.appwidget.updateAll
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.jero.editnote.MoodFlowEditNote
import com.jero.home.MoodFlowHome
import com.jero.login.MoodFlowLogin
import com.jero.moodflow.widget.NotesWidget
import com.jero.navigation.MoodFLowScreen
import com.jero.navigation.utils.clearAndNavigateTo
import com.jero.navigation.utils.goBack
import com.jero.register.MoodFlowRegister
import com.jero.settings.MoodFlowSettings
import com.jero.settings.MoodFlowTags
import com.jero.trash.MoodFlowTrash
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MoodFlowNavigation(isLogged: Boolean) {
    val startScreen = if (isLogged) MoodFLowScreen.Home else MoodFLowScreen.Login
    val backStack = rememberNavBackStack(startScreen)
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    SharedTransitionLayout {
        NavDisplay(
            backStack = backStack,
            sceneStrategies = listOf(listDetailStrategy),
            entryProvider = entryProvider {
                entry<MoodFLowScreen.Login> {
                    MoodFlowLogin(
                        onGoHome = {
                            backStack.clearAndNavigateTo(MoodFLowScreen.Home)
                        },
                        onGoRegister = {
                            backStack.add(MoodFLowScreen.Register)
                        }
                    )
                }

                entry<MoodFLowScreen.Register> {
                    MoodFlowRegister(
                        onGoHome = {
                            backStack.clearAndNavigateTo(MoodFLowScreen.Home)
                        },
                        onGoBack = {
                            backStack.goBack()
                        }
                    )
                }

                entry<MoodFLowScreen.Home>(
                    metadata = ListDetailSceneStrategy.listPane()
                ) {
                    MoodFlowHome(
                        LocalNavAnimatedContentScope.current,
                        onGoLogin = {
                            backStack.clearAndNavigateTo(MoodFLowScreen.Login)
                        },
                        onGoEditNote = { noteId ->
                            backStack.add(
                                MoodFLowScreen.EditNote(
                                    id = noteId,
                                )
                            )
                        },
                        onGoSettings = {
                            backStack.add(MoodFLowScreen.Settings)
                        },
                        onGoTrash = {
                            backStack.add(MoodFLowScreen.Trash)
                        },
                    )
                }

                entry<MoodFLowScreen.EditNote>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) {
                    MoodFlowEditNote(
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                        noteId = it.id,
                        onGoBack = {
                            coroutineScope.launch { NotesWidget().updateAll(context) }
                            backStack.goBack()
                        }
                    )
                }

                entry<MoodFLowScreen.Settings> {
                    MoodFlowSettings(
                        onGoLogin = { backStack.clearAndNavigateTo(MoodFLowScreen.Login) },
                        onGoBack = { backStack.goBack() },
                        onGoTags = { backStack.add(MoodFLowScreen.Tags) },
                    )
                }

                entry<MoodFLowScreen.Tags> {
                    MoodFlowTags(
                        onGoBack = { backStack.goBack() },
                    )
                }

                entry<MoodFLowScreen.Trash> {
                    MoodFlowTrash(
                        onGoBack = {
                            backStack.goBack()
                        }
                    )
                }
            },
            transitionSpec = {
                slideInHorizontally(initialOffsetX = { it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { -it })
            },
            popTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
            predictivePopTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
        )
    }
}
