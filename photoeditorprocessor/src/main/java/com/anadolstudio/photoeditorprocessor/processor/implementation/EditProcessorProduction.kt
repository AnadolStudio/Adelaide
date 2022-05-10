package com.anadolstudio.photoeditorprocessor.processor.implementation

import android.content.Context
import android.graphics.Bitmap
import com.anadolstudio.core.tasks.RxTask
import com.anadolstudio.core.util.BitmapDecoder
import com.anadolstudio.photoeditorprocessor.functions.transform.TransformFunction
import com.anadolstudio.photoeditorprocessor.processor.EditProcessorContract
import com.anadolstudio.photoeditorprocessor.processor.NullBitmapException
import com.anadolstudio.photoeditorprocessor.util.BitmapCommonUtil

class EditProcessorProduction : EditProcessorContract.Abstract() {

    override fun decodeOriginalBitmapWithProcess(context: Context, path: String): Bitmap =
        processAll(
            BitmapDecoder.Manager.decodeBitmapFromPath(
                context,path, BitmapCommonUtil.MAX_SIDE, BitmapCommonUtil.MAX_SIDE
            )
        )

    override fun processPreview(support: Bitmap?) = RxTask.Base.Quick {
        originalBitmap
            ?.let { processAll(it) }
            ?: throw NullBitmapException()
    }.onSuccess { bitmap -> currentBitmap = bitmap }

    override fun processAll(bitmap: Bitmap): Bitmap {
        var result: Bitmap? = null

        for (func in containerFunctions.values) {

            when (func) {
                is TransformFunction -> originalBitmap?.let {
                    func.scale = BitmapCommonUtil.scaleRatioCircumscribed(
                        bitmap.width.toFloat(),
                        bitmap.height.toFloat(),
                        it.width.toFloat(),
                        it.height.toFloat()
                    )
                }

                else -> {}
            }

            result = func.process(bitmap)
        }

        return result ?: bitmap
    }

}
