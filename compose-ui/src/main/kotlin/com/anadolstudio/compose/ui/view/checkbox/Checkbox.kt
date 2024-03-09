package com.anadolstudio.compose.ui.view.checkbox

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.anadolstudio.compose.ui.drawable.LicardIcon
import com.anadolstudio.compose.ui.theme.AdelaideTypography
import com.anadolstudio.compose.ui.theme.LicardDimension
import com.anadolstudio.compose.ui.theme.LicardTheme
import com.anadolstudio.compose.ui.theme.preview.ThemePreviewParameter
import com.anadolstudio.compose.ui.view.text.Text

@Composable
fun Checkbox(
    isEnabled: Boolean,
    text: String,
    onClick: () -> Unit,
    isEnabledIcon: Painter = LicardIcon.SquareCheckboxEnabled,
    isDisabledIcon: Painter = LicardIcon.SquareCheckboxDisabled,
) {
    BaseCheckbox(
        isEnabled = isEnabled,
        text = {
            Text(
                text = text,
                style = AdelaideTypography.captionBook16,
            )
        },
        onClick = onClick,
        isEnabledIcon = isEnabledIcon,
        isDisabledIcon = isDisabledIcon,
    )
}

@Composable
fun Checkbox(
    isEnabled: Boolean,
    text: AnnotatedString,
    onClick: () -> Unit,
    isEnabledIcon: Painter = LicardIcon.SquareCheckboxEnabled,
    isDisabledIcon: Painter = LicardIcon.SquareCheckboxDisabled,
) {
    BaseCheckbox(
        isEnabled = isEnabled,
        text = {
            Text(
                text = text,
                style = AdelaideTypography.captionBook16,
            )
        },
        onClick = onClick,
        isEnabledIcon = isEnabledIcon,
        isDisabledIcon = isDisabledIcon,
    )
}

@Composable
private fun BaseCheckbox(
    isEnabled: Boolean,
    text: @Composable () -> Unit,
    isEnabledIcon: Painter,
    isDisabledIcon: Painter,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null,
            )
            .fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = LicardDimension.layoutHorizontalMargin)
        ) {
            val icon = if (isEnabled) isEnabledIcon else isDisabledIcon

            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.indication(
                    interactionSource = interactionSource,
                    indication = rememberRipple(bounded = false, radius = RippleRadius),
                )
            )

            text.invoke()
        }
    }
}

private val RippleRadius = 20.dp

@Preview
@Composable
private fun Preview(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    LicardTheme(useDarkMode) {
        var checkboxState by remember { mutableStateOf(true) }
        Box {
            Checkbox(
                isEnabled = checkboxState,
                text = "Checkbox",
                onClick = { checkboxState = !checkboxState }
            )
        }
    }
}
