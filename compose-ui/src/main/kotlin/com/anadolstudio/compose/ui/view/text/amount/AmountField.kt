package com.anadolstudio.compose.ui.view.text.amount

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.anadolstudio.compose.ui.theme.AdelaideTypography
import com.anadolstudio.compose.ui.view.text.LargeTextField
import com.anadolstudio.compose.ui.view.text.TextField
import com.anadolstudio.compose.ui.view.text.visualTransformation.AmountVisualTransformation

private const val DECIMAL_SEPARATOR = '.'

private val SHORT_DECIMAL_VALID_INPUT
    get() = Regex("^\\d{1,8}(\\.\\d{0,2})?$")

private fun Double?.isNullOrZero() = this == 0.0 || this == null

@Suppress("LongParameterList", "NullableToStringCall")
@Composable
fun AmountField(
    amount: Double?,
    onValueChange: (Double?) -> Unit,
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
    visualTransformation: VisualTransformation = AmountVisualTransformation(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    inputPattern: Regex = SHORT_DECIMAL_VALID_INPUT,
) {
    var amountStateValue by remember { mutableStateOf(amount) }
    val textFieldValue = remember {
        val initialValue = if (amount.isNullOrZero()) "" else amount.toString()
        mutableStateOf(TextFieldValue(initialValue, selection = TextRange(initialValue.length)))
    }

    if (amount != amountStateValue) {
        amountStateValue = amount
        textFieldValue.value = textFieldValue.value.copy(
            text = if (amount.isNullOrZero()) "" else amount.toString(),
            selection = TextRange(amount.toString().length)
        )
    }

    TextField(
        value = textFieldValue.value,
        onValueChange = { text ->
            val normalizedValue = normalizeAmountInput(text)
            val textValue = normalizedValue.text

            if (validateAmountValue(textValue, inputPattern)) {
                textFieldValue.value = normalizedValue

                val doubleValue = if (textValue.isEmpty()) null else textValue.toDouble()

                amountStateValue = doubleValue
                if (amount != amountStateValue) onValueChange(amountStateValue)
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
    )
}

@Suppress("LongParameterList", "NullableToStringCall")
@Composable
fun LargeAmountField(
    amount: Double?,
    onValueChange: (Double?) -> Unit,
    isRequired: Boolean,
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
    visualTransformation: VisualTransformation = AmountVisualTransformation(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    inputPattern: Regex = SHORT_DECIMAL_VALID_INPUT,
) {
    var amountStateValue by remember { mutableStateOf(amount) }
    val textFieldValue = remember {
        val initialValue = if (amount.isNullOrZero()) "" else amount.toString()
        mutableStateOf(TextFieldValue(initialValue, selection = TextRange(initialValue.length)))
    }

    if (amount != amountStateValue) {
        amountStateValue = amount
        textFieldValue.value = textFieldValue.value.copy(
            text = if (amount.isNullOrZero()) "" else amount.toString(),
            selection = TextRange(amount.toString().length)
        )
    }

    LargeTextField(
        value = textFieldValue.value,
        onValueChange = { text ->
            val normalizedValue = normalizeAmountInput(text)
            val textValue = normalizedValue.text

            if (validateAmountValue(textValue, inputPattern)) {
                textFieldValue.value = normalizedValue

                val doubleValue = if (textValue.isEmpty()) null else textValue.toDouble()

                amountStateValue = doubleValue
                if (amount != amountStateValue) onValueChange(amountStateValue)
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
    )
}

/**
 * Normalizes amount input before conversation to double value.
 *
 * Handled cases:
 * - "0000" -> "0"
 * - "005"  -> "5"
 * - ".5"   -> "0.5"
 * - "."    -> "0."
 * - "0,5"  -> "0.5"
 * - ""     -> ""
 */
private fun normalizeAmountInput(value: TextFieldValue): TextFieldValue {
    val originalText = value.text.ifEmpty { return value }
    val normalizedText = originalText
        .filterNot { it.isWhitespace() }
        .trimStart('0')
        .replaceFirstChar { if (it == DECIMAL_SEPARATOR) "0$DECIMAL_SEPARATOR" else "$it" }
        .ifEmpty { "0" }

    // Cursor position after normalization can be changed in two cases:
    // - When user adds . before input value, and we additionally add 0:
    //   .|5 -> 0.|5
    // - When user inputs one or more zeroes at the value start, and we remove them:
    //   00|5 -> |5
    val lengthDiff = normalizedText.length - originalText.length
    val selection = if (lengthDiff != 0) {
        TextRange(
            start = (value.selection.start + lengthDiff).coerceAtLeast(0),
            end = (value.selection.end + lengthDiff).coerceAtLeast(0),
        )
    } else {
        value.selection
    }

    return value.copy(text = normalizedText, selection = selection)
}

private fun validateAmountValue(value: String, inputPattern: Regex): Boolean {
    return value.isEmpty() || value matches inputPattern
}
