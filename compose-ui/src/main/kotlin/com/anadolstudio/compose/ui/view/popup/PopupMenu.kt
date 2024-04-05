package com.anadolstudio.compose.ui.view.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.AdelaideTypography
import com.anadolstudio.compose.ui.theme.LicardDimension

@Composable
fun PopupMenu(
    vararg menuItemsData: MenuItemsData,
    expandedState: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        modifier = modifier.background(AdelaideTheme.colors.backgroundPrimary),
        expanded = expandedState.value,
        onDismissRequest = { expandedState.value = false }
    ) {
        menuItemsData.forEach { item ->
            MenuItems(
                titleRes = item.titleRes,
                icon = item.icon,
                tint = item.tint,
                onClick = {
                    item.onClick.invoke()
                    expandedState.value = false
                },
            )
        }
    }
}

data class MenuItemsData(
    val titleRes: Int,
    val icon: Painter,
    val tint: Color,
    val onClick: () -> Unit
)

@Composable
private fun MenuItems(
    titleRes: Int,
    icon: Painter,
    onClick: () -> Unit,
    tint: Color = AdelaideTheme.colors.textPrimary
) {
    DropdownMenuItem(
        text = { GroupActionsItem(textRes = titleRes, icon = icon, tint = tint) },
        onClick = onClick
    )
}

@Composable
private fun GroupActionsItem(
    textRes: Int,
    icon: Painter,
    tint: Color = AdelaideTheme.colors.textPrimary,
) {
    Row(
        modifier = Modifier.padding(end = LicardDimension.layoutMediumMargin)
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(24.dp),
            painter = icon,
            contentDescription = null,
            tint = tint
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = LicardDimension.layoutMainMargin)
                .align(Alignment.CenterVertically),
            text = stringResource(textRes),
            style = AdelaideTypography.textBook18,
            color = tint
        )
    }
}
