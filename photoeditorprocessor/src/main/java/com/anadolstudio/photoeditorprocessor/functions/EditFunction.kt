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
                val supportScale = scale(support, main)
                val canvas = Canvas(this)
                canvas.drawBitmap(main, 0f, 0f, null)

                supportScale?.also {

                    canvas.drawBitmap(
                        BitmapCommonUtil.cropFromSource(
                            main.width, main.height,
                            BitmapCommonUtil.getXSpace(main, it),
                            BitmapCommonUtil.getYSpace(main, it),
                            it
                        ), 0f, 0f, null
                    )
                    it.recycle()
                }
                support?.recycle()
                supportScale?.recycle()
                this
            }

        private fun scale(support: Bitmap?, main: Bitmap): Bitmap? {
            val supportScale = support?.let {
                val scale = BitmapCommonUtil.scaleRatioCircumscribed(
                    main.width.toFloat(),
                    main.height.toFloat(),
                    support.width.toFloat(),
                    support.height.toFloat(),
                )

                val scaleW = (it.width * scale).toInt()
                val scaleH = (it.height * scale).toInt()

                Bitmap.createScaledBitmap(it, scaleW, scaleH, true)
            }
            return supportScale
        }

        open class Base(type: FuncItem.MainFunctions) : Abstract(type) {

            override fun reboot() {
                Log.d("EditFunction", "reboot: $type")
            }
        }
    }

}