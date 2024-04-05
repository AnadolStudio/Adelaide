package com.anadolstudio.compose.ui.theme

import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape

@Suppress("unused")
val ModalBottomSheetDefaults.shape: Shape
    @Composable get() = MaterialTheme.shapes.large.copy(
        bottomStart = ZeroCornerSize,
        bottomEnd = ZeroCornerSize,
        topEnd = ZeroCornerSize,
        topStart = ZeroCornerSize,
    )
