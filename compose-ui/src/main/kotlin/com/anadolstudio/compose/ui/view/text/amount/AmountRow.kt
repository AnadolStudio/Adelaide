package com.anadolstudio.compose.ui.view.text.amount

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.anadolstudio.compose.ui.animation.IconAnimatedVisibility
import kotlin.reflect.KFunction1

@Composable
fun AmountTextFieldRow(
    valueFrom: Double?,
    valueTo: Double?,
    labelFromText: String,
    labelToText: String,
    onFromValueChange: KFunction1<Double?, Unit>,
    onFromFromResetClick: () -> Unit,
    onToValueChange: KFunction1<Double?, Unit>,
    onToToResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        AmountTextField(
            amount = valueFrom,
            labelText = labelFromText,
            onValueChange = onFromValueChange,
            onValueResetClick = onFromFromResetClick,
        )

        AmountTextField(
            amount = valueTo,
            labelText = labelToText,
            onValueChange = onToValueChange,
            onValueResetClick = onToToResetClick,
        )
    }
}

@Composable
private fun RowScope.AmountTextField(
    amount: Double?,
    labelText: String,
    onValueChange: (Double?) -> Unit,
    onValueResetClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    AmountField(
        amount = amount,
        onValueChange = onValueChange,
        labelText = labelText,
        modifier = Modifier.weight(1f),
        trailingIcon = {
            IconAnimatedVisibility(visible = amount != null) {
                IconButton(onClick = onValueResetClick) {
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = null)
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Decimal),
        keyboardActions = KeyboardActions {
            focusManager.clearFocus()
        }
    )
}
