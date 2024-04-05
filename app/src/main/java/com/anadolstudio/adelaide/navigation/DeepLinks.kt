package com.anadolstudio.adelaide.navigation

/** Adds query argument with the given [name] associated with the given [value] to string if it is not `null`. */
internal fun String.withQueryParameter(name: String, value: Any?): String {
    return when {
        value == null -> this
        '?' in this -> "$this&$name=$value"
        else -> "$this?$name=$value"
    }
}
