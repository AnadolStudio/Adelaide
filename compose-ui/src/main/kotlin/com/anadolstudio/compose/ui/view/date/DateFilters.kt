package com.anadolstudio.compose.ui.view.date

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.anadolstudio.compose.ui.R

@Composable
fun DateFiltersRow(
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
