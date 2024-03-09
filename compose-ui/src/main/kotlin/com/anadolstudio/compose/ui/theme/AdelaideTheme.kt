package com.anadolstudio.compose.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.anadolstudio.compose.ui.theme.color.AdelaideThemeColors

object AdelaideTheme {
    val colors: AdelaideThemeColors
        @Composable
        @ReadOnlyComposable
        get() = LocalLicardColors.current
}
