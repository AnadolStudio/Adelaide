package com.anadolstudio.adelaide.lce

import com.anadolstudio.adelaide.lce.LceState.Content
import com.anadolstudio.adelaide.lce.LceState.Error
import com.anadolstudio.adelaide.lce.LceState.Loading
import kotlinx.coroutines.flow.flowOf

/**
 * [Lce] no content.
 * Used to describe the [Loading], [Content] and [Error] states.
 * @see Lce.ignoreContent
 */
internal sealed interface LceState {

    val isLoading: Boolean
    val isContent: Boolean
    val isError: Boolean

    fun errorOrNull(): Throwable?

    sealed interface Loading : LceState

    sealed interface Content : LceState

    sealed interface Error : LceState {
        val error: Throwable
    }

    companion object {
        val Loading: Loading = Lce.Loading
        val Content: Content = Lce.Content(Unit)

        @Suppress("FunctionNaming")
        fun Error(error: Throwable): Error = Lce.Error(error)
    }
}

/** Turns [Lce] into [LceState] ignoring the contents of the [Lce.Content] state. */
internal fun Lce<*>.ignoreContent(): LceState {
    return when (this) {
        is Lce.Loading -> this
        is Lce.Error -> this
        is Lce.Content -> Lce.Content(Unit)
    }
}

/** Returns a different value depending on the [LceState] state. */
internal inline fun <R> LceState.fold(onLoading: () -> R, onError: (Throwable) -> R, onContent: () -> R): R {
    return when (this) {
        is Loading -> onLoading()
        is Error -> onError(error)
        is Content -> onContent()
    }
}

internal inline fun <T> LceState.fillContent(contentProducer: () -> T): Lce<T> {
    return fold(
        onLoading = { Lce.Loading },
        onError = { Lce.Error(it) },
        onContent = { Lce.of(contentProducer.invoke()) }
    )
}

internal inline fun <T> LceState.flatMap(contentFlowProducer: () -> LceFlow<T>): LceFlow<T> {
    return fold(
        onLoading = { flowOf(Lce.Loading) },
        onError = { flowOf(Lce.Error(it)) },
        onContent = { contentFlowProducer.invoke() }
    )
}
