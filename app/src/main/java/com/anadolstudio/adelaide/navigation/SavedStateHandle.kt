package com.anadolstudio.adelaide.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.anadolstudio.adelaide.feature.main.NavigationController

/** Observes screen result. Designed to be used with `navigateUp` with result.
 * @param onValue Using method reference */
@Composable
internal fun <T : Any> ObserveResultValue(navigator: NavigationController, key: String, onValue: (T) -> Unit) {
    val savedStateHandle = navigator.currentBackStackEntry?.savedStateHandle ?: return
    val result by savedStateHandle.getLiveData<T?>(key).observeAsState()

    LaunchedEffect(result, onValue) {
        val value = savedStateHandle.get<T?>(key)
        if (value != null) {
            savedStateHandle[key] = null
            onValue(value)
        }
    }
}
