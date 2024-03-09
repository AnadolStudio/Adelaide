package com.anadolstudio.compose.ui.view.state

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.LicardTheme
import com.anadolstudio.compose.ui.theme.largeShimmer
import com.anadolstudio.compose.ui.theme.micro
import com.anadolstudio.compose.ui.theme.preview.ThemePreviewParameter
import com.anadolstudio.compose.ui.view.VSpacer

@Composable
fun ShimmerItem(
    size: DpSize,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.micro,
) {
    Box(
        modifier = modifier
            .size(size.width, size.height)
            .background(getShimmerBrush(), shape),
    )
}

@Composable
fun LargeShimmerItem(
    height: Dp,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.largeShimmer,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(getShimmerBrush(), shape),
    )
}

@Composable
fun CircleShimmerItem(
    shape: Shape = CircleShape,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(getShimmerBrush(), shape),
    )
}

@Composable
fun CheckBoxShimmerItem() {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(getShimmerBrush(), CircleShape)
            .padding(2.dp)
            .background(AdelaideTheme.colors.backgroundPrimary, CircleShape),
    )
}

@Suppress("MagicNumber")
@Composable
private fun getShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        initialValue = 0F,
        targetValue = 1000F,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    return Brush.linearGradient(
        colors = getShimmerColors(),
        start = Offset(10F, 10F),
        end = Offset(translateAnim, translateAnim)
    )
}

@Composable
private fun getShimmerColors() = listOf(
    AdelaideTheme.colors.shimmerGradient.colorStart,
    AdelaideTheme.colors.shimmerGradient.colorCenter,
    AdelaideTheme.colors.shimmerGradient.colorEnd,
)

@Preview
@Composable
private fun Preview(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    LicardTheme(useDarkMode) {
        Column(Modifier.background(AdelaideTheme.colors.backgroundPrimary)) {
            ShimmerItem(size = DpSize(100.dp, 32.dp))
            VSpacer(16.dp)
            LargeShimmerItem(height = 40.dp)
            VSpacer(size = 16.dp)
            CircleShimmerItem()
            VSpacer(size = 16.dp)
            CheckBoxShimmerItem()
        }
    }
}
