package com.anadolstudio.photoeditorprocessor.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur
import kotlin.math.max
import kotlin.math.min

object BitmapRenderEffects {
    fun blur(context: Context, bitmap: Bitmap, radius: Float = 12F): Bitmap {
        //TODO Vulkan
        val outputBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)
        canvas.drawBitmap(bitmap, 0F, 0F, null)

        val rs = RenderScript.create(context)

        val input = Allocation.createFromBitmap(rs, bitmap)
        val output = Allocation.createTyped(rs, input.type)
        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        script.setRadius(min(25F, max(radius, 1F)))
        script.setInput(input)
        script.forEach(output)
        output.copyTo(outputBitmap)

        rs.destroy()
        return outputBitmap
    }

    fun monochrome(bitmap: Bitmap): Bitmap {
        val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)

        val paint = Paint()
        paint.colorFilter = monochromeFilter
        canvas.drawBitmap(bitmap, 0F, 0F, paint)

        return result
    }

    val monochromeFilter = ColorMatrixColorFilter(
            ColorMatrix(
                    floatArrayOf(
                            0.3F, 0.59F, 0.11F, 0F, 0F,
                            0.3F, 0.59F, 0.11F, 0F, 0F,
                            0.3F, 0.59F, 0.11F, 0F, 0F,
                            0.0F, 0.0F, 0.0F, 1.0F, 0.0F
                    )
            )
    )
}

