package com.anadolstudio.compose.ui.view.calendar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.LicardTheme
import com.anadolstudio.compose.ui.theme.preview.ThemePreviewParameter
import com.anadolstudio.compose.ui.view.button.TextButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun calendarColors() = DatePickerDefaults.colors(
    containerColor = AdelaideTheme.colors.calendarBackground,
    todayDateBorderColor = AdelaideTheme.colors.buttonPrimary,
    selectedDayContainerColor = AdelaideTheme.colors.buttonPrimary,
    todayContentColor = AdelaideTheme.colors.buttonPrimary,
    currentYearContentColor = AdelaideTheme.colors.buttonPrimary,
    selectedYearContainerColor = AdelaideTheme.colors.buttonPrimary,
    dayContentColor = AdelaideTheme.colors.textPrimary,
    dayInSelectionRangeContentColor = AdelaideTheme.colors.textPrimary,
    yearContentColor = AdelaideTheme.colors.textPrimary,
    weekdayContentColor = AdelaideTheme.colors.textPrimary,
    titleContentColor = AdelaideTheme.colors.textPrimary,
    subheadContentColor = AdelaideTheme.colors.textPrimary,
    headlineContentColor = AdelaideTheme.colors.textPrimary,
    selectedYearContentColor = AdelaideTheme.colors.textTertiary,
    selectedDayContentColor = AdelaideTheme.colors.textTertiary,
    dayInSelectionRangeContainerColor = AdelaideTheme.colors.textPrimary,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarDialog(
    datePickerState: DatePickerState,
    applyText: String,
    dismissText: String,
    onDismissClick: () -> Unit,
    onApplyClick: (Long?) -> Unit,
) {
    DatePickerDialog(
        onDismissRequest = onDismissClick,
        confirmButton = {
            TextButton(
                onClick = { onApplyClick.invoke(datePickerState.selectedDateMillis) },
                text = applyText,
                contentPadding = PaddingValues(),
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismissClick,
                text = dismissText,
                contentPadding = PaddingValues(),
            )
        },
        colors = calendarColors(),
    ) {
        DatePicker(
            state = datePickerState,
            colors = calendarColors()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun ButtonsPreview(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    val datePickerState = rememberDatePickerState()
    LicardTheme(useDarkMode) {
        CalendarDialog(
            datePickerState = datePickerState,
            applyText = "Ok",
            dismissText = "Cancel",
            onDismissClick = {},
            onApplyClick = {},
        )
    }
}
