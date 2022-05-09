package com.anadolstudio.photoeditorprocessor.processor.implementation

import android.content.Context
import android.graphics.Bitmap
import com.anadolstudio.core.tasks.RxTask
import com.anadolstudio.photoeditorprocessor.processor.EditProcessorContract
import com.anadolstudio.photoeditorprocessor.processor.NullBitmapException

class EditProcessorStudy : EditProcessorContract.Abstract() {

    override fun decodeOriginalBitmapWithProcess(context: Context, path: String): Bitmap =
        getCurrentImage()

    override fun processPreview(support: Bitmap?) = RxTask.Base.Quick {
        val func = containerFunctions[containerFunctions.keys.last()]
            ?: throw IllegalArgumentException("Function is null")

        getCurrentImage().let { func.process(it, support) }
    }.onSuccess { bitmap -> currentBitmap = bitmap }

    override fun processAll(bitmap: Bitmap): Bitmap = TODO("Not implement")

}
