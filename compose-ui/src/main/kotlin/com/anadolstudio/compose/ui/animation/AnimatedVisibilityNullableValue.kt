package com.anadolstudio.compose.ui.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Suppress("unused") // Used to override animation defaults for ColumnScope
@Composable
public fun <T : Any> ColumnScope.AnimatedVisibilityNullableValue(
    value: T?,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + expandVertically(),
    exit: ExitTransition = fadeOut() + shrinkVertically(),
    content: @Composable AnimatedVisibilityScope.(T) -> Unit,
) {
    AnimatedVisibilityNullableValueImpl(
        value = value,
        modifier = modifier,
        enter = enter,
        exit = exit,
        content = content,
    )
}

@Composable
public fun <T : Any> AnimatedVisibilityNullableValue(
    value: T?,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
    content: @Composable AnimatedVisibilityScope.(T) -> Unit,
) {
    AnimatedVisibilityNullableValueImpl(
        value = value,
        modifier = modifier,
        enter = enter,
        exit = exit,
        content = content,
    )
}

@Composable
private fun <T : Any> AnimatedVisibilityNullableValueImpl(
    value: T?,
    enter: EnterTransition,
    exit: ExitTransition,
    content: @Composable AnimatedVisibilityScope.(T) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Remember value to animate the case when value become null.
    // In this case we should set visible to `false`, but render content with last non-null value.
    var lastValue by remember { mutableStateOf(value) }
    if (value != null) lastValue = value

    AnimatedVisibility(
        visible = value != null,
        modifier = modifier,
        enter = enter,
        exit = exit,
        content = { lastValue?.let { content(it) } },
    )
}
