package com.anadolstudio.adelaide.navigation.bottomnavigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import com.anadolstudio.adelaide.feature.main.LicardNavigator
import com.anadolstudio.compose.ui.theme.AdelaideTheme

@Composable
internal fun LicardBottomTabBar(
    navigator: LicardNavigator,
    navigationTabs: List<NavigationTab>,
) {
    val navBackStackEntry by navigator.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BottomNavigationBar(
        containerColor = AdelaideTheme.colors.backgroundTabbar
    ) {
        navigationTabs.forEach { tab ->
            BottomNavigationTab(
                navigator = navigator,
                tab = tab,
                selected = currentRoute?.contains(tab.route) == true,
            )
        }
    }
}
