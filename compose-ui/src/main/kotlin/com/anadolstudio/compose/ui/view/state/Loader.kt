package com.anadolstudio.compose.ui.view.state

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anadolstudio.compose.ui.modifier.noRippleClickable
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.LicardTheme
import com.anadolstudio.compose.ui.theme.preview.ThemePreviewParameter

@Composable
fun Loader(
    modifier: Modifier = Modifier,
    color: Color = AdelaideTheme.colors.buttonPrimary,
    strokeWidth: Dp = 4.dp,
) {
    CircularProgressIndicator(
        color = color,
        strokeWidth = strokeWidth,
        modifier = modifier.size(40.dp),
    )
}

@Composable
@Suppress("ReusedModifierInstance")
fun LoaderLayout(
    modifier: Modifier = Modifier,
    showLoader: Boolean = false,
    backgroundColor: Color = Color.Black.copy(alpha = 0.4f),
    loaderBackgroundColor: Color = AdelaideTheme.colors.backgroundPrimary
) {
    AnimatedVisibility(
        visible = showLoader,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = backgroundColor)
                .noRippleClickable { },
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.Center)
                    .background(color = loaderBackgroundColor, shape = CircleShape),
            ) {
                Loader(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoaderPreview(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    LicardTheme(useDarkMode) {
        LoaderLayout(showLoader = true)
    }
}
