package com.anadolstudio.photoeditorprocessor.functions.sticker

import android.graphics.Bitmap
import com.anadolstudio.photoeditorprocessor.functions.EditFunction
import com.anadolstudio.photoeditorprocessor.functions.FuncItem
import com.anadolstudio.photoeditorprocessor.util.BitmapCommonUtil

class StickerFunction : EditFunction.Abstract.Base(FuncItem.MainFunctions.STICKER) {

    override fun process(main: Bitmap, support: Bitmap?): Bitmap {
        val supportScale = support?.let {
            val scale = if (main.width > main.height) {//Land
                BitmapCommonUtil.getScaleRatio(it.width.toFloat(), main.width.toFloat())
            } else {//Port
                BitmapCommonUtil.getScaleRatio(it.height.toFloat(), main.height.toFloat())
            }
            if (scale > 1) Bitmap.createScaledBitmap(
                it, (it.width * scale).toInt(), (it.height * scale).toInt(), true
            )
            else it
        }

        return super.process(main, supportScale)
    }
}