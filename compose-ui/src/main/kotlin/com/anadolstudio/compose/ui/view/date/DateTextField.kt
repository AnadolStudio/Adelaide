package com.anadolstudio.compose.ui.view.date

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.anadolstudio.compose.ui.animation.IconAnimatedVisibility
import com.anadolstudio.compose.ui.modifier.noRippleClickable
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.view.text.AccountingTextFieldDefaults
import com.anadolstudio.compose.ui.view.text.TextField

@Composable
fun RowScope.DateTextField(
    value: String,
    labelText: String,
    onCalendarClick: () -> Unit,
    onDateResetClick: () -> Unit,
) {
    TextField(
        value = value,
        onValueChange = { },
        labelText = labelText,
        enabled = false,
        modifier = Modifier
            .weight(1f)
            .noRippleClickable(onCalendarClick),
        trailingIcon = {
            IconAnimatedVisibility(value.isNotEmpty()) {
                IconButton(onClick = onDateResetClick) {
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = null)
                }
            }
        },
        colors = AccountingTextFieldDefaults.textFieldColors(
            disabledLabelColor = AdelaideTheme.colors.textSecondary,
            disabledTrailingIconColor = AdelaideTheme.colors.textPrimary,
        )
    )
}
