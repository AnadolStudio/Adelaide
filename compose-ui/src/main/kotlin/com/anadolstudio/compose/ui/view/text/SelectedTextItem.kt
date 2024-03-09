package com.anadolstudio.compose.ui.view.text

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.anadolstudio.compose.ui.drawable.LicardIcon
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.AdelaideTypography
import com.anadolstudio.compose.ui.theme.LicardDimension
import com.anadolstudio.compose.ui.view.VSpacer

@Composable
fun SelectedTextItem(
    title: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    isSelected: Boolean = false,
) {
    Column(
        modifier = modifier
            .clickable { onClick.invoke(title) }
            .padding(horizontal = LicardDimension.layoutHorizontalMargin)
    ) {
        VSpacer(12.dp)
        Row {
            Text(
                text = title,
                color = AdelaideTheme.colors.textPrimary,
                style = AdelaideTypography.textBook18,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            AnimatedVisibility(visible = isSelected) {
                Icon(
                    painter = LicardIcon.CheckMark,
                    tint = AdelaideTheme.colors.textQuaternary,
                    contentDescription = null
                )
            }
        }
        if (!description.isNullOrBlank()) {
            Text(
                text = description,
                color = AdelaideTheme.colors.textSecondary,
                style = AdelaideTypography.captionBook16,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        VSpacer(12.dp)
    }
}
