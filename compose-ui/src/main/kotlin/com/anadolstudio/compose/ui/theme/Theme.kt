package com.anadolstudio.compose.ui.theme

import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.anadolstudio.compose.ui.theme.color.AdelaideColorDarkPalette
import com.anadolstudio.compose.ui.theme.color.AdelaideColorLightPalette
import com.anadolstudio.compose.ui.theme.color.AdelaideThemeColors

@Composable
fun AdelaideTheme(
    useDarkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorPalette = remember(useDarkTheme) { if (useDarkTheme) AdelaideColorDarkPalette else AdelaideColorLightPalette }
    MaterialTheme(
        typography = MaterialTypography,
        shapes = Shapes,
    ) {
        CompositionLocalProvider(
            LocalLicardColors provides colorPalette,
            LocalTextStyle provides AdelaideTypography.textBook18,
            LocalContentColor provides colorPalette.textPrimary,
            content = content,
        )
    }
}

internal val LocalLicardColors = staticCompositionLocalOf<AdelaideThemeColors> {
    error("No LicardThemeColors provided")
}
