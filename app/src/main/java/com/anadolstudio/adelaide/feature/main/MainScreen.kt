@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialNavigationApi::class)

package com.anadolstudio.adelaide.feature.main

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import com.anadolstudio.adelaide.di.viewmodel.daggerViewModel
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.shape
import com.anadolstudio.compose.ui.view.snackbar.SnackbarHost
import com.anadolstudio.compose.ui.view.snackbar.rememberSnackbarHostState
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout

@Composable
internal fun MainScreen(
    navigator: LicardNavigator,
    viewModel: MainViewModel = daggerViewModel(),
) {
    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = rememberSnackbarHostState(scaffoldState.snackbarHostState)

    Scaffold(
        modifier = Modifier.semantics { testTagsAsResourceId = true },
        scaffoldState = scaffoldState,
        backgroundColor = AdelaideTheme.colors.backgroundPrimary,
        snackbarHost = { hostState ->
            SnackbarHost(
                hostState = hostState,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .navigationBarsPadding()
                    .imePadding(),
            )
        },
    ) { paddingValues ->
        ModalBottomSheetLayout(
            bottomSheetNavigator = navigator.bottomSheetNavigator,
            sheetShape = ModalBottomSheetDefaults.shape,
            scrimColor = AdelaideTheme.colors.textPrimary.copy(alpha = 0.32f),
            sheetBackgroundColor = AdelaideTheme.colors.backgroundPrimary
        ) {
            NavHost(
                navController = navigator,
                graph = MainGraph(navigator, snackbarHostState),
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}
