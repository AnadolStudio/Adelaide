package com.anadolstudio.compose.ui.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.surface(
    backgroundColor: Color,
    shape: Shape,
    elevation: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
): Modifier {
    return this
        .shadow(elevation, shape)
        .background(color = backgroundColor, shape = shape)
        .clip(shape)
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
}
