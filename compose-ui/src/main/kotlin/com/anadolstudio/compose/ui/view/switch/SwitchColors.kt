package com.anadolstudio.compose.ui.view.switch

import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import com.anadolstudio.compose.ui.theme.AdelaideTheme

@Composable
fun SwitchLicardColors() = SwitchDefaults.colors(
    checkedThumbColor = AdelaideTheme.colors.switchThumbColor,
    checkedTrackColor = AdelaideTheme.colors.switchCheckedTrackColor,
    uncheckedThumbColor = AdelaideTheme.colors.switchThumbColor,
    uncheckedTrackColor = AdelaideTheme.colors.switchUncheckedTrackColor,
    uncheckedBorderColor = AdelaideTheme.colors.switchUncheckedTrackColor,
)
