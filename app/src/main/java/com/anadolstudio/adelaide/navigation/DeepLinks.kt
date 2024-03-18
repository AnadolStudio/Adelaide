package com.licard.b2b.app.library.navigation

import androidx.navigation.NavDeepLink

internal fun navDeepLinks(vararg uris: String): List<NavDeepLink> = uris.map(::navDeepLink)

internal fun navDeepLink(uri: String): NavDeepLink = navDeepLink { uriPattern = uri }

/** Adds query argument with the given [name] associated with the given [value] to string if it is not `null`. */
internal fun String.withQueryParameter(name: String, value: Any?): String {
    return when {
        value == null -> this
        '?' in this -> "$this&$name=$value"
        else -> "$this?$name=$value"
    }
}
