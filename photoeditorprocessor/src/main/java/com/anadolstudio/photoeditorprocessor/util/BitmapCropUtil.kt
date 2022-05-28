package com.anadolstudio.photoeditorprocessor.util

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

object BitmapCropUtil {

    fun cropBitmap(
        src: Bitmap,
        points: FloatArray,
        degreesRotated: Int,
        fixAspectRatio: Boolean,
        aspectRatioX: Int,
        aspectRatioY: Int,
        flipHorizontally: Boolean,
        flipVertically: Boolean
    ): Bitmap {
        var result: Bitmap? = null

        try {
            // adjust crop points by the sampling because the image is smaller
            result = cropBitmapObjectWithScale(
                src,
                points,
                degreesRotated,
                fixAspectRatio,
                aspectRatioX,
                aspectRatioY,
                1f,
                flipHorizontally,
                flipVertically
            )

        } catch (e: OutOfMemoryError) {
            result?.recycle()
            throw e
        }

        return result
    }

    private fun cropBitmapObjectWithScale(
        bitmap: Bitmap,
        points: FloatArray,
        degreesRotated: Int,
        fixAspectRatio: Boolean,
        aspectRatioX: Int,
        aspectRatioY: Int,
        scale: Float,
        flipHorizontally: Boolean,
        flipVertically: Boolean
    ): Bitmap {

        // get the rectangle in original image that contains the required cropped area (larger for non
        // rectangular crop)
        val rect = if (points.isNotEmpty()) {
            getRectFromPoints(
                points,
                bitmap.width,
                bitmap.height,
                fixAspectRatio,
                aspectRatioX,
                aspectRatioY
            )
        } else {
            Rect(0, 0, bitmap.width, bitmap.height)
        }

        // crop and rotate the cropped image in one operation
        val matrix = Matrix()
        matrix.setRotate(
            degreesRotated.toFloat(),
            (bitmap.width / 2).toFloat(),
            (bitmap.height / 2).toFloat()
        )
        matrix.postScale(
            if (flipHorizontally) -scale else scale,
            if (flipVertically) -scale else scale
        )
        var result = Bitmap.createBitmap(
            bitmap,
            rect.left,
            rect.top,
            rect.width(),
            rect.height(),
            matrix,
            true
        )
        /*var result = Bitmap.createBitmap(
            bitmap,
            rect.left,
            rect.top,
            rect.width(),
            rect.height()
        )
        result =  Bitmap.createBitmap(
            result, 0, 0,
            result.width, result.height,
            matrix, true
        )*/
        if (result == bitmap) {
            // corner case when all bitmap is selected, no worth optimizing for it
            result = bitmap.copy(bitmap.config, false)
        }

        // rotating by 0, 90, 180 or 270 degrees doesn't require extra cropping
        return result
    }

    private fun getRectFromPoints(
        points: FloatArray,
        imageWidth: Int,
        imageHeight: Int,
        fixAspectRatio: Boolean,
        aspectRatioX: Int,
        aspectRatioY: Int
    ): Rect {
        val left = round(max(0f, getRectLeft(points)))
        val top = round(max(0f, getRectTop(points)))

        val right = round(
            min(imageWidth.toFloat(), getRectRight(points))
        )

        val bottom = round(
            min(imageHeight.toFloat(), getRectBottom(points))
        )

        val rect = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())

        if (fixAspectRatio) {
            fixRectForAspectRatio(rect, aspectRatioX, aspectRatioY)
        }

        return rect
    }

    private fun getRectLeft(points: FloatArray): Float =
        minOf(points[0], points[2], points[4], points[6])

    /** Get top value of the bounding rectangle of the given points.  */
    private fun getRectTop(points: FloatArray): Float =
        minOf(points[1], points[3], points[5], points[7])

    /** Get right value of the bounding rectangle of the given points.  */
    private fun getRectRight(points: FloatArray): Float =
        maxOf(points[0], points[2], points[4], points[6])

    /** Get bottom value of the bounding rectangle of the given points.  */
    private fun getRectBottom(points: FloatArray): Float =
        maxOf(points[1], points[3], points[5], points[7])

    private fun fixRectForAspectRatio(rect: Rect, aspectRatioX: Int, aspectRatioY: Int) {
        if (aspectRatioX == aspectRatioY && rect.width() != rect.height()) {
            if (rect.height() > rect.width()) {
                rect.bottom -= rect.height() - rect.width()
            } else {
                rect.right -= rect.width() - rect.height()
            }
        }
    }
}