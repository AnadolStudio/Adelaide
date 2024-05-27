package com.anadolstudio.adelaide.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph
import androidx.navigation.compose.composable
import com.anadolstudio.adelaide.feature.gallery.presetnation.MediaScreen
import com.anadolstudio.adelaide.navigation.RootNavGraphContract
import com.anadolstudio.compose.ui.view.snackbar.SnackbarHostState

internal object MainGraph : RootNavGraphContract() {

    override val startDestination = gallery()

    private fun gallery() = route { "gallery" }

    @Composable
    operator fun invoke(
        navigator: NavigationController,
        snackbarHostState: SnackbarHostState,
    ): NavGraph = remember(navigator, snackbarHostState) {
        navigator.createGraph {
            composable(gallery()) { MediaScreen(navigator, snackbarHostState) }
        }
    }

}
