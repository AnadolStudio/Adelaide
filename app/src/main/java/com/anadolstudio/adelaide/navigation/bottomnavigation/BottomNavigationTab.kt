package com.anadolstudio.adelaide.navigation.bottomnavigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import com.anadolstudio.adelaide.feature.main.LicardNavigator
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.AdelaideTypography

@Composable
internal fun RowScope.BottomNavigationTab(
    navigator: LicardNavigator,
    tab: NavigationTab,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    NavigationBarItem(
        icon = { Icon(painter = tab.icon, contentDescription = tab.name.get(context)) },
        label = {
            Text(
                text = tab.name.get(context),
                style = AdelaideTypography.captionMedium12,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        alwaysShowLabel = true,
        selected = selected,
        enabled = !selected,
        onClick = { navigator.navigate(tab) },
        modifier = modifier,
        colors = NavigationBarColors(),
    )
}

@Composable
private fun NavigationBarColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = AdelaideTheme.colors.buttonSecondaryText,
    selectedTextColor = AdelaideTheme.colors.buttonSecondaryText,
    disabledTextColor = AdelaideTheme.colors.buttonSecondaryText,
    disabledIconColor = AdelaideTheme.colors.buttonSecondaryText,
    indicatorColor = AdelaideTheme.colors.backgroundTabbar,
    unselectedIconColor = AdelaideTheme.colors.unselectedTextTabbar,
    unselectedTextColor = AdelaideTheme.colors.unselectedTextTabbar,
)

private fun LicardNavigator.navigate(tab: NavigationTab) {
    bottomNavigate(tab.route)
}

fun LicardNavigator.bottomNavigate(route: String) {
    navigate(route) {
        graph.startDestinationRoute?.let { route ->
            popUpTo("$route/start") {
                saveState = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }
}
