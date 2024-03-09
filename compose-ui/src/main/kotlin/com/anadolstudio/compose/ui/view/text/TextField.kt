package com.anadolstudio.compose.ui.view.text

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.AdelaideTypography
import com.anadolstudio.compose.ui.theme.LicardDimension
import com.anadolstudio.compose.ui.theme.LicardTheme
import com.anadolstudio.compose.ui.theme.preview.ThemePreviewParameter
import com.anadolstudio.compose.ui.view.VSpacer

@Suppress("LongParameterList")
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = AdelaideTypography.textBook18,
    labelText: String = "",
    hintText: String = "",
    showHint: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    colors: TextFieldColors = AccountingTextFieldDefaults.textFieldColors(),
) {
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)

    TextField(
        value = textFieldValue,
        onValueChange = { fieldValue ->
            textFieldValueState = fieldValue
            if (value != fieldValue.text) {
                onValueChange(fieldValue.text)
            }
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        labelText = labelText,
        hintText = hintText,
        showHint = showHint,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        colors = colors,
    )
}

@Suppress("LongParameterList")
@Composable
fun TextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = AdelaideTypography.textBook18,
    labelText: String = "",
    hintText: String = "",
    showHint: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    colors: TextFieldColors = AccountingTextFieldDefaults.textFieldColors(),
) {
    Column(modifier.padding(vertical = 8.dp)) {
        BaseTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            label = { Text(text = labelText) },
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            colors = colors,
        )
        TextFieldHint(hintText, showHint, isError)
    }
}

@Suppress("LongParameterList")
@Composable
fun LargeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = AdelaideTypography.textBook18,
    labelText: String = "",
    hintText: String = "",
    showHint: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    isRequired: Boolean = false,
    colors: TextFieldColors = AccountingTextFieldDefaults.textFieldColors(),
) {
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)

    LargeTextField(
        value = textFieldValue,
        onValueChange = { fieldValue ->
            textFieldValueState = fieldValue
            if (value != fieldValue.text) {
                onValueChange(fieldValue.text)
            }
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        labelText = labelText,
        hintText = hintText,
        showHint = showHint,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        isRequired = isRequired,
        colors = colors,
    )
}

@Suppress("LongParameterList")
@Composable
fun LargeTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = AdelaideTypography.textBook18,
    labelText: String = "",
    hintText: String = "",
    showHint: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    isRequired: Boolean = false,
    colors: TextFieldColors = AccountingTextFieldDefaults.textFieldColors(),
) {
    Column(modifier = modifier) {
        BaseTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            label = {
                if (isRequired) {
                    Text(text = labelText.addAsterisk())
                } else {
                    Text(text = labelText)
                }
            },
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            colors = colors,
        )
        TextFieldHint(hintText, showHint, isError)
    }
}

