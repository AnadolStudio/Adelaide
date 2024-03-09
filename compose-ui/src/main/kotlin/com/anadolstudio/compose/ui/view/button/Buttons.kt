package com.anadolstudio.compose.ui.view.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anadolstudio.compose.ui.drawable.LicardIcon
import com.anadolstudio.compose.ui.modifier.consumeTouches
import com.anadolstudio.compose.ui.modifier.rememberMinSize
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.AdelaideTypography
import com.anadolstudio.compose.ui.theme.LicardTheme
import com.anadolstudio.compose.ui.theme.micro
import com.anadolstudio.compose.ui.theme.preview.ThemePreviewParameter
import com.anadolstudio.compose.ui.theme.tiny
import com.anadolstudio.compose.ui.view.HSpacer
import com.anadolstudio.compose.ui.view.VSpacer
import androidx.compose.material.TextButton as MaterialTextButton

@Composable
fun PrimaryButtonLarge(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: LicardButtonColors = LicardButtonColors.primaryButtonColors(),
    maxLines: Int = 1,
    shape: Shape = MaterialTheme.shapes.tiny,
    contentPadding: PaddingValues = LargeButtonContentPadding,
    loading: Boolean = false,
    icon: Painter? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = LargeButtonHeight)
            .consumeTouches(loading)
            .rememberMinSize { _, _ -> !loading }
            .indication(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = AdelaideTheme.colors.buttonPrimaryRipple,
                    radius = 10.dp
                )
            ),
        enabled = enabled,
        elevation = null,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = colors.contentColor(enabled).value,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                    )
                    HSpacer(8.dp)
                }
                com.anadolstudio.compose.ui.view.text.Text(
                    text = text,
                    maxLines = maxLines,
                    textAlign = TextAlign.Center,
                    style = AdelaideTypography.textMedium18,
                )
            }
        }
    }
}

@Composable
fun OutlineButtonLarge(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: LicardButtonColors = LicardButtonColors.outlineButtonColors(),
    maxLines: Int = 1,
    shape: Shape = MaterialTheme.shapes.tiny,
    contentPadding: PaddingValues = LargeButtonContentPadding,
    loading: Boolean = false,
    icon: Painter? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = LargeButtonHeight)
            .consumeTouches(loading)
            .rememberMinSize { _, _ -> !loading },
        enabled = enabled,
        elevation = null,
        shape = shape,
        colors = colors,
        border = BorderStroke(width = 1.dp, color = colors.contentColor(enabled).value),
        contentPadding = contentPadding,
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = colors.contentColor(enabled).value,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        tint = colors.contentColor(enabled).value
                    )
                    HSpacer(8.dp)
                }
                com.anadolstudio.compose.ui.view.text.Text(
                    text = text,
                    maxLines = maxLines,
                    textAlign = TextAlign.Center,
                    style = AdelaideTypography.textMedium18,
                )
            }
        }
    }
}

@Composable
fun FloatTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: Painter? = null,
    paddingValues: PaddingValues = PaddingValues(bottom = 20.dp, end = 16.dp),
) {
    TextButton(
        modifier = modifier.padding(paddingValues),
        text = text,
        onClick = onClick,
        colors = LicardButtonColors.primaryButtonColors(),
        icon = icon,
        iconModifier = Modifier.size(24.dp),
        iconHorizontalSpace = 10.dp,
        shape = RoundedCornerShape(40.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
        enabled = enabled,
    )
}

@Suppress("LongParameterList")
@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: LicardButtonColors = LicardButtonColors.textButtonColors(),
    shape: Shape = MaterialTheme.shapes.micro,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    textStyle: TextStyle = AdelaideTypography.textMedium18,
    loading: Boolean = false,
    icon: Painter? = null,
    iconModifier: Modifier? = null,
    iconHorizontalSpace: Dp = 8.dp,
) {
    MaterialTextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        elevation = null,
        shape = shape,
        colors = colors,
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = colors.contentColor(enabled).value,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(contentPadding),
            ) {
                if (icon != null) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = iconModifier ?: Modifier
                    )
                    HSpacer(iconHorizontalSpace)
                }
                com.anadolstudio.compose.ui.view.text.Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    style = textStyle,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
