package com.anadolstudio.compose.ui.view.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anadolstudio.compose.ui.drawable.LicardIcon
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.AdelaideTypography
import com.anadolstudio.compose.ui.theme.LicardDimension
import com.anadolstudio.compose.ui.theme.LicardTheme
import com.anadolstudio.compose.ui.theme.preview.ThemePreviewParameter
import com.anadolstudio.compose.ui.view.WSpacer
import com.anadolstudio.compose.ui.view.text.Text

@Suppress("LongParameterList")
@Composable
fun NavigationItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    isDividerVisible: Boolean = true,
    isNavigationIconVisible: Boolean = true,
    iconTint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    textColor: Color = AdelaideTheme.colors.textPrimary,
    dividerColor: Color = DividerDefaults.color,
    dividerHorizontalPadding: Dp = LicardDimension.layoutHorizontalMargin
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = LicardDimension.layoutHorizontalMargin,
                vertical = 20.dp,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (icon != null) {
                Icon(painter = icon, contentDescription = null, tint = iconTint)
            }
            Text(text = title, style = AdelaideTypography.textBook18, color = textColor)
            WSpacer()
            AnimatedVisibility(isNavigationIconVisible) {
                Icon(painter = LicardIcon.Forward, contentDescription = null)
            }
        }

        AnimatedVisibility(isDividerVisible) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dividerHorizontalPadding),
                color = dividerColor,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewNavigationItem(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    LicardTheme(useDarkMode) {
        Column {
            NavigationItem(
                icon = LicardIcon.Invoice,
                title = "Счета",
                isDividerVisible = true,
                onClick = {},
            )
            NavigationItem(
                icon = LicardIcon.Wallet,
                title = "Платежи",
                onClick = {},
            )
        }
    }
}
