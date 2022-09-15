package com.anadolstudio.photoeditorprocessor.processor.implementation

import android.content.Context
import android.graphics.Bitmap
import com.anadolstudio.core.rx_util.quickSingleFrom
import com.anadolstudio.photoeditorprocessor.processor.EditProcessorContract
import io.reactivex.Single

class EditProcessorStudy : EditProcessorContract.Abstract() {

    override fun decodeOriginalBitmapWithProcess(context: Context, path: String): Bitmap =
        getCurrentImage()

    override fun processPreview(support: Bitmap?): Single<Bitmap> = quickSingleFrom {
        val func = containerFunctions[containerFunctions.keys.last()]
                ?: throw IllegalArgumentException("Function is null")

        getCurrentImage().let { func.process(it, support) }
    }.doOnSuccess { bitmap ->
        originalBitmap = bitmap
        currentBitmap = bitmap
    }

    override fun processAll(bitmap: Bitmap): Bitmap = TODO("Not implement")

    fun setCurrentImage(bitmap: Bitmap) {
        currentBitmap = bitmap
    }
}
