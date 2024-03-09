package com.anadolstudio.compose.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anadolstudio.compose.ui.R
import com.anadolstudio.compose.ui.view.text.Text

private const val FUTURA_MEDIUM_FONT_WEIGHT = 450

private val FuturaMediumWeight = FontWeight(FUTURA_MEDIUM_FONT_WEIGHT)

private val FuturaPTFontFamily = FontFamily(
    Font(resId = R.font.futurapt_book, weight = FontWeight.Normal),
    Font(resId = R.font.futurapt_medium, weight = FuturaMediumWeight),
)

val MaterialTypography: Typography = Typography(
    h1 = AdelaideTypography.titleBook44,
    h2 = AdelaideTypography.titleBook34,
    h3 = AdelaideTypography.titleBook28,
    subtitle1 = AdelaideTypography.textBook18,
    body1 = AdelaideTypography.textMedium18,
    body2 = AdelaideTypography.textMedium18,
    button = AdelaideTypography.textMedium18,
    caption = AdelaideTypography.captionBook14,
)

object AdelaideTypography {

    @Suppress("DEPRECATION")
    private val defaultStyle = TextStyle(
        fontFamily = FuturaPTFontFamily,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false,
        ),
    )

    val titleBook44 = defaultStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 44.sp,
        lineHeight = 48.sp,
    )
    val titleBook34 = defaultStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp,
        lineHeight = 40.sp,
    )
    val titleBook28 = defaultStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 34.sp,
    )
    val titleBook28_32 = defaultStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 32.sp,
    )
    val textBook23 = defaultStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 23.sp,
        lineHeight = 28.sp,
    )
    val textBook24 = defaultStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 30.sp,
    )
    val textBook14 = defaultStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 30.sp,
    )
    val textMedium18 = defaultStyle.copy(
        fontWeight = FuturaMediumWeight,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    )
    val textBook18 = defaultStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    )
    val textBook18Underlined = defaultStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        textDecoration = TextDecoration.Underline
    )
    val captionMedium16 = defaultStyle.copy(
        fontWeight = FuturaMediumWeight,
        fontSize = 16.sp,
        lineHeight = 20.sp,
    )
    val captionBook16 = defaultStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    )
    val captionMedium14 = defaultStyle.copy(
        fontWeight = FuturaMediumWeight,
        fontSize = 14.sp,
        lineHeight = 16.sp,
    )
    val captionBook14 = defaultStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 16.sp,
    )
    val captionMedium12 = defaultStyle.copy(
        fontWeight = FuturaMediumWeight,
        fontSize = 12.sp,
        lineHeight = 14.sp,
    )
}

@Preview(showBackground = true)
@Composable
private fun TypographyPreview() {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Text("TitleBook44", style = AdelaideTypography.titleBook44)
        Text("TitleBook34", style = AdelaideTypography.titleBook34)
        Text("TitleBook28", style = AdelaideTypography.titleBook28)
        Text("TitleBook28_32", style = AdelaideTypography.titleBook28)
        Text("TextBook24", style = AdelaideTypography.textBook24)
        Text("TextMedium18", style = AdelaideTypography.textMedium18)
        Text("TextBook18", style = AdelaideTypography.textBook18)
        Text("CaptionMedium16", style = AdelaideTypography.captionMedium16)
        Text("CaptionBook16", style = AdelaideTypography.captionBook16)
        Text("CaptionMedium14", style = AdelaideTypography.captionMedium14)
        Text("CaptionBook14", style = AdelaideTypography.captionBook14)
        Text("CaptionMedium12", style = AdelaideTypography.captionMedium12)
    }
}
