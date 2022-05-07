package com.anadolstudio.adelaide.domain.editphotoprocessor.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.lang.IllegalArgumentException
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

object BitmapUtils {
    const val MIME_TYPE = "image/*"
    const val MAX_SIDE = 2560
    const val MAX_SIDE_COPY = MAX_SIDE / 2

    private fun getDegree(orientation: Int) = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }

    fun decodeBitmapFromContentResolverPath(
        context: Context,
        path: String,
        minSide: Int = MAX_SIDE
    ): Bitmap {

        // Читаем с inJustDecodeBounds = true для определения размеров
        val options = BitmapFactory.Options()
        var orientation = 1

        options.inJustDecodeBounds = true

        context.contentResolver.openFileDescriptor(Uri.parse(path), "r").use { pfd ->
            pfd?.let {
                BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor, null, options)
                orientation = ExifInterface(pfd.fileDescriptor).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    1
                )
            }
        }
        val degree = getDegree(orientation)

        options.inSampleSize = calculateInSampleSize(options, minSide)
        options.inJustDecodeBounds = false
        var bitmap: Bitmap? = null

        context.contentResolver.openFileDescriptor(Uri.parse(path), "r").use { pfd ->
            bitmap = pfd?.let {
                BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor, null, options)
            } ?: throw IllegalArgumentException("PFD is null")
        }

        if (bitmap == null) throw IllegalArgumentException("Bitmap is null")

        bitmap = scaleBitmap(bitmap!!)
        bitmap = rotate(bitmap!!, degree.toFloat())


        return bitmap!!
    }

    fun scaleBitmap(src: Bitmap, ratio: Float = MAX_SIDE.toFloat()): Bitmap {
        val srcW = src.width.toFloat()
        val srcH = src.height.toFloat()
        val scaleRatio = getScaleRatioMin(ratio, srcW, srcH)
        val scaleW = (srcW * scaleRatio).toInt()
        val scaleH = (srcH * scaleRatio).toInt()

        return Bitmap.createScaledBitmap(src, scaleW, scaleH, true)
    }

    fun getScaleRatioMin(maxSide: Float, supportW: Float, supportH: Float): Float =
        getScaleRatioMin(maxSide, maxSide, supportW, supportH)

    fun getScaleRatioMin(mainW: Float, mainH: Float, supportW: Float, supportH: Float): Float =
        if (supportW > mainW || supportH > mainH)
            min(mainW / supportW, mainH / supportH)
        else 1F

    fun getScaleRatio(mainW: Float, mainH: Float, supportW: Float, supportH: Float): Float =
        if (supportW > mainW && supportH > mainH)
            min(mainW / supportW, mainH / supportH)
        else
            max(mainW / supportW, mainH / supportH)

    fun calculateInSampleSize(
        options: BitmapFactory.Options, maxSide: Int = MAX_SIDE
    ): Int {
        return calculateInSampleSize(options.outWidth, options.outHeight, maxSide)
    }

    fun calculateInSampleSize(
        width: Int, height: Int, maxSide: Int = MAX_SIDE
    ): Int {
        // Реальные размеры изображения
        var inSampleSize = 1

        if (width < maxSide && height < maxSide) return inSampleSize

        if (width > maxSide || height > maxSide) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize > maxSide || halfWidth / inSampleSize > maxSide) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

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

    fun rotate(source: Bitmap, degree: Float): Bitmap {
        if (degree == 0f) return source
        //Если хочешь кастомный градус, то используй editor().setScaleType(ImageView.ScaleType.MATRIX);
        val matrix = createRotateMatrix(degree)
        return Bitmap.createBitmap(
            source, 0, 0,
            source.width, source.height,
            matrix, true
        )
    }

    fun createRotateMatrix(degree: Float): Matrix {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return matrix
    }

    fun flip(source: Bitmap, flipHorizontally: Boolean, flipVertically: Boolean): Bitmap? {
        val matrix: Matrix =
            createFlipMatrix(
                source.width / 2F,
                source.height / 2F,
                flipHorizontally,
                flipVertically
            )
        return Bitmap.createBitmap(
            source, 0, 0,
            source.width, source.height,
            matrix, true
        )
    }

    fun createFlipMatrix(
        centerX: Float,
        centerY: Float,
        flipHorizontally: Boolean,
        flipVertically: Boolean
    ): Matrix {
        val matrix = Matrix()
        val xFlip = if (flipHorizontally) 1 else -1
        val yFlip = if (flipVertically) 1 else -1
        matrix.postScale(xFlip.toFloat(), yFlip.toFloat(), centerX, centerY)
        return matrix
    }
}