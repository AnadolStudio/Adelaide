package com.anadolstudio.compose.ui.view.text.visualTransformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

internal open class NumberVisualTransformation : VisualTransformation {

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

    protected fun Double.formatSeparated(): String {
        val symbols = DecimalFormatSymbols().apply {
            groupingSeparator = GROUP_SEPARATOR
        }
        val formatter = DecimalFormat().apply {
            decimalFormatSymbols = symbols
        }
        return formatter.format(this)
    }

    internal class NumberOffsetMapping(
        private val integerLength: Int,
        private val decimalLength: Int,
    ) : OffsetMapping {

        private val alignmentOffset = (GROUP_SIZE - integerLength).mod(GROUP_SIZE)

        override fun originalToTransformed(offset: Int): Int {
            val alignedOffset = offset + alignmentOffset
            val groupsCountBeforeOffset = (minOf(alignedOffset, integerLength) - 1) / GROUP_SIZE
            return offset + groupsCountBeforeOffset
        }

        override fun transformedToOriginal(offset: Int): Int {
            val alignedOffset = offset + alignmentOffset
            val maxGroupsCount = (integerLength - 1) / GROUP_SIZE
            val groupsCountBeforeOffset =
                (alignedOffset / (GROUP_SIZE + 1)).coerceAtMost(maxGroupsCount)
            return (offset - groupsCountBeforeOffset).coerceIn(0, integerLength + decimalLength)
        }
    }

    protected companion object {
        const val GROUP_SIZE = 3
        const val DECIMAL_SEPARATOR = '.'
        const val GROUP_SEPARATOR = ' '
    }
}
