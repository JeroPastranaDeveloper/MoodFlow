package com.jero.moodflow.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jero.editnote.MoodFlowEditNote
import com.jero.home.MoodFlowHome
import com.jero.login.MoodFlowLogin
import com.jero.navigation.MoodFLowScreen
import com.jero.register.MoodFlowRegister

context(SharedTransitionScope)
fun NavGraphBuilder.moodFlowNavigation() {
    composable<MoodFLowScreen.Login> {
        MoodFlowLogin(this)
    }

    composable<MoodFLowScreen.Register> {
        MoodFlowRegister(this)
    }

    composable<MoodFLowScreen.Home> {
        MoodFlowHome(this)
    }

    composable<MoodFLowScreen.EditNote>(
        typeMap = MoodFLowScreen.EditNote.typeMap,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(
                    durationMillis = 450,
                    easing = FastOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 450,
                    easing = FastOutSlowInEasing
                )
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing
                )
            )
        }
    ) {
        MoodFlowEditNote(this)
    }


    /*composable<MoodFlowScreen.SelectDatabase> {
        SelectDatabaseScreen(this)
    }

    composable<MoodFlowScreen.Accounts> {
        AccountsScreen(this)
    }

    composable<MoodFlowScreen.AddEditAccount>(
        typeMap = MoodFlowScreen.AddEditAccount.typeMap
    ) {
        AddEditAccountScreen(this)
    }

    composable<MoodFlowScreen.AccountDetail>(
        typeMap = MoodFlowScreen.AccountDetail.typeMap
    ) {
        AccountDetailScreen(this)
    }*/
}
