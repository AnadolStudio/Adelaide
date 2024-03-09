package com.anadolstudio.compose.ui.view.text.visualTransformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TransformedText

internal class VolumeVisualTransformation : NumberVisualTransformation() {

    override fun filter(text: AnnotatedString): TransformedText {
        val components = text.split(DECIMAL_SEPARATOR, limit = 2)
        val formattedText = buildAnnotatedString {
            if (components.first().isNotEmpty()) {
                append(
                    components.first()
                        .toDouble()
                        .formatSeparated()
                        .takeWhile { it != DECIMAL_SEPARATOR }
                )
            }
            if (components.size > 1) {
                append(DECIMAL_SEPARATOR)
                append(components.last())
            }

            if (components.first().isNotEmpty()) {
                append(LITRE)
            }
        }

        val decimalLength = if (components.size > 1) {
            components.last().length + DECIMAL_SEPARATOR.toString().length
        } else {
            0
        }

        return TransformedText(
            text = formattedText,
            offsetMapping = NumberOffsetMapping(
                integerLength = components.first().length,
                decimalLength = decimalLength,
            ),
        )
    }

    private companion object {
        const val LITRE = " \u043B"
    }
}
