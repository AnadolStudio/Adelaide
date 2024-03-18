@file:Suppress("TooManyFunctions")

package com.anadolstudio.adelaide.navigation

import android.os.Build
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val jsonDefaultsFormat = Json { encodeDefaults = true }

/** Returns placeholder that should be used in route in place of argument value. */
internal val NamedNavArgument.placeholder: String
    get() = "{$name}"

internal fun stringArgument(name: String) = navArgument(name) { type = NavType.StringType }

internal fun optionalStringArgument(name: String) = navArgument(name) {
    type = NavType.StringType
    nullable = true
}

internal inline fun <reified E : Enum<*>> enumArgument(name: String) = navArgument(name) {
    type = NavType.EnumType(E::class.java)
}

@Suppress("StringLiteralDuplication")
internal fun NavBackStackEntry.requireStringArgument(argument: NamedNavArgument): String {
    return requireArgument(value = getStringArgument(argument), name = argument.name)
}

internal inline fun <reified T> objectToString(value: T): String {
    return jsonDefaultsFormat.encodeToString(value)
}

internal inline fun <reified T> NavBackStackEntry.requireObject(argument: NamedNavArgument): T {
    return jsonDefaultsFormat.decodeFromString(requireStringArgument(argument))
}

internal inline fun <reified T> NavBackStackEntry.getObject(argument: NamedNavArgument): T? {
    return getStringArgument(argument)?.let { jsonDefaultsFormat.decodeFromString(it) }
}

internal fun NavBackStackEntry.getStringArgument(argument: NamedNavArgument): String? {
    return arguments?.getString(argument.name)
}

internal inline fun <reified E : Enum<E>> NavBackStackEntry.requireEnumArgument(argument: NamedNavArgument): E {
    return requireArgument(value = getEnumArgument<E>(argument), name = argument.name)
}

internal inline fun <reified E : Enum<E>> NavBackStackEntry.getEnumArgument(argument: NamedNavArgument): E? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arguments?.getSerializable(argument.name, E::class.java)
    } else {
        @Suppress("DEPRECATION") arguments?.getSerializable(argument.name) as? E
    }
}

internal fun longArgument(name: String) = navArgument(name) { type = NavType.LongType }

internal fun NavBackStackEntry.getLongArgument(argument: NamedNavArgument): Long? {
    return arguments?.getLong(argument.name)
}

internal fun NavBackStackEntry.requireLongArgument(argument: NamedNavArgument): Long {
    return requireArgument(value = getLongArgument(argument), name = argument.name)
}

internal fun intArgument(name: String) = navArgument(name) { type = NavType.IntType }

internal fun NavBackStackEntry.getIntArgument(argument: NamedNavArgument): Int? {
    return arguments?.getInt(argument.name)
}

internal fun NavBackStackEntry.requireIntArgument(argument: NamedNavArgument): Int {
    return requireArgument(value = getIntArgument(argument), name = argument.name)
}

internal fun booleanArgument(name: String) = navArgument(name) { type = NavType.BoolType }

internal fun NavBackStackEntry.getBooleanArgument(argument: NamedNavArgument): Boolean? {
    return arguments?.getBoolean(argument.name)
}

internal fun NavBackStackEntry.requireBooleanArgument(argument: NamedNavArgument): Boolean {
    return requireArgument(value = getBooleanArgument(argument), name = argument.name)
}

internal fun floatArgument(name: String) = navArgument(name) { type = NavType.FloatType }

internal fun NavBackStackEntry.getFloatArgument(argument: NamedNavArgument): Float? {
    return arguments?.getFloat(argument.name)
}

internal fun NavBackStackEntry.requireFloatArgument(argument: NamedNavArgument): Float {
    return requireArgument(value = getFloatArgument(argument), name = argument.name)
}

private fun <T : Any> NavBackStackEntry.requireArgument(value: T?, name: String): T {
    return requireNotNull(value) { "Argument '$name' is required for destination $destination." }
}
