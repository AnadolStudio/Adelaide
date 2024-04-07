package com.anadolstudio.adelaide.feature.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import com.anadolstudio.adelaide.di.DI
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class SingleActivity : FragmentActivity() {

    private val viewModel: ActivityMainViewModel by viewModels { DI.viewModelsInjector.viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AdelaideEntryPoint()
        }
    }

    private fun applyStatusBarColor(useDarkTheme: Boolean) {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = !useDarkTheme
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
internal fun AdelaideEntryPoint() {
    val context = LocalContext.current

    val systemUiController = rememberSystemUiController()
    val navigator = rememberNavigator()

    AdelaideTheme(useDarkTheme = false) {
        systemUiController.setStatusBarColor(AdelaideTheme.colors.colorPrimary)
        systemUiController.setNavigationBarColor(AdelaideTheme.colors.colorPrimary)

        // todo Yes we have read text of deprecation and LocalImageLoader is exactly what we need.
        CompositionLocalProvider(
            content = { MainScreen(navigator) },
        )
    }
}
