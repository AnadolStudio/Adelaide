package com.anadolstudio.compose.ui.view.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@Composable
fun MultiStyleText(
    text: String,
    textStyle: TextStyle,
    color: Color,
    styleRange: StyleRange,
    modifier: Modifier = Modifier,
) {
    val annotatedString = buildAnnotatedString {
        var currentPosition = 0
        // Append the text before range with the default style
        withStyle(SpanStyle()) {
            append(text.substring(currentPosition, styleRange.startIndex))
        }
        val style = SpanStyle(
            color = styleRange.textColor,
            background = styleRange.backgroundColor ?: Color.Transparent
        )
        // Append the text in range with the style
        withStyle(style) {
            append(text.substring(styleRange.startIndex, styleRange.endIndex))
        }
        currentPosition = styleRange.endIndex
        // Append the remaining text with the default style
        withStyle(SpanStyle()) {
            append(text.substring(currentPosition))
        }
    }
    Text(
        text = annotatedString,
        style = textStyle,
        color = color,
        modifier = modifier
    )
}

data class StyleRange(
    val startIndex: Int,
    val endIndex: Int,
    val textColor: Color,
    val backgroundColor: Color? = null,
)
