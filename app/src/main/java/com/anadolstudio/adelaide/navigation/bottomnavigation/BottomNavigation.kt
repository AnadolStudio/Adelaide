package com.anadolstudio.adelaide.navigation.bottomnavigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.currentBackStackEntryAsState
import com.anadolstudio.adelaide.feature.main.LicardNavigator

@Composable
internal fun BottomNavigation(
    navigator: LicardNavigator,
    noBottomNavigationRoutes: Set<String>,
    navigationTabs: List<NavigationTab>,
) {
    AnimatedVisibility(
        visible = navigator.isBottomNavigationVisible(noBottomNavigationRoutes),
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        LicardBottomTabBar(
            navigator = navigator,
            navigationTabs = navigationTabs,
        )
    }
}

@Composable
private fun LicardNavigator.isBottomNavigationVisible(noBottomNavigationRoutes: Set<String>): Boolean {
    val currentBackStackEntry by currentBackStackEntryAsState()
    val destination = currentBackStackEntry?.destination

    var isVisible by remember { mutableStateOf(false) }

    isVisible = destination != null && destination.route !in noBottomNavigationRoutes

    return isVisible
}
