package com.anadolstudio.compose.ui.view.selector

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.anadolstudio.compose.ui.modifier.noRippleClickable
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.AdelaideTypography
import com.anadolstudio.compose.ui.theme.LicardTheme
import com.anadolstudio.compose.ui.theme.preview.ThemePreviewParameter
import com.anadolstudio.compose.ui.view.HSpacer
import com.anadolstudio.compose.ui.view.VSpacer
import com.anadolstudio.compose.ui.view.WSpacer
import com.anadolstudio.compose.ui.view.text.Text

@Composable
fun SegmentPicker(
    segments: List<String>,
    onSegmentChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    fillWidth: Boolean = false,
    selectedIndex: Int = 0,
    enabled: Boolean = true
) {
    Row(modifier) {
        segments.forEachIndexed { index, segment ->
            if (fillWidth) {
                WSpacer()
            }
            Segment(
                title = segment,
                isSelected = index == selectedIndex,
                enabled = enabled,
                onSegmentClick = {
                    onSegmentChange.invoke(index)
                }
            )
            if (segments.lastIndex != index) {
                HSpacer(24.dp)
            }
            if (fillWidth) {
                WSpacer()
            }
        }
    }
}

@Composable
private fun Segment(
    title: String,
    isSelected: Boolean,
    onSegmentClick: () -> Unit,
    enabled: Boolean = true
) {
    AnimatedContent(
        targetState = isSelected,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = ""
    ) { selected ->
        val textColor = if (selected) AdelaideTheme.colors.buttonPrimary else AdelaideTheme.colors.textPrimary
        val dividerColor = if (selected) AdelaideTheme.colors.buttonPrimary else Color.Transparent
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .noRippleClickable {
                    if (enabled) {
                        onSegmentClick.invoke()
                    }
                }
        ) {
            Text(
                text = title,
                style = AdelaideTypography.textMedium18,
                color = textColor
            )
            VSpacer(8.dp)
            Divider(color = dividerColor)
        }
    }
}

@Preview
@Composable
private fun SegmentPickerPreview(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    LicardTheme(useDarkMode) {
        Column(modifier = Modifier.background(AdelaideTheme.colors.backgroundPrimary)) {
            SegmentPicker(
                segments = listOf("First", "Second", "Any"),
                onSegmentChange = {},
            )
            VSpacer(4.dp)
            Divider(color = AdelaideTheme.colors.divider)
            VSpacer(4.dp)
            SegmentPicker(
                segments = listOf("First", "Second", "Any"),
                onSegmentChange = {},
                fillWidth = true,
            )
        }
    }
}
