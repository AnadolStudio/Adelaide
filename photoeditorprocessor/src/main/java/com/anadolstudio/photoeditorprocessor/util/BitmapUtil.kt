package com.anadolstudio.photoeditorprocessor.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object BitmapUtil {
    const val MIME_TYPE = "image/*"
    const val MAX_SIDE = 2560
    const val MAX_SIDE_COPY = MAX_SIDE / 2

    private fun getDegree(orientation: Int) = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }

    fun validateUri(context: Context, path: String): Boolean {
        return context.contentResolver.openFileDescriptor(Uri.parse(path), "r").use {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = false
            BitmapFactory.decodeFileDescriptor(it?.fileDescriptor, null, options) != null
        }
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
            } ?: throw IllegalArgumentException("PFD from path $path is null ")
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

    fun getScaleRatio(current: Float, defaultInt: Float): Float = defaultInt / current

    fun getScaleRatioMax(mainW: Float, mainH: Float, supportW: Float, supportH: Float) =
        max(mainW / supportW, mainH / supportH)

    fun getScaleRatioMax(mainW: Int, mainH: Int, supportW: Int, supportH: Int) =
        getScaleRatioMax(mainW.toFloat(), mainH.toFloat(), supportW.toFloat(), supportH.toFloat())

    fun getScaleRatio(mainW: Float, mainH: Float, supportW: Float, supportH: Float): Float =
        if (supportW > mainW && supportH > mainH)
            min(mainW / supportW, mainH / supportH)
        else
            max(mainW / supportW, mainH / supportH)

    fun calculateInSampleSize(
        options: BitmapFactory.Options, maxSide: Int = MAX_SIDE
    ): Int = calculateInSampleSize(options.outWidth, options.outHeight, maxSide)

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
        val matrix: Matrix = createFlipMatrix(
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

    fun getXSpace(one: Bitmap, two: Bitmap) = abs((two.width - one.width) / 2)

    fun getYSpace(one: Bitmap, two: Bitmap) = abs((two.height - one.height) / 2)

    fun cropFromSource(height: Int, weight: Int, x: Int, y: Int, source: Bitmap): Bitmap =
        Bitmap.createBitmap(source, x, y, weight, height)

    fun scaleBitmap(main: Bitmap, support: Bitmap): Bitmap =
        scaleBitmap(main.width.toFloat(), main.height.toFloat(), support, true)

    fun scaleBitmap(mainW: Float, mainH: Float, support: Bitmap): Bitmap =
        scaleBitmap(mainW, mainH, support, true)

    fun scaleBitmap(main: Bitmap, support: Bitmap, isHard: Boolean): Bitmap =
        scaleBitmap(main.width.toFloat(), main.height.toFloat(), support, isHard)

    fun scaleBitmap(mainW: Float, mainH: Float, support: Bitmap, isHard: Boolean): Bitmap =
        with(support) {
            val scaleRatio = getScaleRatio(
                mainW, mainH,
                width.toFloat(), height.toFloat()
            )

            val scaleW = (if (isHard) mainW else width.toFloat() * scaleRatio).toInt()
            val scaleH = (if (isHard) mainH else height.toFloat() * scaleRatio).toInt()

            Bitmap.createScaledBitmap(support, scaleW, scaleH, true)
        }

    fun centerCrop(source: Bitmap): Bitmap = with(source) {
        val minSide = min(width, height)
        val maxSide = max(width, height)
        val x = if (width >= height) maxSide / 2 - minSide / 2 else 0
        val y = if (width >= height) 0 else maxSide / 2 - minSide / 2
        Bitmap.createBitmap(source, x, y, minSide, minSide)
    }

}