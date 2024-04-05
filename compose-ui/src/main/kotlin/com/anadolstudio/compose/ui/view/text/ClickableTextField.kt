package com.anadolstudio.compose.ui.view.text

import androidx.compose.material.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.anadolstudio.compose.ui.modifier.noRippleClickable
import com.anadolstudio.compose.ui.theme.AdelaideTheme

@Composable
fun ClickableTextField(
    value: String,
    labelText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    colors: TextFieldColors = AccountingTextFieldDefaults.textFieldColors()
) {
    TextField(
        value = value,
        onValueChange = { },
        labelText = labelText,
        enabled = false,
        modifier = modifier
            .noRippleClickable(onClick),
        trailingIcon = trailingIcon,
        colors = colors
    )
}

@Composable
fun LargeClickableTextField(
    value: String,
    labelText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    colors: TextFieldColors = AccountingTextFieldDefaults.textFieldColors(
        disabledLabelColor = AdelaideTheme.colors.textSecondary
    )
) {
    LargeTextField(
        value = value,
        onValueChange = { },
        labelText = labelText,
        enabled = false,
        trailingIcon = trailingIcon,
        modifier = modifier
            .noRippleClickable(onClick),
        colors = colors
    )
}
