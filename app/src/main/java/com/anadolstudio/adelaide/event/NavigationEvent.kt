package com.anadolstudio.adelaide.event

import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import com.anadolstudio.adelaide.feature.main.LicardNavigator
import com.anadolstudio.adelaide.navigation.bottomnavigation.bottomNavigate
import com.anadolstudio.adelaide.navigation.navigateSafely
import timber.log.Timber

internal interface NavigationEvent : Event {

    fun navigate(navigator: LicardNavigator): Boolean

    class ToRoute(
        private val route: String,
        private val navOptions: NavOptions,
    ) : NavigationEvent {

        constructor(route: String, builder: NavOptionsBuilder.() -> Unit = {}) : this(
            route,
            navOptions(builder)
        )

        override fun navigate(navigator: LicardNavigator): Boolean =
            navigator.navigateSafely(route, navOptions)
    }

    class Up(private val results: Map<String, Any?>) : NavigationEvent {

        override fun navigate(navigator: LicardNavigator): Boolean {
            val previousBackStackEntry = navigator.previousBackStackEntry
            return if (previousBackStackEntry != null) {
                if (results.isNotEmpty()) {
                    results.forEach { (key, value) ->
                        previousBackStackEntry.savedStateHandle[key] = value
                    }
                }
                navigator.navigateUp()
            } else {
                navigator.finish()
            }
        }
    }

    class UpTo(
        private val route: String,
        private val inclusive: Boolean,
        private val results: Map<String, Any?>,
    ) : NavigationEvent {
        override fun navigate(navigator: LicardNavigator): Boolean {
            try {
                val backStackEntry = navigator.getBackStackEntry(route)
                if (results.isNotEmpty()) {
                    results.forEach { (key, value) ->
                        backStackEntry.savedStateHandle[key] = value
                    }
                }
            } catch (exception: IllegalArgumentException) {
                Timber.e(exception)
            }

            return navigator.popBackStack(route, inclusive)
        }
    }

    class ToTab(
        private val route: String,
    ) : NavigationEvent {

        override fun navigate(navigator: LicardNavigator): Boolean {
            return try {
                navigator.bottomNavigate(route)
                true
            } catch (e: IllegalArgumentException) {
                Timber.d(e)
                false
            }
        }
    }

    class Finish : NavigationEvent {
        override fun navigate(navigator: LicardNavigator): Boolean {
            return navigator.finish()
        }
    }
}

internal fun EventsDispatcher.navigateTo(
    route: String,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    offerEvent(NavigationEvent.ToRoute(route, builder))
}

internal fun EventsDispatcher.navigateUp(vararg results: Pair<String, Any?>) {
    offerEvent(NavigationEvent.Up(results.toMap()))
}

internal fun EventsDispatcher.navigateToTab(route: String) {
    offerEvent(NavigationEvent.ToTab(route))
}

internal fun EventsDispatcher.navigateUpTo(
    route: String,
    vararg results: Pair<String, Any?>,
    inclusive: Boolean = false,
) {
    offerEvent(NavigationEvent.UpTo(route, inclusive, results.toMap()))
}

internal fun EventsDispatcher.finishLicardFlow() {
    offerEvent(NavigationEvent.Finish())
}
