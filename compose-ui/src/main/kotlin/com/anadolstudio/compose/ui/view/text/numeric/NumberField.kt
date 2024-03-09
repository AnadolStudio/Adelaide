package com.anadolstudio.compose.ui.view.text.numeric

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.anadolstudio.compose.ui.animation.IconAnimatedVisibility
import com.anadolstudio.compose.ui.view.text.TextField
import com.anadolstudio.compose.ui.view.text.visualTransformation.VolumeVisualTransformation

@Composable
fun NumberField(
    value: Double?,
    labelText: String,
    onValueChange: (Double?) -> Unit,
    onValueResetClick: () -> Unit,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val focusManager = LocalFocusManager.current
    val number = remember { mutableStateOf(value) }
    val textValue = remember(value != number.value) {
        number.value = value
        mutableStateOf(
            value?.let { num ->
                if (num % 1.0 == 0.0) {
                    num.toInt().toString()
                } else {
                    num.toString()
                }
            }.orEmpty()
        )
    }

    val numberRegex = remember { "[-]?[\\d]*[.]?[\\d]*".toRegex() }

    TextField(
        value = textValue.value,
        labelText = labelText,
        modifier = modifier,
        onValueChange = { text ->
            if (numberRegex.matches(text)) {
                textValue.value = text
                number.value = text.toDoubleOrNull()
                onValueChange(number.value)
            }
        },
        trailingIcon = {
            IconAnimatedVisibility(visible = value != null) {
                IconButton(onClick = onValueResetClick) {
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = null)
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
        keyboardActions = KeyboardActions {
            focusManager.clearFocus()
        },
        visualTransformation = visualTransformation
    )
}

@Composable
@Suppress("ReusedModifierInstance")
fun VolumeTextFieldRow(
    valueFrom: Double?,
    valueTo: Double?,
    labelFromText: String,
    labelToText: String,
    onFromValueChange: (Double?) -> Unit,
    onFromFromResetClick: () -> Unit,
    onToValueChange: (Double?) -> Unit,
    onToToResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        NumberField(
            value = valueFrom,
            labelText = labelFromText,
            modifier = modifier.weight(1f),
            onValueChange = onFromValueChange,
            onValueResetClick = onFromFromResetClick,
            visualTransformation = VolumeVisualTransformation()
        )

        NumberField(
            value = valueTo,
            labelText = labelToText,
            modifier = modifier.weight(1f),
            onValueChange = onToValueChange,
            onValueResetClick = onToToResetClick,
            visualTransformation = VolumeVisualTransformation()
        )
    }
}
