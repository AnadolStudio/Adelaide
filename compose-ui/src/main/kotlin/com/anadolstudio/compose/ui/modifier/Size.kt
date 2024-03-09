package com.anadolstudio.compose.ui.modifier

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.IntSize

/** Remembers minimal size if the given [predicate] returns `true`. */
fun Modifier.rememberMinSize(predicate: (old: IntSize, new: IntSize) -> Boolean): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "rememberMinSize"
    }
) {
    var contentSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    Modifier
        .onGloballyPositioned { coordinates ->
            val size = coordinates.size
            if (predicate(contentSize, size)) contentSize = size
        }
        .sizeIn(
            minHeight = with(density) { contentSize.height.toDp() },
            minWidth = with(density) { contentSize.width.toDp() },
        )
}
