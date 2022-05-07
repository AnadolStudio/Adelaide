package com.anadolstudio.adelaide.domain.editphotoprocessor

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import com.anadolstudio.adelaide.domain.editphotoprocessor.util.BitmapSaver
import com.anadolstudio.adelaide.domain.editphotoprocessor.util.BitmapUtils
import com.anadolstudio.adelaide.domain.editphotoprocessor.util.BitmapUtils.MAX_SIDE_COPY
import com.anadolstudio.core.tasks.ProgressListener
import com.anadolstudio.core.tasks.RxTask
import java.io.File

class EditProcessorIml(
    val activity: AppCompatActivity,
) : EditProcessorContract {

    companion object {
        val TAG: String = EditProcessorIml::class.java.name
    }

    private lateinit var path: String
    private var originalBitmap: Bitmap? = null
    private var currentBitmap: Bitmap? = null

    override val containerFunctions: LinkedHashMap<String, EditFunction> = LinkedHashMap()

    private fun LinkedHashMap<String, EditFunction>.add(func: EditFunction) {
        put(func.type, func)
    }

    override val applyFuncList: MutableSet<EditFunction> = mutableSetOf()// TODO нужен ли?

    override fun init(path: String): RxTask<Bitmap> = RxTask.Base.Quick {
        this.path = path
        BitmapUtils.decodeBitmapFromContentResolverPath(activity, path, MAX_SIDE_COPY)
    }.onSuccess { result -> originalBitmap = result }

    override fun saveAsBitmap(): RxTask<Bitmap> {
        TODO("Not yet implemented")
    }

    override fun saveAsFile(
        context: Context,
        file: File,
        processListener: ProgressListener<String>
    ) = RxTask.Progress.Quick(processListener) { progressListener ->

        var bitmap = BitmapUtils.decodeBitmapFromContentResolverPath(activity, path)
        bitmap = processAll(bitmap)

        progressListener.onProgress("Setup...")

        BitmapSaver.Factory.save(progressListener, context, bitmap, file)
    }

    override fun processPreview() = RxTask.Base.Quick {
        originalBitmap?.let { processAll(it) }
            ?: throw InvalidateBitmapException("Bitmap is null")
    }.onSuccess { bitmap -> currentBitmap = bitmap }

    override fun process(bitmap: Bitmap, func: EditFunction): Bitmap {
        return func.process(bitmap)
    }

    override fun processAll(bitmap: Bitmap): Bitmap {
        var result: Bitmap? = null

        for (f in containerFunctions.values) {

            (f as? TransformFunction)?.let {
                originalBitmap?.let {

                    f.scale = BitmapUtils.getScaleRatio(
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

    override fun getOriginalImage(): Bitmap? = originalBitmap

    override fun getCurrentImage(): Bitmap? = currentBitmap ?: originalBitmap

    override fun clear() {
        originalBitmap?.recycle()
        currentBitmap?.recycle()
    }

    fun getFunction(type: String): EditFunction? = containerFunctions[type]

    fun add(func: EditFunction) {
        containerFunctions.add(func)
    }


}
