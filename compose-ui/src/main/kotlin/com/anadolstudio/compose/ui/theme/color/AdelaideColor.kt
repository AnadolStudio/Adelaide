@file:Suppress("MagicNumber")

package com.anadolstudio.compose.ui.theme.color

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anadolstudio.compose.ui.view.text.Text

internal object AdelaideColor {
    internal val Black: Color = Color(0xFF000000)
    internal val BlackChinese: Color = Color(0xFF161616)
    internal val BlackEerie: Color = Color(0xFF2A2A2A)
    internal val BlackRaisin: Color = Color(0xFF262626)
    internal val BlackSmoky: Color = Color(0xFF0C0C0C)
    internal val Black_10: Color = Color(0x1A000000)
    internal val Black_20: Color = Color(0x33000000)
    internal val Bronze: Color = Color(0xFFE7D6C7)
    internal val BronzeDark: Color = Color(0xFFDCC0AA)
    internal val BronzeLight: Color = Color(0xFFF4E4D7)
    internal val Ekto92: Color = Color(0xFF52AFEC)
    internal val EktoDiesel: Color = Color(0xFF8865E1)
    internal val EktoPlus95: Color = Color(0xFF6E90A9)
    internal val Gold: Color = Color(0xFFEAA641)
    internal val Golden: Color = Color(0xFFEBDEC5)
    internal val GoldenDark: Color = Color(0xFFE2CCA0)
    internal val GoldenLight: Color = Color(0xFFF3E8D1)
    internal val Gray: Color = Color(0xFF7E7E7E)
    internal val GrayBG: Color = Color(0xFFF2F2F2)
    internal val GrayBright: Color = Color(0xFFEBEBEB)
    internal val GrayCultured: Color = Color(0xFFF8F8F8)
    internal val GrayDark: Color = Color(0xFF666666)
    internal val GrayLight: Color = Color(0xFFCCCCCC)
    internal val GrayMiddle: Color = Color(0xFF999999)
    internal val GrayUltralight: Color = Color(0xFFEEEEEE)
    internal val Green: Color = Color(0xFF2FCF8C)
    internal val GreenDark: Color = Color(0xFF27AE60)
    internal val GrayCharleston: Color = Color(0xFF2A2A2A)
    internal val Orange: Color = Color(0xFFFF6736)
    internal val OrangeDark: Color = Color(0xFFEA4E1C)
    internal val OrangeOrioles: Color = Color(0xFFF44C15)
    internal val Platinum: Color = Color(0xFFE0DBD2)
    internal val PlatinumDark: Color = Color(0xFFDAD4C9)
    internal val PlatinumLight: Color = Color(0xFFF4EFE6)
    internal val Red: Color = Color(0xFFD2233C)
    internal val RedDark: Color = Color(0xFFBD1B32)
    internal val RedLight: Color = Color(0xFFE5233F)
    internal val RedSoft: Color = Color(0xFFFB4761)
    internal val Silver: Color = Color(0xFFDADDDE)
    internal val SilverDark: Color = Color(0xFFD5D8D9)
    internal val SilverLight: Color = Color(0xFFEDEFF0)
    internal val SilverQuick: Color = Color(0xFFA3A3A3)
    internal val White: Color = Color(0xFFFFFFFF)
    internal val White_10: Color = Color(0x1AFFFFFF)
    internal val White_20: Color = Color(0x33FFFFFF)
    internal val Fuel95: Color = Color(0xFF27AE60)
}

@Preview
@Composable
@Suppress("LongMethod", "StringLiteralDuplication")
private fun PalettePreview() = Column {
    Row {
        ColorPreview(AdelaideColor.Black, name = "Black")
        ColorPreview(AdelaideColor.White, name = "White")
        ColorPreview(AdelaideColor.Red, name = "Red")
        ColorPreview(AdelaideColor.Green, name = "Green")
        ColorPreview(AdelaideColor.GreenDark, name = "GreenLight")
    }
    Row {
        ColorPreview(AdelaideColor.RedSoft, name = "RedSoft")
        ColorPreview(AdelaideColor.Ekto92, name = "Ekto92")
        ColorPreview(AdelaideColor.EktoDiesel, name = "EktoDiesel")
        ColorPreview(AdelaideColor.EktoPlus95, name = "EktoPlus95")
    }
    Row {
        ColorPreview(AdelaideColor.GrayCultured, name = "GrayCultured")
        ColorPreview(AdelaideColor.GrayBG, name = "GrayBG")
        ColorPreview(AdelaideColor.GrayUltralight, name = "GrayUltralight")
        ColorPreview(AdelaideColor.GrayBright, name = "GrayBright")
        ColorPreview(AdelaideColor.GrayLight, name = "GrayLight")
        ColorPreview(AdelaideColor.GrayMiddle, name = "GrayMiddle")
    }
    Row {
        ColorPreview(AdelaideColor.Gray, name = "Gray")
        ColorPreview(AdelaideColor.GrayDark, name = "GrayDark")
        ColorPreview(AdelaideColor.GrayCharleston, name = "Green")
    }
    Row {
        ColorPreview(AdelaideColor.BlackEerie, name = "BlackEerie")
        ColorPreview(AdelaideColor.BlackRaisin, name = "BlackRaisin")
        ColorPreview(AdelaideColor.BlackChinese, name = "BlackChinese")
        ColorPreview(AdelaideColor.BlackSmoky, name = "BlackSmoky")
    }
    Row {
        ColorPreview(AdelaideColor.Orange, name = "Orange")
        ColorPreview(AdelaideColor.OrangeOrioles, name = "Orange")
        ColorPreview(AdelaideColor.OrangeDark, name = "Orange")
    }
    Row {
        ColorPreview(AdelaideColor.RedSoft, name = "RedSoft")
        ColorPreview(AdelaideColor.RedLight, name = "RedLight")
        ColorPreview(AdelaideColor.RedDark, name = "RedDark")
    }
    Row {
        ColorPreview(AdelaideColor.Bronze, name = "Bronze")
        ColorPreview(AdelaideColor.BronzeLight, name = "BronzeLight")
        ColorPreview(AdelaideColor.BronzeDark, name = "BronzeDark")
    }
    Row {
        ColorPreview(AdelaideColor.Silver, name = "Silver")
        ColorPreview(AdelaideColor.SilverLight, name = "SilverLight")
        ColorPreview(AdelaideColor.SilverDark, name = "SilverDark")
        ColorPreview(AdelaideColor.SilverQuick, name = "SilverQuick")
    }
    Row {
        ColorPreview(AdelaideColor.Platinum, name = "Platinum")
        ColorPreview(AdelaideColor.PlatinumLight, name = "PlatinumLight")
        ColorPreview(AdelaideColor.PlatinumDark, name = "PlatinumDark")
    }
    Row {
        ColorPreview(AdelaideColor.Golden, name = "Golden")
        ColorPreview(AdelaideColor.GoldenLight, name = "GoldenLight")
        ColorPreview(AdelaideColor.GoldenDark, name = "GoldenDark")
        ColorPreview(AdelaideColor.Gold, name = "Gold")
    }
}

@Composable
private fun ColorPreview(color: Color, name: String) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(color)
            .padding(8.dp)
    ) {
        val textColor = if (color.luminance() < 0.5) Color.White else Color.Black
        Text(name, fontSize = 10.sp, color = textColor)
    }
}
