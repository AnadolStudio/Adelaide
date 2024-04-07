package com.anadolstudio.compose.ui.theme.color

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
data class AdelaideThemeColors(
    val isLight: Boolean,
    val colorPrimary: Color,
    val colorSecondary: Color,
    val colorAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val buttonPrimaryRipple: Color,
    val divider: Color,
    val template: Color = AdelaideColor.template,
    val colorOverlay: Color = AdelaideColor.colorOverlay,
    val shimmerGradient: GradientColor = GradientColor(
        colorStart = AdelaideColor.shimmersStart,
        colorCenter = AdelaideColor.shimmersCenter,
        colorEnd = AdelaideColor.shimmersEnd,
    ),
)

data class GradientColor(
    val colorStart: Color,
    val colorCenter: Color,
    val colorEnd: Color,
)
