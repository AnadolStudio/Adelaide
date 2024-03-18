package com.licard.b2b.app.library.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavOptionsBuilder
import com.anadolstudio.adelaide.event.EventsDispatcher
import com.anadolstudio.adelaide.navigation.placeholder

internal abstract class BaseNavGraphContract {

    protected abstract fun buildRoute(childRoute: String): String

    /**
     * Creates route using the given [builder].
     * If called from nested graph, its route will be added as a prefix.
     */
    protected inline fun route(builder: () -> String): String = buildRoute(builder())

    /** Navigates to the given [route] from the root of the current graph. */
    protected abstract fun EventsDispatcher.navigateFromRoot(
        route: String,
        builder: NavOptionsBuilder.() -> Unit = {},
    )

    /** Replaces placeholder of the given [argument] with the given [value]. */
    protected fun String.setArgument(argument: NamedNavArgument, value: String): String {
        return this.replace(argument.placeholder, value)
    }

    /** Adds query [argument] to string with the given [value] if it is not null. */
    protected fun String.withQueryParameter(argument: NamedNavArgument, value: Any?): String {
        return withQueryParameter(argument.name, value)
    }
}