@Suppress("LongMethod", "StringLiteralDuplication")
private fun ButtonsPreview(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    LicardTheme(useDarkMode) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(AdelaideTheme.colors.backgroundPrimary)
        ) {
            PrimaryButtonLarge(
                text = "PrimaryButtonLarge",
                onClick = {},
            )
            VSpacer(8.dp)
            PrimaryButtonLarge(
                text = "PrimaryButtonLarge",
                onClick = {},
                loading = true,
            )
            VSpacer(8.dp)
            PrimaryButtonLarge(
                text = "PrimaryButtonLarge",
                onClick = {},
                icon = LicardIcon.Plus
            )
            VSpacer(8.dp)
            PrimaryButtonLarge(
                text = "PrimaryButtonLarge",
                onClick = {},
                enabled = false
            )
            VSpacer(8.dp)
            OutlineButtonLarge(
                text = "OutlineButtonLarge",
                onClick = {},
            )
            VSpacer(8.dp)
            OutlineButtonLarge(
                text = "OutlineButtonLarge",
                onClick = {},
                loading = true,
            )
            VSpacer(8.dp)
            OutlineButtonLarge(
                text = "OutlineButtonLarge",
                onClick = {},
                icon = LicardIcon.Plus,
            )
            VSpacer(8.dp)
            TextButton(
                text = "TextButton",
                onClick = {},
            )
            VSpacer(8.dp)
            TextButton(
                text = "TextButton",
                onClick = {},
                loading = true
            )
            VSpacer(8.dp)
            TextButton(
                text = "TextButton",
                onClick = {},
                icon = LicardIcon.Plus,
            )
            VSpacer(8.dp)
            FloatTextButton(
                text = "FloatTextButton",
                onClick = {},
                icon = LicardIcon.Plus,
                paddingValues = PaddingValues()
            )
            VSpacer(8.dp)
        }
    }
}

private val LargeButtonHeight = 56.dp
private val LargeButtonContentPadding: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = 16.dp)

@Immutable
data class LicardButtonColors(
    private val backgroundColor: Color,
    private val contentColor: Color,
    private val disabledBackgroundColor: Color,
    private val disabledContentColor: Color,
) : ButtonColors {

    @Composable
    override fun backgroundColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) backgroundColor else disabledBackgroundColor)
    }

    @Composable
    override fun contentColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) contentColor else disabledContentColor)
    }

    companion object {

        @Composable
        fun primaryButtonColors(
            backgroundColor: Color = AdelaideTheme.colors.buttonPrimary,
            contentColor: Color = AdelaideTheme.colors.textTertiary,
            disabledBackgroundColor: Color = AdelaideTheme.colors.buttonPrimaryDisabled,
            disabledContentColor: Color = contentColor,
        ): LicardButtonColors = LicardButtonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            disabledBackgroundColor = disabledBackgroundColor,
            disabledContentColor = disabledContentColor,
        )

        @Composable
        fun outlineButtonColors(
            backgroundColor: Color = Color.Transparent,
            contentColor: Color = AdelaideTheme.colors.buttonPrimary,
        ): LicardButtonColors = LicardButtonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            disabledBackgroundColor = backgroundColor,
            disabledContentColor = contentColor,
        )

        @Composable
        fun textButtonColors(
            backgroundColor: Color = Color.Transparent,
            contentColor: Color = AdelaideTheme.colors.buttonPrimary,
            disabledContentColor: Color = AdelaideTheme.colors.buttonSecondaryText
                .copy(alpha = ContentAlpha.disabled),
        ): LicardButtonColors = LicardButtonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            disabledBackgroundColor = backgroundColor,
            disabledContentColor = disabledContentColor
        )
    }
}
