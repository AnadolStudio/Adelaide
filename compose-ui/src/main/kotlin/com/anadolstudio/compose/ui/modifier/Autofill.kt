package com.anadolstudio.compose.ui.modifier

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.autofill(
    vararg autofillTypes: AutofillType,
    onFill: (String) -> Unit
): Modifier = composed {
    val autofill = LocalAutofill.current
    val autofillNode = AutofillNode(onFill = onFill, autofillTypes = autofillTypes.toList())
    LocalAutofillTree.current += autofillNode
    if (autofill != null) {
        onGloballyPositioned { autofillNode.boundingBox = it.boundsInWindow() }
            .onFocusChanged { focusState ->
                if (focusState.isFocused && autofillNode.boundingBox != null) {
                    autofill.requestAutofillForNode(autofillNode)
                } else {
                    autofill.cancelAutofillForNode(autofillNode)
                }
            }
    } else {
        this
    }
}
