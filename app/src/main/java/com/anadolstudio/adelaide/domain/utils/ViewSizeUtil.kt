package com.anadolstudio.adelaide.domain.utils

import android.graphics.Bitmap
import android.graphics.Point
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.anadolstudio.photoeditorprocessor.util.BitmapUtil
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object ViewSizeUtil {

    fun changeViewSize(bitmap: Bitmap, view: View, workspaceSize: Point) {
        with(workspaceSize) {
            val pair = getSupportPair(x, y, bitmap.width, bitmap.height)
            changeViewSize(
                view,
                if (pair.first > pair.second) MATCH_PARENT else WRAP_CONTENT,
                if (pair.first > pair.second) WRAP_CONTENT else MATCH_PARENT
            )
        }
    }

    private fun getSupportPair(
        widthInto: Int, heightInto: Int, widthOut: Int, heightOut: Int
    ): Pair<Int, Int> {
        val ratio: Float = com.anadolstudio.photoeditorprocessor.util.BitmapUtil.getScaleRatioMax(widthInto, heightInto, widthOut, heightOut)
        val delaW: Int = abs(widthInto - widthOut * ratio).toInt()
        val deltaH: Int = abs(heightInto - heightOut * ratio).toInt()

        return Pair(delaW, deltaH)
    }

    fun changeViewSize(view: View, layoutParamsWidth: Int, layoutParamsHeight: Int) {
        view.layoutParams.width = layoutParamsWidth
        view.layoutParams.height = layoutParamsHeight
        view.requestLayout()
    }

    fun changeViewSize(widthOut: Int, heightOut: Int, widthInto: Int, heightInto: Int): Point {
        val pair = getSupportPair(widthInto, heightInto, widthOut, heightOut)

        var w = if (pair.first > pair.second) MATCH_PARENT else WRAP_CONTENT
        var h = if (pair.first > pair.second) WRAP_CONTENT else MATCH_PARENT

        if (w == WRAP_CONTENT) { // Нужны реальные пиксели
            val scale = heightInto / heightOut.toFloat()
            w = (widthOut.toFloat() * scale).toInt()
        }

        if (h == WRAP_CONTENT) {
            val scale = widthInto / widthOut.toFloat()
            h = (heightOut.toFloat() * scale).toInt()
        }
        return Point(w, h)
    }

    fun fitViewToEdge(
        widthContainer: Int,
        heightContainer: Int,
        source: Bitmap,
        view: View
    ) {
        val ratio = min(
            com.anadolstudio.photoeditorprocessor.util.BitmapUtil.getScaleRatio(source.height.toFloat(), heightContainer.toFloat()),
            com.anadolstudio.photoeditorprocessor.util.BitmapUtil.getScaleRatio(source.width.toFloat(), widthContainer.toFloat())
        )

        val realWidthBitmap = source.width * if (ratio >= 1) 1 / ratio else ratio
        val realHeightBitmap = source.height * if (ratio >= 1) 1 / ratio else ratio

        val scale = max(
            widthContainer / realWidthBitmap,
            heightContainer / realHeightBitmap
        )

        view.scaleX = scale
        view.scaleY = scale
    }
}