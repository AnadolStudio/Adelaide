package com.anadolstudio.photoeditorprocessor.functions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import com.anadolstudio.photoeditorprocessor.util.BitmapCommonUtil

interface EditFunction {

    val type: FuncItem.MainFunctions

    fun process(main: Bitmap, support: Bitmap? = null): Bitmap
    //TODO support - временное решение, только в рамках задания университета

    abstract class Abstract(override val type: FuncItem.MainFunctions) : EditFunction {

        override fun equals(other: Any?): Boolean {
            other ?: return false
            return if ((other as? EditFunction) == null) false
            else this.type == other.type
        }

        abstract fun reboot()

        override fun hashCode(): Int = type.hashCode()

        override fun toString(): String = "EditFunction(type=$type)"

        override fun process(main: Bitmap, support: Bitmap?): Bitmap =
            with(Bitmap.createBitmap(main.width, main.height, Bitmap.Config.ARGB_8888)) {

                val supportScale = support?.let { BitmapCommonUtil.scaleBitmap(main, it)}
                val canvas = Canvas(this)
                canvas.drawBitmap(main, 0f, 0f, null)

                supportScale?.also {
                    canvas.drawBitmap(
                        BitmapCommonUtil.cropFromSource(
                            main.height, main.width,
                            BitmapCommonUtil.getXSpace(main, it),
                            BitmapCommonUtil.getYSpace(main, it), it
                        ), 0f, 0f, null
                    )
                    it.recycle()
                }
                support?.recycle()
                supportScale?.recycle()
                this
            }

        open class Base(type: FuncItem.MainFunctions) : Abstract(type) {

            override fun reboot() {
                Log.d("EditFunction", "reboot: $type")
            }
        }
    }

}