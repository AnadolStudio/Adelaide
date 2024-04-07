package com.anadolstudio.compose.ui.view.selector

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.AdelaideTypography
import com.anadolstudio.compose.ui.theme.Dimension
import com.anadolstudio.compose.ui.theme.Shapes
import com.anadolstudio.compose.ui.theme.largeBlock
import com.anadolstudio.compose.ui.theme.preview.ThemePreviewParameter
import com.anadolstudio.compose.ui.view.VSpacer
import com.anadolstudio.compose.ui.view.WSpacer
import com.anadolstudio.compose.ui.view.text.Text

@Composable
fun SegmentPicker(
    height: Dp,
    segments: List<String>,
    onSegmentChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    fillWidth: Boolean = false,
    selectedIndex: Int = 0,
    enabled: Boolean = true
) {
    Row(modifier) {
        segments.forEachIndexed { index, segment ->
            Segment(
                height = height,
                title = segment,
                isSelected = index == selectedIndex,
                fraction = 1F / segments.size,
                enabled = enabled && selectedIndex != index,
                fillWidth = fillWidth,
                onSegmentClick = {
                    onSegmentChange.invoke(index)
                }
            )
        }
    }
}

@Composable
private fun RowScope.Segment(
    height: Dp,
    title: String,
    isSelected: Boolean,
    fraction: Float,
    onSegmentClick: () -> Unit,
    fillWidth: Boolean = false,
    enabled: Boolean = true
) {
    val modifier = if (fillWidth) Modifier.weight(fraction) else Modifier

    AnimatedContent(
        modifier = modifier,
        targetState = isSelected,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "Segment"
    ) { selected ->
        val style = if (selected) AdelaideTypography.textBold18 else AdelaideTypography.textLight18
        val thickness = if (selected) 2.dp else 1.dp
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .height(height)
                .clip(Shapes.largeBlock)
                .clickable(enabled) { onSegmentClick.invoke() }
                .padding(horizontal = Dimension.mainMargin)
        ) {
            WSpacer()
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                text = title,
                style = style,
                color = AdelaideTheme.colors.textPrimary
            )
            VSpacer(2.dp)
            Divider(
                color = AdelaideTheme.colors.colorAccent,
                thickness = thickness
            )
            WSpacer()
        }
    }
}

@Preview
@Composable
private fun SegmentPickerPreview(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    AdelaideTheme(useDarkMode) {
        Column(modifier = Modifier.background(AdelaideTheme.colors.colorPrimary)) {
            SegmentPicker(
                height = 36.dp,
                segments = listOf("First", "Second", "Any"),
                onSegmentChange = {},
            )
            VSpacer(4.dp)
            Divider(color = AdelaideTheme.colors.divider)
            VSpacer(4.dp)
            SegmentPicker(
                height = 52.dp,
                segments = listOf("First", "Second", "Any"),
                onSegmentChange = {},
                fillWidth = true,
            )
            VSpacer(4.dp)
        }
    }
}
