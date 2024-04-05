package com.anadolstudio.compose.ui.view.snackbar

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

fun Modifier.snackbarAnchor(state: SnackbarHostState): Modifier = composed {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    DisposableEffect(state) {
        onDispose {
            state.resetPadding()
        }
    }

    Modifier.onGloballyPositioned { coordinates ->
        val bounds = coordinates.boundsInRoot()

        state.padding = if (state.paddingSide.isBottom) {
            if (bounds.top == 0f) return@onGloballyPositioned
            configuration.screenHeightDp.dp - bounds.top.pxToDp(density)
        } else {
            if (bounds.bottom == 0f) return@onGloballyPositioned
            bounds.bottom.pxToDp(density)
        }
    }
}

fun Modifier.snackbarPadding(state: SnackbarHostState): Modifier {
    return this.padding(top = state.paddingTop, bottom = state.paddingBottom)
}

fun Modifier.animatedSnackbarPadding(state: SnackbarHostState): Modifier = composed {
    val topPad by animateDpAsState(state.paddingTop)
    val bottomPad by animateDpAsState(state.paddingBottom)
    Modifier.padding(top = topPad, bottom = bottomPad)
}

private fun Float.pxToDp(density: Density) = with(density) {
    this@pxToDp.toDp()
}
