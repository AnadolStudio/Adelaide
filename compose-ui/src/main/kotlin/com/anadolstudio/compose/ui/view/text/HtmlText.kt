package com.anadolstudio.compose.ui.view.text

import android.graphics.Typeface.BOLD
import android.graphics.Typeface.BOLD_ITALIC
import android.graphics.Typeface.ITALIC
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.text.util.Linkify
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.AdelaideTypography

@Composable
fun HtmlText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = AdelaideTypography.textBook18,
    color: Color = AdelaideTheme.colors.textPrimary,
    colorLink: Color = AdelaideTheme.colors.textQuaternary,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Visible,
    onTextLayout: (TextLayoutResult) -> Unit = { },
    onUrlClick: (String) -> Unit = { },
) {
    val correctText = text.replace("<li>".toRegex(), "<li> \u2022 ")
    val htmlText = HtmlCompat.fromHtml(correctText, HtmlCompat.FROM_HTML_MODE_LEGACY)
    val annotatedText = remember(correctText) {
        htmlText.toAnnotatedString(colorLink)
    }

    SelectionContainer(modifier = modifier) {
        ClickableText(
            text = annotatedText,
            style = style,
            color = color,
            maxLines = maxLines,
            overflow = overflow,
            onTextLayout = onTextLayout,
            onClick = { position -> annotatedText.handleClickableAt(position, onUrlClick) }
        )
    }
}

private fun Spanned.toAnnotatedString(colorLink: Color): AnnotatedString =
    buildAnnotatedString {
        applyHtmlStyle(this@toAnnotatedString, colorLink)
        append(this@toAnnotatedString.toString())
    }

internal fun AnnotatedString.Builder.applyHtmlStyle(spanned: Spanned, colorLink: Color) {
    val styleSpans = spanned.getSpans<StyleSpan>()
    val underlineSpans = spanned.getSpans<UnderlineSpan>()
    val strikethroughSpans = spanned.getSpans<StrikethroughSpan>()
    val urlSpans = spanned.getSpans<URLSpan>()
    val bulletSpans = spanned.getSpans<BulletSpan>()

    styleSpans.forEach { styleSpan ->
        val start = spanned.getSpanStart(styleSpan)
        val end = spanned.getSpanEnd(styleSpan)

        val style = when (styleSpan.style) {
            BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
            ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
            BOLD_ITALIC -> SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
            else -> null
        }

        style?.let { addStyle(it, start, end) }
    }
    underlineSpans.forEach { underlineSpan ->
        val start = spanned.getSpanStart(underlineSpan)
        val end = spanned.getSpanEnd(underlineSpan)
        addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
    }
    strikethroughSpans.forEach { strikethroughSpan ->
        val start = spanned.getSpanStart(strikethroughSpan)
        val end = spanned.getSpanEnd(strikethroughSpan)
        addStyle(SpanStyle(textDecoration = TextDecoration.LineThrough), start, end)
    }
    bulletSpans.forEach { bulletSpan ->
        val start = spanned.getSpanStart(bulletSpan)
        val end = spanned.getSpanEnd(bulletSpan)
        addStyle(SpanStyle(), start, end)
    }
    urlSpans.forEach { urlSpan ->
        val start = spanned.getSpanStart(urlSpan)
        val end = spanned.getSpanEnd(urlSpan)

        Linkify.addLinks(SpannableString(urlSpan.url), Linkify.WEB_URLS)
        addStyle(SpanStyle(color = colorLink), start, end)
        addStringAnnotation(CLICKABLE_TEXT_TAG, urlSpan.url, start, end)
    }
}
