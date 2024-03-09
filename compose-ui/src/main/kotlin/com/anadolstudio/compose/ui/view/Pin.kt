package com.anadolstudio.compose.ui.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.AdelaideTypography
import com.anadolstudio.compose.ui.theme.LicardTheme
import com.anadolstudio.compose.ui.view.text.Text

@Composable
fun PinField(
    digitsCount: Int,
    value: String,
    modifier: Modifier = Modifier,
) {
    val textFieldValue = TextFieldValue(text = value, selection = TextRange(value.length))

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        val toolbarPosition = remember { mutableStateOf<Rect?>(null) }

        PinField(
            textValue = textFieldValue,
            digitsCount = digitsCount,
            modifier = Modifier.onGloballyPositioned { coordinates ->
                toolbarPosition.value = coordinates.boundsInWindow()
            },
        )
    }
}

@Composable
private fun PinField(
    textValue: TextFieldValue,
    digitsCount: Int,
    modifier: Modifier = Modifier,
) {
    var focused by remember { mutableStateOf(false) }

    BasicTextField(
        value = textValue,
        onValueChange = { },
        readOnly = true,
        modifier = modifier.onFocusChanged { focusState -> focused = focusState.hasFocus },
        decorationBox = {
            PinInputContent(
                textValue = textValue,
                digitsCount = digitsCount,
            )
        }
    )
}

@Composable
private fun PinInputContent(
    textValue: TextFieldValue,
    digitsCount: Int,
) {
    Row {
        repeat(digitsCount) { index ->
            if (index != 0) Spacer(modifier = Modifier.width(10.dp))
            PinChar(
                value = textValue.text.getOrNull(index)?.toString().orEmpty(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PinChar(
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        AnimatedContent(
            targetState = value,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center,
        ) { targetValue ->
            val maskedValue = PasswordVisualTransformation().filter(AnnotatedString(targetValue))
            Text(
                text = maskedValue.text,
                style = AdelaideTypography.titleBook44,
                textAlign = TextAlign.Center
            )
        }
        VSpacer(12.dp)
        Divider(
            color = AdelaideTheme.colors.divider,
            thickness = 1.dp
        )
    }
}

@Suppress("MagicNumber")
@Composable
fun PinKeyboard(
    onKeyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    leftAction: @Composable (BoxScope.() -> Unit)? = null,
    onLeftActionClick: () -> Unit = {},
    rightAction: @Composable (BoxScope.() -> Unit)? = null,
    onRightActionClick: () -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 26.dp),
        content = {
            items((1..9).toList()) { key ->
                val keyValue = key.toString()
                PinButton(
                    onClick = { onKeyClick(keyValue) },
                    content = { NumberKey(keyValue) },
                )
            }
            item {
                if (leftAction != null) {
                    PinButton(content = leftAction, onClick = onLeftActionClick)
                } else {
                    Box(modifier = Modifier.fillMaxSize())
                }
            }
            item {
                PinButton(
                    onClick = { onKeyClick("0") },
                    content = { NumberKey("0") },
                )
            }
            item {
                if (rightAction != null) {
                    PinButton(content = rightAction, onClick = onRightActionClick)
                } else {
                    Box(modifier = Modifier.fillMaxSize())
                }
            }
        }
    )
}

@Composable
fun PinButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val color by animateColorAsState(
        if (isPressed) AdelaideTheme.colors.textSecondary else AdelaideTheme.colors.textPrimary
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
    ) {
        CompositionLocalProvider(LocalContentColor provides color) {
            content()
        }
    }
}

@Composable
private fun NumberKey(number: String) {
    Text(number, style = AdelaideTypography.titleBook44)
}

@Preview(showBackground = true)
@Composable
private fun PinFieldPreview() {
    LicardTheme {
        PinField(
            digitsCount = 4,
            value = "23",
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun KeyboardPreview() {
    LicardTheme {
        PinKeyboard(onKeyClick = {})
    }
}
