package com.jero.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel

@Composable
fun SharedTransitionScope.MoodFlowHome(
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = koinViewModel()
) {/*
    SetStatusBarIconsColor()
    val composeNavigator = currentComposeNavigator
    val state by viewModel.state.collectAsState(UiState())

    val context = LocalContext.current
    val fileManager: FileManager = koinInject()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(UiIntent.LoadAccounts)
    }

    Scaffold(
        topBar = { MoodFlowAppBar(
            stringResource(R.string.accounts_screen_title),
            showAdditionalOptions = true,
            additionalOptions = listOf(
                stringResource(R.string.select_another_database),
                stringResource(R.string.github),
                stringResource(R.string.linkedin),
            ),
        ) {
            when (it) {
                0 -> {
                    viewModel.sendIntent(UiIntent.ClearPreferences)
                }
                1 -> {
                    viewModel.sendIntent(UiIntent.OpenExplorer(it))
                }
                2 -> {
                    viewModel.sendIntent(UiIntent.OpenExplorer(it))
                }
            }
        } },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {

            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(32.dp),
                contentColor = Color.White,
                containerColor = Color.Black,
                onClick = { viewModel.sendIntent(UiIntent.OnAddSeeAccount()) }
            ) {
                Icon(
                    painter = painterResource(
                        R.drawable.ic_add
                    ),
                    contentDescription = "Add",
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                state.accounts.forEachIndexed { index, account ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {
                                    viewModel.sendIntent(UiIntent.OnAddSeeAccount(account.id))
                                },
                                onLongClick = {
                                    viewModel.sendIntent(UiIntent.OnDeleteAccount(account.id))
                                }
                            )
                            .padding(16.dp)
                    ) {
                        Text(text = account.title)
                    }
                    if (index != state.accounts.lastIndex) {
                        HorizontalDivider()
                    }
                }

                if (state.showDeleteAccountDialog) {
                    CustomDialog(
                        stringResource(R.string.delete_account), stringResource(R.string.can_not_undone_action),
                        onAccept = {
                            viewModel.sendIntent(UiIntent.DeleteAccount)
                        },
                        onCancel = {
                            viewModel.sendIntent(UiIntent.HideDeleteAccountDialog)
                        }
                    )
                }
            }
        }
    }

    BackHandler {
        (context as? Activity)?.finish()
    }

    HandleActions(viewModel.actions) { action ->
        when (action) {
            is UiAction.OnAddSeeAccount -> {

            }

            is UiAction.LoadAccounts -> {
                val accounts = fileManager.readAccounts(context, action.uri.toUri())
                viewModel.sendIntent(UiIntent.SetAccounts(accounts))
            }

            is UiAction.DeleteAccount -> {
                val updatedAccounts =
                    fileManager.deleteAccount(context, action.uri, action.accountId)
                viewModel.sendIntent(UiIntent.SetAccounts(updatedAccounts))
            }

            UiAction.GoDatabaseSelection -> composeNavigator.navigateUp()

            is UiAction.OpenExplorer -> {
                val intent = Intent(Intent.ACTION_VIEW, action.uri)
                context.startActivity(intent)
            }
        }
    }*/
}
