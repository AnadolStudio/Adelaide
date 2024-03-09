package com.anadolstudio.compose.ui.view.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.anadolstudio.compose.ui.animation.IconAnimatedVisibility
import com.anadolstudio.compose.ui.drawable.LicardIcon
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.AdelaideTypography
import com.anadolstudio.compose.ui.theme.LicardDimension
import com.anadolstudio.compose.ui.theme.LicardTheme
import com.anadolstudio.compose.ui.theme.Shapes
import com.anadolstudio.compose.ui.theme.preview.ThemePreviewParameter
import com.anadolstudio.compose.ui.theme.tiny
import com.anadolstudio.compose.ui.view.VSpacer
import com.anadolstudio.compose.ui.view.text.Text

@Composable
fun Search(
    value: String,
    placeholderText: String,
    onValueChange: (String) -> Unit,
    errorText: String = "",
    onValueResetClick: () -> Unit = {},
    enabled: Boolean = true,
    isError: Boolean = false,
    textStyle: TextStyle = AdelaideTypography.textBook18,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val borderColor by animateColorAsState(getBorderlineColor(isError, enabled))

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier
                .background(AdelaideTheme.colors.backgroundSecondary, shape = Shapes.tiny)
                .border(BorderStroke(width = 1.dp, color = borderColor), shape = Shapes.tiny)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(start = 16.dp),
                painter = LicardIcon.Search,
                contentDescription = null
            )
            SearchTextField(
                value = value,
                placeholderText = placeholderText,
                textStyle = textStyle,
                enabled = enabled,
                onValueChange = onValueChange,
                onValueResetClick = onValueResetClick,
                isError = isError,
                interactionSource = interactionSource,
            )
        }

        ErrorHint(errorText, isError)
    }
}

@Composable
private fun SearchTextField(
    value: String,
    placeholderText: String,
    enabled: Boolean,
    textStyle: TextStyle,
    onValueChange: (String) -> Unit,
    onValueResetClick: () -> Unit = {},
    singleLine: Boolean = true,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)
    val onTextFieldValueChange: (TextFieldValue) -> Unit = { fieldValue ->
        textFieldValueState = fieldValue
        if (value != fieldValue.text) {
            onValueChange(fieldValue.text)
        }
    }

    val colors = createSearchTextFieldColors()
    val textColor = textStyle.color.takeOrElse { colors.textColor(enabled).value }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    BasicTextField(
        value = textFieldValue,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        onValueChange = onTextFieldValueChange,
        enabled = enabled,
        readOnly = false,
        textStyle = mergedTextStyle,
        cursorBrush = SolidColor(colors.cursorColor(isError).value),
        visualTransformation = VisualTransformation.None,
        keyboardOptions = KeyboardOptions.Default,
        keyboardActions = KeyboardActions(),
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = 1,
        minLines = 1,
        decorationBox = @Composable { innerTextField ->
            SearchFieldDecorationBox(
                textFieldValue = textFieldValue,
                innerTextField = innerTextField,
                placeholderText = placeholderText,
                onValueResetClick = onValueResetClick,
                singleLine = singleLine,
                enabled = enabled,
                isError = isError,
                interactionSource = interactionSource,
                colors = colors
            )
        }
    )
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun SearchFieldDecorationBox(
    textFieldValue: TextFieldValue,
    innerTextField: @Composable () -> Unit,
    placeholderText: String,
    onValueResetClick: () -> Unit,
    singleLine: Boolean,
    enabled: Boolean,
    isError: Boolean,
    interactionSource: MutableInteractionSource,
    colors: TextFieldColors
) {
    TextFieldDefaults.TextFieldDecorationBox(
        value = textFieldValue.text,
        visualTransformation = VisualTransformation.None,
        innerTextField = innerTextField,
        placeholder = { Text(placeholderText) },
        label = null,
        leadingIcon = null,
        trailingIcon = {
            IconAnimatedVisibility(visible = textFieldValue.text.isNotEmpty()) {
                IconButton(
                    onClick = onValueResetClick,
                    modifier = Modifier.size(LicardDimension.minTouchSize)
                ) {
                    Icon(
                        modifier = Modifier.size(10.dp),
                        painter = LicardIcon.Close,
                        contentDescription = null,
                    )
                }
            }
        },
        singleLine = singleLine,
        enabled = enabled,
        isError = isError,
        interactionSource = interactionSource,
        colors = colors,
        contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 0.dp)
    )
}

@Composable
private fun ColumnScope.ErrorHint(
    errorText: String = "",
    isError: Boolean = false,
) {
    AnimatedVisibility(isError) {
        Column {
            VSpacer(4.dp)
            Text(
                text = errorText,
                style = AdelaideTypography.captionBook14,
                color = AdelaideTheme.colors.error,
            )
        }
    }
}

@Composable
private fun getBorderlineColor(
    error: Boolean,
    enabled: Boolean,
): Color {
    return when {
        error -> AdelaideTheme.colors.error
        enabled -> Color.Transparent
        else -> Color.Transparent
    }
}

@Composable
private fun createSearchTextFieldColors(): TextFieldColors = TextFieldDefaults.textFieldColors(
    textColor = AdelaideTheme.colors.textPrimary,
    disabledTextColor = AdelaideTheme.colors.textPrimary,
    backgroundColor = Color.Transparent,
    cursorColor = AdelaideTheme.colors.buttonPrimary,
    trailingIconColor = AdelaideTheme.colors.textPrimary,
    disabledTrailingIconColor = AdelaideTheme.colors.buttonPrimaryDisabled,
    errorTrailingIconColor = AdelaideTheme.colors.textPrimary,
    placeholderColor = AdelaideTheme.colors.textSecondary,
    disabledPlaceholderColor = AdelaideTheme.colors.textPrimaryDisabled,
)

@Preview
@Composable
private fun Preview(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    LicardTheme(useDarkMode) {
        Column {
            Search(value = "", placeholderText = "Поиск", onValueChange = {})
            Search(value = "Пример текста", placeholderText = "Номер карты", onValueChange = {})
            Search(value = "", placeholderText = "Номер карты", onValueChange = {}, isError = true)
        }
    }
}
