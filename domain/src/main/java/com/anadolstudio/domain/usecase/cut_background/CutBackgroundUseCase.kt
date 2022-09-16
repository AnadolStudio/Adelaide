package com.anadolstudio.domain.usecase.cut_background

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import com.anadolstudio.core.common_util.ProgressListener
import com.anadolstudio.domain.exceprions.SmallMaskException
import com.anadolstudio.photoeditorcore.domain.photo_segmenter.PhotoSegmenter
import com.anadolstudio.photoeditorcore.domain.util.BitmapCommonUtil
import com.anadolstudio.photoeditorcore.domain.util.BitmapCutUtil
import java.nio.ByteBuffer

class CutBackgroundUseCase {

    companion object {
        private const val CONFIDENCE = 0.35f
    }

    fun process(colorMask: Int, mask: PhotoSegmenter.PhotoSegmenterData): Bitmap {
        val maskBuffer: ByteBuffer = mask.byteBuffer
        val width: Int = mask.width
        val height: Int = mask.height

        val size = width * height
        val bits = IntArray(size)

        var count = 0f

        for (index in 0 until height * width) {
            val foregroundConfidence = maskBuffer.float

            if (foregroundConfidence > CONFIDENCE) {
                bits[index] = colorMask
                count++
                continue
            }
        }

        if (count / size < 0.1f) throw SmallMaskException()

        return Bitmap.createBitmap(bits, width, height, Bitmap.Config.ARGB_8888)
    }

    fun cutByMask(
            context: Context,
            processListener: ProgressListener<String>?,
            mainBitmap: Bitmap,
            drawBitmap: Bitmap
    ): Bitmap {
        processListener?.onProgress(10, "Setup...")

        val bitmap = BitmapCommonUtil.scaleBitmap(mainBitmap, drawBitmap)

        val main = Point(mainBitmap.width, mainBitmap.height)
        val support = Point(bitmap.width, bitmap.height)

        val pixelsOriginal = IntArray(main.x * main.y)
        val pixelsInverseOriginal = IntArray(main.x * main.y)
        val pixelsBitmap = IntArray(support.x * support.y)

        processListener?.onProgress(40, "Cutting...")

        if (pixelsBitmap.size != pixelsOriginal.size) throw IllegalArgumentException()

        mainBitmap.getPixels(pixelsOriginal, 0, main.x, 0, 0, main.x, main.y)
        bitmap.getPixels(pixelsBitmap, 0, support.x, 0, 0, support.x, support.y)
        val edgePixels = BitmapCutUtil.getEdgePixels(bitmap)

        for (i in pixelsOriginal.indices) {
            if (pixelsBitmap[i] == Color.TRANSPARENT) {
                pixelsInverseOriginal[i] = pixelsOriginal[i]
                pixelsOriginal[i] = Color.TRANSPARENT
            }
        }

        val edgePixelsArray = pixelsOriginal.clone()

        for (key in edgePixels.keys) {
            pixelsOriginal[key] = Color.TRANSPARENT
        }

        processListener?.onProgress(80, "Blurring...")

        val result = BitmapCutUtil.blur(context, pixelsOriginal, edgePixelsArray, main.x, main.y)
        result.getPixels(pixelsOriginal, 0, main.x, 0, 0, main.x, main.y)

        val inverseBlur = BitmapCutUtil.blur(context, pixelsInverseOriginal, main.x, main.y)
        inverseBlur.getPixels(pixelsInverseOriginal, 0, main.x, 0, 0, main.x, main.y)

        processListener?.onProgress(100, "Done")

        for (i in pixelsInverseOriginal.indices) {
            val alpha = 255 - Color.alpha(pixelsInverseOriginal[i])
            pixelsOriginal[i] = BitmapCutUtil.getColorWithAlpha(pixelsOriginal[i], alpha)
        }

        return Bitmap.createBitmap(pixelsOriginal, main.x, main.y, Bitmap.Config.ARGB_8888)
    }

}