@Composable
private fun ColumnScope.TextFieldHint(
    hintText: String = "",
    showHint: Boolean = true,
    isError: Boolean = false,
) {
    AnimatedVisibility(showHint) {
        Column {
            VSpacer(4.dp)
            val hintColor = if (isError) {
                AdelaideTheme.colors.error
            } else {
                AdelaideTheme.colors.hintColor
            }
            Text(
                text = hintText,
                style = AdelaideTypography.captionBook14,
                color = hintColor,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BaseTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.TextFieldShape,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
) {
    val textColor = textStyle.color.takeOrElse {
        colors.textColor(enabled).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    BasicTextField(
        value = value,
        modifier = modifier
            .background(colors.backgroundColor(enabled).value, shape)
            .indicatorLine(enabled, isError, interactionSource, colors)
            .defaultMinSize(
                minWidth = TextFieldDefaults.MinWidth,
                minHeight = TextFieldDefaults.MinHeight
            ),
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = mergedTextStyle,
        cursorBrush = SolidColor(colors.cursorColor(isError).value),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        decorationBox = @Composable { innerTextField ->
            // places leading icon, text field with label and placeholder, trailing icon
            TextFieldDefaults.TextFieldDecorationBox(
                value = value.text,
                visualTransformation = visualTransformation,
                innerTextField = innerTextField,
                placeholder = placeholder,
                label = label,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                singleLine = singleLine,
                enabled = enabled,
                isError = isError,
                interactionSource = interactionSource,
                colors = colors,
            )
        }
    )
}

@ExperimentalMaterialApi
private fun Modifier.indicatorLine(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource,
    colors: TextFieldColors,
    horizontalPadding: Dp = LicardDimension.layoutHorizontalMargin,
    focusedIndicatorLineThickness: Dp = TextFieldDefaults.FocusedBorderThickness,
    unfocusedIndicatorLineThickness: Dp = TextFieldDefaults.UnfocusedBorderThickness
) = composed {
    val stroke = animateBorderStrokeAsState(
        enabled = enabled,
        isError = isError,
        interactionSource = interactionSource,
        colors = colors,
        focusedBorderThickness = focusedIndicatorLineThickness,
        unfocusedBorderThickness = unfocusedIndicatorLineThickness
    )
    Modifier.drawIndicatorLine(stroke.value, horizontalPadding)
}

@Composable
private fun animateBorderStrokeAsState(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource,
    colors: TextFieldColors,
    focusedBorderThickness: Dp,
    unfocusedBorderThickness: Dp
): State<BorderStroke> {
    val focused by interactionSource.collectIsFocusedAsState()
    val indicatorColor = colors.indicatorColor(enabled, isError, interactionSource)
    val targetThickness = if (focused) focusedBorderThickness else unfocusedBorderThickness
    val animatedThickness = if (enabled) {
        animateDpAsState(targetThickness, tween(durationMillis = 150))
    } else {
        rememberUpdatedState(unfocusedBorderThickness)
    }
    return rememberUpdatedState(
        BorderStroke(animatedThickness.value, SolidColor(indicatorColor.value))
    )
}

internal fun Modifier.drawIndicatorLine(
    indicatorBorder: BorderStroke,
    horizontalPadding: Dp,
): Modifier {
    val strokeWidthDp = indicatorBorder.width
    return drawWithContent {
        drawContent()
        if (strokeWidthDp == Dp.Hairline) return@drawWithContent
        val strokeWidth = strokeWidthDp.value * density
        val y = size.height - strokeWidth / 2
        drawLine(
            indicatorBorder.brush,
            Offset(horizontalPadding.toPx(), y),
            Offset(size.width - horizontalPadding.toPx(), y),
            strokeWidth
        )
    }
}

@Composable
internal fun String.addAsterisk(): AnnotatedString = buildAnnotatedString {
    append(this@addAsterisk)
    withStyle(style = SpanStyle(color = AdelaideTheme.colors.asteriskColor)) {
        append("\u002A")
    }
}

object AccountingTextFieldDefaults {
    @Suppress("LongParameterList")
    @Composable
    fun textFieldColors(
        textColor: Color = AdelaideTheme.colors.textPrimary,
        disabledTextColor: Color = AdelaideTheme.colors.textPrimary,
        backgroundColor: Color = Color.Transparent,
        cursorColor: Color = AdelaideTheme.colors.buttonPrimary,
        errorCursorColor: Color = AdelaideTheme.colors.error,
        focusedIndicatorColor: Color = AdelaideTheme.colors.divider,
        unfocusedIndicatorColor: Color = AdelaideTheme.colors.divider,
        disabledIndicatorColor: Color = AdelaideTheme.colors.divider,
        errorIndicatorColor: Color = AdelaideTheme.colors.error,
        leadingIconColor: Color = AdelaideTheme.colors.textSecondary,
        disabledLeadingIconColor: Color = AdelaideTheme.colors.buttonPrimaryDisabled,
        errorLeadingIconColor: Color = AdelaideTheme.colors.error,
        trailingIconColor: Color = AdelaideTheme.colors.textPrimary,
        disabledTrailingIconColor: Color = AdelaideTheme.colors.buttonPrimaryDisabled,
        errorTrailingIconColor: Color = AdelaideTheme.colors.textPrimary,
        focusedLabelColor: Color = AdelaideTheme.colors.textPrimary,
        unfocusedLabelColor: Color = AdelaideTheme.colors.textSecondary,
        disabledLabelColor: Color = AdelaideTheme.colors.textPrimaryDisabled,
        errorLabelColor: Color = AdelaideTheme.colors.textSecondary,
        placeholderColor: Color = AdelaideTheme.colors.textSecondary,
        disabledPlaceholderColor: Color = AdelaideTheme.colors.textPrimaryDisabled,
    ): TextFieldColors = TextFieldDefaults.textFieldColors(
        textColor = textColor,
        disabledTextColor = disabledTextColor,
        cursorColor = cursorColor,
        errorCursorColor = errorCursorColor,
        focusedIndicatorColor = focusedIndicatorColor,
        unfocusedIndicatorColor = unfocusedIndicatorColor,
        errorIndicatorColor = errorIndicatorColor,
        disabledIndicatorColor = disabledIndicatorColor,
        leadingIconColor = leadingIconColor,
        disabledLeadingIconColor = disabledLeadingIconColor,
        errorLeadingIconColor = errorLeadingIconColor,
        trailingIconColor = trailingIconColor,
        disabledTrailingIconColor = disabledTrailingIconColor,
        errorTrailingIconColor = errorTrailingIconColor,
        backgroundColor = backgroundColor,
        focusedLabelColor = focusedLabelColor,
        unfocusedLabelColor = unfocusedLabelColor,
        disabledLabelColor = disabledLabelColor,
        errorLabelColor = errorLabelColor,
        placeholderColor = placeholderColor,
        disabledPlaceholderColor = disabledPlaceholderColor
    )
}

@Preview(showBackground = true)
@Composable
@Suppress("StringLiteralDuplication")
private fun TextFieldPreview(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    LicardTheme(useDarkMode) {
        Column(
            Modifier
                .padding(8.dp)
                .background(AdelaideTheme.colors.backgroundPrimary)
        ) {
            LargeTextField(
                value = "With label",
                onValueChange = {},
                labelText = "Text Field",
                trailingIcon = { Icon(Icons.Filled.Clear, "Clear") },
            )
            LargeTextField(
                value = "",
                onValueChange = {},
                labelText = "Text Field",
                trailingIcon = { Icon(Icons.Filled.Clear, "Clear") },
            )
            LargeTextField(
                value = "With label",
                onValueChange = {},
                labelText = "Text Field",
                hintText = "Hint Text",
                isError = true,
                trailingIcon = { Icon(Icons.Filled.Clear, "Clear") },
            )
        }
    }
}
