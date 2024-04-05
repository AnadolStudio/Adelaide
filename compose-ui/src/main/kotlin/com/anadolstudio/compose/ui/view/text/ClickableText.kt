package com.anadolstudio.compose.ui.view.text

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun ClickableText(
    text: AnnotatedString,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    color: Color = Color.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator = Modifier.pointerInput(onClick) {
        detectTapGestures { pos ->
            layoutResult.value?.let { layoutResult ->
                onClick(layoutResult.getOffsetForPosition(pos))
            }
        }
    }

    Text(
        text = text,
        modifier = modifier.then(pressIndicator),
        textAlign = textAlign,
        style = style,
        color = color,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = { result ->
            layoutResult.value = result
            onTextLayout(result)
        }
    )
}
