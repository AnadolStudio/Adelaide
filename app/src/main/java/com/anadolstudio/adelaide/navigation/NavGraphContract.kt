package com.anadolstudio.adelaide.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import com.anadolstudio.adelaide.di.viewmodel.daggerViewModel
import com.anadolstudio.adelaide.event.EventsDispatcher
import com.anadolstudio.adelaide.event.navigateTo
import com.anadolstudio.adelaide.feature.main.NavigationController
import com.licard.b2b.app.library.navigation.BaseNavGraphContract
import androidx.navigation.compose.navigation as originalNavigation

internal open class NavGraphContract : BaseNavGraphContract() {

    var route: String = ""
        private set
    var startDestination: String = ""
        private set

    protected fun NavGraphBuilder.navigation(
        route: String,
        startDestination: String = "$route/start",
        arguments: List<NamedNavArgument> = emptyList(),
        deepLinks: List<NavDeepLink> = emptyList(),
        builder: NavGraphBuilder.() -> Unit,
    ) {
        this@NavGraphContract.route = route
        this@NavGraphContract.startDestination = startDestination
        originalNavigation(
            route = route,
            startDestination = startDestination,
            arguments = arguments,
            deepLinks = deepLinks,
            builder = builder,
        )
    }

    @Composable
    protected inline fun <reified T : ViewModel> graphViewModel(
        navigator: NavigationController,
        backStackEntry: NavBackStackEntry,
    ): T {
        val entry = remember(backStackEntry) { navigator.getBackStackEntry(route) }
        return daggerViewModel(entry)
    }

    @Composable
    protected inline fun <reified T : ViewModel> graphFlowEventHandler(
        navigator: NavigationController,
        backStackEntry: NavBackStackEntry,
        factory: ViewModelProvider.Factory,
    ): T {
        val entry = remember(backStackEntry) { navigator.getBackStackEntry(route) }
        return viewModel(factory = factory, viewModelStoreOwner = entry)
    }

    @Composable
    protected inline fun <T> rememberFlowParams(
        navigator: NavigationController,
        backStackEntry: NavBackStackEntry,
        crossinline transform: @DisallowComposableCalls (NavBackStackEntry) -> T,
    ): T {
        return remember(backStackEntry) { transform(navigator.getBackStackEntry(route)) }
    }

    override fun buildRoute(childRoute: String): String = "$route/$childRoute"

    override fun EventsDispatcher.navigateFromRoot(
        route: String,
        builder: NavOptionsBuilder.() -> Unit
    ) {
        navigateTo(route) {
            popUpTo(this@NavGraphContract.route)
            builder()
        }
    }
}
