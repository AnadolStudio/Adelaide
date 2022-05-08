package com.anadolstudio.adelaide.domain.editphotoprocessor

import android.content.Context
import android.graphics.Bitmap
import com.anadolstudio.adelaide.domain.editphotoprocessor.functions.EditFunction
import com.anadolstudio.adelaide.domain.editphotoprocessor.functions.FuncItem
import com.anadolstudio.adelaide.domain.editphotoprocessor.functions.implementation.TransformFunction
import com.anadolstudio.adelaide.domain.editphotoprocessor.util.BitmapSaver
import com.anadolstudio.adelaide.domain.editphotoprocessor.util.BitmapUtil
import com.anadolstudio.adelaide.domain.editphotoprocessor.util.BitmapUtil.MAX_SIDE_COPY
import com.anadolstudio.core.tasks.ProgressListener
import com.anadolstudio.core.tasks.RxTask
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

        BitmapSaver.Factory.save(progressListener, context, bitmap, file)
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

        for (f in containerFunctions.values) {

            (f as? TransformFunction)?.let {
                originalBitmap?.let {

                    f.scale = BitmapUtil.getScaleRatio(
                        bitmap.width.toFloat(),
                        bitmap.height.toFloat(),
                        it.width.toFloat(),
                        it.height.toFloat()
                    )

                }
            }

            result = f.process(bitmap)
        }

        return result ?: bitmap
    }

    override fun getOriginalImage(): Bitmap = originalBitmap ?: throw NullBitmapException()

    override fun getCurrentImage(): Bitmap? = currentBitmap ?: originalBitmap

    override fun clear() {
        originalBitmap?.recycle()
        currentBitmap?.recycle()
    }

    override fun getFunction(type: FuncItem.MainFunctions): EditFunction? = containerFunctions[type.name]

    override fun addFunction(func: EditFunction) {
        containerFunctions.add(func)
    }

}
