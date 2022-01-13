package com.anadolstudio.adelaide.editphotoprocessor

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE
import com.anadolstudio.adelaide.helpers.FunctionItem
import com.anadolstudio.adelaide.helpers.RatioItem

abstract class AbstractFunction(override val type: String) : EditFunction {
    override fun equals(other: Any?): Boolean {
        other ?: return false
        return if ((other as? EditFunction) == null) false
        else this.type == other.type
    }

    abstract fun reboot()
}

class TransformFunction() : AbstractFunction(FunctionItem.TRANSFORM.name), Parcelable {

    companion object CREATOR : Parcelable.Creator<TransformFunction> {
        override fun createFromParcel(parcel: Parcel): TransformFunction {
            return TransformFunction(parcel)
        }

        override fun newArray(size: Int): Array<TransformFunction?> {
            return arrayOfNulls(size)
        }

        const val DEGREES_ROTATE = 90
    }


    var ratioItem: RatioItem = RatioItem.FREE
    var scale = 1F
    var flipHorizontal = false
    var flipVertical = false
    var cropPoints = FloatArray(0)
    var cropWindow: Rect? = null
    var degrees = 0

    var fixAspectRatio = (ratioItem.ratio.x != 0 && ratioItem.ratio.y != 0)

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel?, p1: Int) {
        parcel?.writeFloat(scale)
        parcel?.writeByte(if (flipHorizontal) 1 else 0)
        parcel?.writeByte(if (flipVertical) 1 else 0)
        parcel?.writeFloatArray(cropPoints)
        parcel?.writeInt(degrees)
        parcel?.writeString(ratioItem.name)
        parcel?.writeParcelable(cropWindow, PARCELABLE_WRITE_RETURN_VALUE)
    }

    constructor(parcel: Parcel) : this() {
        scale = parcel.readFloat()
        flipHorizontal = parcel.readByte() != 0.toByte()
        flipVertical = parcel.readByte() != 0.toByte()
        cropPoints = parcel.createFloatArray()!!
        degrees = parcel.readInt()
        ratioItem = RatioItem.valueOf(parcel.readString().toString())
        fixAspectRatio = (ratioItem.ratio.x != 0 && ratioItem.ratio.y != 0)
        cropWindow = parcel.readParcelable(Rect::class.java.classLoader)
    }

    fun setDegree(degrees: Int, w: Int, h: Int) {
        this.degrees = if (degrees < 0) degrees % 360 + 360 else degrees % 360
        // TODO скорее всего тут более сложня логика зависящая от flip (h+v)

        /*val isFlippedHorizontally: Boolean = flipHorizontal
        flipHorizontal = flipVertical
        flipVertical = isFlippedHorizontally*/

//        updateCropWindow(w, h, false, true)
    }

    override fun process(bitmap: Bitmap): Bitmap {
        val points = cropPoints.clone()
        for (i in points.indices) {
            points[i] *= scale
        }

        return BitmapUtils.cropBitmap(
            bitmap,
            points,
            degrees,
            fixAspectRatio,
            ratioItem.ratio.x,
            ratioItem.ratio.y,
            flipHorizontal,
            flipVertical
        )
    }

    override fun reboot() {
        scale = 1F
        flipHorizontal = false
        flipVertical = false
        cropPoints = FloatArray(0)
        degrees = 0
        ratioItem = RatioItem.FREE
    }

    fun flipHorizontally(w: Int, h: Int) {
        flipHorizontal = !flipHorizontal
        updateCropWindow(w, h, true, false)
    }

    fun flipVertically(w: Int, h: Int) {
        flipVertical = !flipVertical
        updateCropWindow(w, h, false, true)
    }

    private fun updateCropWindow(w: Int, h: Int, horizontal: Boolean, vertical: Boolean) {

        val centerX = w / 2F
        val centerY = h / 2F
        var delta: Float
        var tmp: Int
        cropWindow?.let {
            if (horizontal) {
                delta = centerX - it.left
                tmp = (it.left + 2 * delta).toInt()
                delta = centerX - it.right
                it.left = (it.right + 2 * delta).toInt()
                it.right = tmp
            }
            if (vertical) {
                delta = centerY - it.top
                tmp = (it.top + 2 * delta).toInt()
                delta = centerY - it.bottom
                it.top = (it.bottom + 2 * delta).toInt()
                it.bottom = tmp
            }
        }
    }

    fun getCopyWithoutCrop(): TransformFunction {
        val function = TransformFunction()
        function.scale = 1F
        function.flipHorizontal = flipHorizontal
        function.flipVertical = flipVertical
        function.degrees = degrees
        function.ratioItem = ratioItem
        return function
    }


    fun copy(): TransformFunction {
        val function = TransformFunction()
        function.scale = 1F
        function.flipHorizontal = flipHorizontal
        function.flipVertical = flipVertical
        function.degrees = degrees
        function.ratioItem = ratioItem
        function.cropWindow = cropWindow
        function.cropPoints = cropPoints
        return function
    }
}

