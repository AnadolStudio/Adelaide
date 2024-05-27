package com.anadolstudio.adelaide.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph
import com.anadolstudio.adelaide.feature.main.NavigationController
import com.anadolstudio.compose.ui.view.snackbar.SnackbarHostState

internal abstract class RootBottomNavGraphContract : RootNavGraphContract() {

    abstract val noBottomNavigationRoutes: Set<String>

    @Composable
    abstract operator fun invoke(
        rootNavigator: NavigationController,
        navigator: NavigationController,
        snackbarHostState: SnackbarHostState
    ): NavGraph
}
