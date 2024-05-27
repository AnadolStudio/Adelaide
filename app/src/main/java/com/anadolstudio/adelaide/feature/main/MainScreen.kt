@file:OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterialNavigationApi::class,
    ExperimentalMaterial3Api::class
)

package com.anadolstudio.adelaide.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import com.anadolstudio.adelaide.di.viewmodel.daggerViewModel
import com.anadolstudio.adelaide.util.livedata.observeState
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.Dimension
import com.anadolstudio.compose.ui.theme.Shapes
import com.anadolstudio.compose.ui.theme.largeBanner
import com.anadolstudio.compose.ui.theme.preview.ThemePreviewParameter
import com.anadolstudio.compose.ui.theme.shape
import com.anadolstudio.compose.ui.view.selector.SegmentPicker
import com.anadolstudio.compose.ui.view.snackbar.SnackbarHost
import com.anadolstudio.compose.ui.view.snackbar.SnackbarHostState
import com.anadolstudio.compose.ui.view.snackbar.rememberSnackbarHostState
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout

@Composable
internal fun MainScreen(
    navigator: NavigationController,
    viewModel: MainViewModel = daggerViewModel(),
) {
    val state by viewModel.stateLiveData.observeState()

    MainLayout(navigator, state, viewModel)
}

@Composable
private fun MainLayout(
    navigator: NavigationController?,
    state: MainScreenState,
    controller: MainController,
) {
    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = rememberSnackbarHostState(scaffoldState.snackbarHostState)
    val scrollBottomBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.semantics { testTagsAsResourceId = true },
        scaffoldState = scaffoldState,
        backgroundColor = AdelaideTheme.colors.colorSecondary,
        snackbarHost = { hostState ->
            SnackbarHost(
                hostState = hostState,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .navigationBarsPadding()
                    .imePadding(),
            )
        },
        bottomBar = { MainBottomAppBar(state, controller, scrollBottomBehavior) }
    ) { paddingValues ->
        navigator?.let {
            ModalBottomSheet(
                it,
                snackbarHostState,
                paddingValues,
                scrollBottomBehavior
            )
        }
    }
}

@Composable
private fun ModalBottomSheet(
    navigator: NavigationController,
    snackbarHostState: SnackbarHostState,
    paddingValues: PaddingValues,
    scrollBottomBehavior: BottomAppBarScrollBehavior
) {
    ModalBottomSheetLayout(
        bottomSheetNavigator = navigator.bottomSheetNavigator,
        sheetShape = ModalBottomSheetDefaults.shape,
        scrimColor = AdelaideTheme.colors.textPrimary.copy(alpha = 0.32f),
        sheetBackgroundColor = AdelaideTheme.colors.colorPrimary
    ) {
        NavHost(
            navController = navigator,
            graph = MainGraph(navigator, snackbarHostState),
            modifier = Modifier
                .padding(paddingValues.calculateTopPadding())
                .nestedScroll(scrollBottomBehavior.nestedScrollConnection),
        )
    }
}

@Composable
private fun MainBottomAppBar(
    state: MainScreenState,
    controller: MainController,
    scrollBottomBehavior: BottomAppBarScrollBehavior
) {
    BottomAppBar(
        containerColor = Color.Transparent,
        scrollBehavior = scrollBottomBehavior,
        contentPadding = PaddingValues(horizontal = Dimension.mainMargin)
    ) {
        SegmentPicker(
            modifier = Modifier.background(
                color = AdelaideTheme.colors.colorPrimary,
                shape = Shapes.largeBanner
            ),
            segments = state.tabList,
            selectedIndex = state.currentTab.ordinal,
            onSegmentChange = controller::onTabClicked,
            fillWidth = true,
            height = 48.dp,
        )
    }
}

@Preview
@Composable
private fun Preview(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    val state = remember {
        MainScreenState(
            tabList = listOf("Медиа", "Альбомы", "Новости")
        )
    }
    val controller = object : MainController {
        override fun onBackClicked() = Unit
        override fun onTabClicked(tabIndex: Int) = Unit
    }
    AdelaideTheme(useDarkMode) {
        MainLayout(null, state, controller)
    }
}
