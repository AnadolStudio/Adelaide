package com.anadolstudio.compose.ui.view.text.date

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.anadolstudio.compose.ui.R
import com.anadolstudio.compose.ui.animation.IconAnimatedVisibility
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.view.text.AccountingTextFieldDefaults
import com.anadolstudio.compose.ui.view.text.ClickableTextField

@Composable
fun DateRow(
    startDate: String,
    endDate: String,
    onStartDateCalendarClick: () -> Unit,
    onStartDateResetClick: () -> Unit,
    onEndDateCalendarClick: () -> Unit,
    onEndDateResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
    ) {
        DateTextField(
            value = startDate,
            labelText = stringResource(R.string.date_from_label),
            onCalendarClick = onStartDateCalendarClick,
            onDateResetClick = onStartDateResetClick,
        )

        DateTextField(
            value = endDate,
            labelText = stringResource(R.string.date_to_label),
            onCalendarClick = onEndDateCalendarClick,
            onDateResetClick = onEndDateResetClick,
        )
    }
}

@Composable
fun RowScope.DateTextField(
    value: String,
    labelText: String,
    onCalendarClick: () -> Unit,
    onDateResetClick: () -> Unit,
) {
    ClickableTextField(
        value = value,
        labelText = labelText,
        onClick = onCalendarClick,
        modifier = Modifier.weight(1f),
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
