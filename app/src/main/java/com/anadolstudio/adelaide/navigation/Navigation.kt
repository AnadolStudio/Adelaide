package com.anadolstudio.adelaide.navigation

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavOptions
import com.anadolstudio.adelaide.feature.main.LicardNavigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * Tries to navigate using this [navigator].
 * Returns `true` if navigated successfully, otherwise `false`.
 */
internal fun LicardNavigator.navigateSafely(route: String, navOptions: NavOptions): Boolean {
    return try {
        navigate(route, navOptions)
        true
    } catch (e: IllegalArgumentException) {
        Timber.d(e)
        false
    }
}

@Composable
internal fun LicardNavigator.previousBackStackEntryAsState(): State<NavBackStackEntry?> {
    return previousBackStackEntryFlow.collectAsState(null)
}

internal val LicardNavigator.previousBackStackEntryFlow: Flow<NavBackStackEntry?>
    get() = currentBackStackEntryFlow.map { previousBackStackEntry }

internal fun ActivityResultLauncher<Intent>.launchAppSettings(packageName: String) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null),
    )
    launch(intent)
}
