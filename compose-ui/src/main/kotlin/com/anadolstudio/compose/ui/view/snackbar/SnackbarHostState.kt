package com.anadolstudio.compose.ui.view.snackbar

import androidx.compose.material.SnackbarData
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import androidx.compose.material.SnackbarHostState as MaterialSnackbarHostState

@Stable
class SnackbarHostState(internal val delegate: MaterialSnackbarHostState) {
    var padding: Dp by mutableStateOf(0.dp)
    var paddingSide: PaddingSide by mutableStateOf(PaddingSide.BOTTOM)

    val paddingTop: Dp
        get() = if (paddingSide.isBottom) 0.dp else padding
    val paddingBottom: Dp
        get() = if (paddingSide.isBottom) padding else 0.dp

    private val mutex by lazy { getField<Mutex>() }

    private val snackbarData by lazy { getField<MutableState<SnackbarData?>>() }

    private var currentContinuation: CancellableContinuation<*>? = null

    fun resetPadding() {
        padding = 0.dp
    }

    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        snackbarStyle: SnackbarStyle = SnackbarStyle.Default
    ): SnackbarResult {
        currentContinuation?.cancel()
        return mutex.withLock {
            val snackbarDataState = snackbarData
            try {
                suspendCancellableCoroutine { continuation ->
                    currentContinuation = continuation.takeUnless { snackbarStyle == SnackbarStyle.Error }
                    snackbarDataState.value = StyledSnackbarData(
                        message = message,
                        actionLabel = actionLabel,
                        duration = duration,
                        style = snackbarStyle,
                        continuation = continuation,
                    )
                }
            } finally {
                snackbarDataState.value = null
                currentContinuation = null
            }
        }
    }

    enum class PaddingSide {
        TOP, BOTTOM;

        val isBottom: Boolean
            get() = this == BOTTOM
    }

    @Suppress("ExplicitCollectionElementAccessMethod")
    private inline fun <reified T> getField(): T {
        return MaterialSnackbarHostState::class.java.declaredFields
            .first { T::class.java.isAssignableFrom(it.type) }
            .apply { isAccessible = true }
            .get(delegate) as T
    }
}

@Composable
fun SnackbarPaddingEffect(state: SnackbarHostState, value: Dp) {
    DisposableEffect(value) {
        state.padding = value
        onDispose {
            state.resetPadding()
        }
    }
}

@Composable
fun rememberSnackbarHostState(snackbarHostState: MaterialSnackbarHostState): SnackbarHostState {
    return remember(snackbarHostState) { SnackbarHostState(snackbarHostState) }
}
