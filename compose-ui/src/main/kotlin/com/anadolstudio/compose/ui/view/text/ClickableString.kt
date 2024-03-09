package com.anadolstudio.compose.ui.view.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import com.anadolstudio.compose.ui.theme.AdelaideTheme

const val CLICKABLE_TEXT_TAG: String = "CLICKABLE"

@Composable
fun buildStringWithClickable(
    text: String,
    clickablePart: String,
    annotation: String = "",
    isUnderlined: Boolean = false,
): AnnotatedString = buildAnnotatedString {
    val startIndex = text.indexOf(clickablePart)
    val endIndex = startIndex + clickablePart.length

    append(text)
    applyClickableStyle(startIndex, endIndex, isUnderlined, annotation)
}

@Suppress("ComposableNaming")
@Composable
internal fun AnnotatedString.Builder.applyClickableStyle(
    startIndex: Int,
    endIndex: Int,
    isUnderlined: Boolean,
    annotation: String,
) {
    addStyle(
        style = SpanStyle(color = AdelaideTheme.colors.buttonPrimary),
        start = startIndex,
        end = endIndex,
    )

    if (isUnderlined) {
        addStyle(
            style = SpanStyle(textDecoration = TextDecoration.Underline),
            start = startIndex,
            end = endIndex,
        )
    }

    addStringAnnotation(
        tag = CLICKABLE_TEXT_TAG,
        annotation = annotation,
        start = startIndex,
        end = endIndex,
    )
}

inline fun AnnotatedString.handleClickableAt(offset: Int, handle: (annotation: String) -> Unit) {
    getStringAnnotations(tag = CLICKABLE_TEXT_TAG, start = offset, end = offset)
        .firstOrNull()
        ?.let { handle(it.item) }
}
