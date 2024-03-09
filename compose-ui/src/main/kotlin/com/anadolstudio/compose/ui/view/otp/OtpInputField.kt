package com.anadolstudio.compose.ui.view.otp

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.anadolstudio.compose.ui.modifier.autofill
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.AdelaideTypography
import com.anadolstudio.compose.ui.theme.LicardTheme
import com.anadolstudio.compose.ui.theme.Shapes
import com.anadolstudio.compose.ui.theme.preview.ThemePreviewParameter
import com.anadolstudio.compose.ui.theme.tiny
import com.anadolstudio.compose.ui.view.VSpacer

@Suppress("MagicNumber")
private val ANIMATION_STEPS = listOf(0f to 0, -7f to 80, 14f to 240, 0f to 320)
private const val ANIMATION_TOTAL_TIME = 320

@Composable
fun OtpInputField(
    digitsCount: Int,
    value: String,
    onValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    onErrorShown: () -> Unit = {},
    enabled: Boolean = true,
    error: String? = null,
) {
    var textFieldValueState by remember {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }
    val textFieldValue = textFieldValueState.copy(text = value, selection = TextRange(value.length))

    var showError by remember { mutableStateOf(false) }
    val offsetX = remember { Animatable(0f) }

    val shouldUpdate = { fieldValue: TextFieldValue ->
                fieldValue != textFieldValue &&
                fieldValue.text.isDigitsOnly() &&
                fieldValue.text.length <= digitsCount
    }

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        OtpInputField(
            textValue = textFieldValue,
            digitsCount = digitsCount,
            onValueChange = { fieldValue ->
                if (shouldUpdate(fieldValue)) {
                    textFieldValueState = fieldValue
                    onValue(fieldValue.text)
                }
            },
            modifier = Modifier.offset { IntOffset(offsetX.value.dp.roundToPx(), 0) },
            enabled = enabled,
            error = error != null,
        )
        ErrorLabel(error.orEmpty(), showError)
    }
    AnimationEffect(error, offsetX) { shouldShowError ->
        showError = shouldShowError
        if (showError) onErrorShown()
    }
}

@Suppress("ReusedModifierInstance") // https://github.com/appKODE/detekt-rules-compose/issues/5
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun OtpInputField(
    textValue: TextFieldValue,
    digitsCount: Int,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: Boolean = false,
) {
    var focused by remember { mutableStateOf(false) }
    val toolbarPosition = remember { mutableStateOf<Rect?>(null) }

    BasicTextField(
        value = textValue,
        onValueChange = onValueChange,
        modifier = modifier
            .onFocusChanged { focusState -> focused = focusState.hasFocus }
            .autofill(
                AutofillType.SmsOtpCode,
                onFill = { onValueChange(TextFieldValue(it)) },
            ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = {
            OtpInputContent(
                textValue = textValue,
                digitsCount = digitsCount,
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    toolbarPosition.value = coordinates.boundsInWindow()
                },
                enabled = enabled,
                focused = focused,
                error = error,
            )
        }
    )
}

@Composable
private fun AnimationEffect(
    error: String?,
    offsetX: Animatable<Float, AnimationVector1D>,
    onShowError: (Boolean) -> Unit,
) {
    LaunchedEffect(error) {
        if (error != null) {
            offsetX.animateTo(
                0f,
                animationSpec = keyframes {
                    durationMillis = ANIMATION_TOTAL_TIME
                    ANIMATION_STEPS.onEach { (offsetX, time) ->
                        offsetX at time
                    }
                }
            )
            onShowError(true)
        } else {
            onShowError(false)
        }
    }
}

@Composable
private fun OtpInputContent(
    textValue: TextFieldValue,
    digitsCount: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    focused: Boolean = false,
    error: Boolean = false,
) {
    Row(modifier = modifier) {
        repeat(digitsCount) { index ->
            if (index != 0) Spacer(modifier = Modifier.width(12.dp))
            OtpChar(
                value = textValue.text.getOrNull(index)?.toString().orEmpty(),
                error = error,
                enabled = enabled,
                focused = focused && index == textValue.text.length,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ErrorLabel(error: String, showError: Boolean) {
    AnimatedVisibility(visible = showError, enter = fadeIn(), exit = fadeOut()) {
        Text(
            text = error,
            modifier = Modifier.padding(top = 4.dp),
            color = AdelaideTheme.colors.error,
            style = AdelaideTypography.captionBook14
        )
    }
}

@Composable
private fun getBorderlineColor(
    error: Boolean,
    enabled: Boolean,
    focused: Boolean,
): Color {
    return when {
        error -> AdelaideTheme.colors.error
        enabled && focused -> Color.Transparent
        else -> Color.Transparent
    }
}

@Composable
private fun OtpChar(
    value: String,
    error: Boolean,
    enabled: Boolean,
    focused: Boolean,
    modifier: Modifier = Modifier,
) {
    val borderColor by animateColorAsState(getBorderlineColor(error, enabled, focused))

    Column(
        modifier = modifier
            .height(56.dp)
            .background(AdelaideTheme.colors.backgroundSecondary, shape = Shapes.tiny)
            .border(BorderStroke(width = 1.dp, color = borderColor), shape = Shapes.tiny),
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedContent(
            targetState = value,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center,
        ) { targetValue ->
            Text(
                text = targetValue,
                style = AdelaideTypography.titleBook34,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OtpInputPreview(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    LicardTheme(useDarkMode) {
        var value by remember { mutableStateOf("") }
        Column(modifier = Modifier.background(AdelaideTheme.colors.backgroundPrimary)) {
            OtpInputField(
                digitsCount = 6,
                value = value,
                onValue = { value = it },
                onErrorShown = {},
                modifier = Modifier.padding(16.dp),
            )
            VSpacer(8.dp)
            OtpInputField(
                digitsCount = 6,
                value = "123456",
                onValue = { value = it },
                onErrorShown = {},
                modifier = Modifier.padding(16.dp),
                error = "fdfsfsdfsdfsdf",
            )
        }
    }
}
