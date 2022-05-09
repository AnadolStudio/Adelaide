package com.anadolstudio.photoeditorprocessor

import android.content.Context
import android.graphics.Bitmap
import com.anadolstudio.core.tasks.ProgressListener
import com.anadolstudio.core.tasks.RxTask
import com.anadolstudio.photoeditorprocessor.functions.EditFunction
import com.anadolstudio.photoeditorprocessor.functions.FuncItem
import com.anadolstudio.photoeditorprocessor.functions.implementation.TransformFunction
import com.anadolstudio.photoeditorprocessor.util.BitmapSaver
import com.anadolstudio.photoeditorprocessor.util.BitmapUtil
import com.anadolstudio.photoeditorprocessor.util.BitmapUtil.MAX_SIDE_COPY
import java.io.File

class EditProcessorIml : EditProcessorContract.Base() {

    companion object {
        val TAG: String = EditProcessorIml::class.java.name
    }

    private lateinit var path: String
    private var originalBitmap: Bitmap? = null
    private var currentBitmap: Bitmap? = null

    override val containerFunctions: LinkedHashMap<String, EditFunction> = LinkedHashMap()

    private fun LinkedHashMap<String, EditFunction>.add(func: EditFunction) {
        put(func.type.name, func)
    }

    override val applyFuncList: MutableSet<EditFunction> = mutableSetOf()// TODO нужен ли?

    override fun init(context: Context, path: String): RxTask<Bitmap> = RxTask.Base.Quick {
        this.path = path
        BitmapUtil.decodeBitmapFromContentResolverPath(context, path, MAX_SIDE_COPY)
    }.onSuccess { result -> originalBitmap = result }

    override fun saveAsBitmap(): RxTask<Bitmap> {
        TODO("Not yet implemented")
    }

    override fun saveAsFile(
        context: Context,
        file: File,
        processListener: ProgressListener<String>
    ) = RxTask.Progress.Quick(processListener) { progressListener ->

        var bitmap = BitmapUtil.decodeBitmapFromContentResolverPath(context, path)
        bitmap = processAll(bitmap)

        progressListener.onProgress("Setup...")
        val parent = file.parent ?: throw FileParentException()
        val nameDir = parent.substring(parent.lastIndexOf("/"), parent.length)

        BitmapSaver.Factory.save(progressListener, context, bitmap, nameDir, file)
    }

    override fun processPreview() = RxTask.Base.Quick {
        originalBitmap
            ?.let { processAll(it) }
            ?: throw NullBitmapException()
    }.onSuccess { bitmap -> currentBitmap = bitmap }

    override fun process(bitmap: Bitmap, func: EditFunction): Bitmap {
        return func.process(bitmap)
    }

    override fun processAll(bitmap: Bitmap): Bitmap {
        var result: Bitmap? = null

        for (func in containerFunctions.values) {

            when (func) {
                is TransformFunction -> originalBitmap?.let {
                    func.scale = BitmapUtil.getScaleRatio(
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

    override fun getOriginalImage(): Bitmap = originalBitmap ?: throw NullBitmapException()

    override fun getCurrentImage(): Bitmap = currentBitmap ?: getOriginalImage()

    override fun clear() {
        originalBitmap?.recycle()
        currentBitmap?.recycle()
    }

    override fun getFunction(type: FuncItem.MainFunctions): EditFunction? =
        containerFunctions[type.name]

    override fun addFunction(func: EditFunction) {
        containerFunctions.add(func)
    }

}
