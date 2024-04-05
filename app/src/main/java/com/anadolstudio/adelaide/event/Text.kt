package com.anadolstudio.adelaide.event

import android.content.Context
import android.content.res.Resources
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

/**
 * Wrapper to make it possible to work with plain [String] and [StringRes] in the same way.
 *
 * ```
 *  // in some place where we can't access Context
 *  val errorMessage = exception.message?.let(Text::Plain) ?: Text.Resource(R.string.unknown_error)
 *  showMessage(errorMessage)
 *
 *  // in Activity, Fragment or View
 *  val messageText = getString(message)
 * ```
 */
sealed class Text {

    /** Retrieves [String] using given [context]. */
    abstract fun get(context: Context): String

    /** Plain string. */
    data class Plain(val string: String) : Text() {
        override fun get(context: Context): String = string
    }

    /** String resource, requires [Context] to get [String]. */
    data class Resource(@StringRes val resourceId: Int) : Text() {
        override fun get(context: Context): String = context.getString(resourceId)
    }

    /** String resource with argument, requires [Context] to get [String]. */
    class ResourceWithArg(@StringRes val resourceId: Int, private val arg: String) : Text() {
        override fun get(context: Context): String = context.getString(resourceId, arg)
    }
}

/**
 * Unwraps and returns a string for the given [text].
 * @see Text
 */
inline fun Context.getString(text: Text): String = text.get(this)

/**
 * Unwraps and returns a string for the given [text].
 * @see Text
 */
inline fun Fragment.getString(text: Text): String = requireContext().getString(text)

/**
 * Unwraps and returns a string for the given [text].
 * @see Text
 */
inline fun View.getString(text: Text): String = context.getString(text)

internal fun Text?.getOrEmpty(context: Context): String = this?.get(context).orEmpty()

internal fun Text?.orEmptyText(): Text = Text.Plain("")

internal fun Text.get(resources: Resources): String {
    return when (this) {
        is Text.Plain -> string
        is Text.Resource -> resources.getString(resourceId)
        is Text.ResourceWithArg -> resources.getString(resourceId)
    }
}
