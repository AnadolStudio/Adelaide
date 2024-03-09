package com.anadolstudio.compose.ui.view.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.core.text.HtmlCompat
import com.anadolstudio.compose.ui.theme.AdelaideTheme

@Composable
fun buildHtmlWithClickable(
    text: String,
    clickablePart: String,
    annotation: String = "",
    isUnderlined: Boolean = false,
    colorLink: Color = AdelaideTheme.colors.textQuaternary,
): AnnotatedString {
    val spannedText = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    val startIndex = spannedText.toString().indexOf(clickablePart)
    val endIndex = startIndex + clickablePart.length

    return buildAnnotatedString {
        append(spannedText.toString())
        applyHtmlStyle(spannedText, colorLink)
        applyClickableStyle(startIndex, endIndex, isUnderlined, annotation)
    }
}
