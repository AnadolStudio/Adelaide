package com.anadolstudio.photoeditorprocessor.processor

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.RequiresPermission
import com.anadolstudio.core.tasks.ProgressListener
import com.anadolstudio.core.tasks.RxTask
import com.anadolstudio.core.util.BitmapDecoder
import com.anadolstudio.photoeditorprocessor.functions.EditFunction
import com.anadolstudio.photoeditorprocessor.functions.FuncItem
import com.anadolstudio.photoeditorprocessor.util.BitmapCommonUtil
import com.anadolstudio.photoeditorprocessor.util.BitmapSaver
import java.io.File

interface EditProcessorContract {

    val containerFunctions: LinkedHashMap<String, EditFunction>

    val applyFuncList: MutableSet<EditFunction>

    fun init(context: Context, path: String): RxTask<Bitmap>

    fun process(bitmap: Bitmap, func: EditFunction): Bitmap

    fun processAll(bitmap: Bitmap): Bitmap

    fun getOriginalImage(): Bitmap

    fun getCurrentImage(): Bitmap// Большая сторона <= 1280

    fun addFunction(func: EditFunction)

    fun getFunction(type: FuncItem.MainFunctions): EditFunction?

    abstract class Abstract : EditProcessorContract {

        protected lateinit var path: String
        protected var originalBitmap: Bitmap? = null
        protected var currentBitmap: Bitmap? = null

        override val containerFunctions: LinkedHashMap<String, EditFunction> = LinkedHashMap()

        protected fun LinkedHashMap<String, EditFunction>.add(func: EditFunction) {
            put(func.type.name, func)
        }

        override val applyFuncList: MutableSet<EditFunction> = mutableSetOf()
        // TODO будет использоваться для удаления функции в стеке

        override fun init(context: Context, path: String): RxTask<Bitmap> = RxTask.Base.Quick {
            this.path = path
            BitmapDecoder.Manager.decodeBitmapFromPath(
                context, path, BitmapCommonUtil.MAX_SIDE, BitmapCommonUtil.MAX_SIDE
            )
        }.onSuccess { result -> originalBitmap = result }

        override fun getOriginalImage(): Bitmap = originalBitmap ?: throw NullBitmapException()

        override fun getCurrentImage(): Bitmap = currentBitmap ?: getOriginalImage()

        open fun clear() {
            originalBitmap?.recycle()
            currentBitmap?.recycle()
        }

        override fun getFunction(type: FuncItem.MainFunctions): EditFunction? =
            containerFunctions[type.name]

        override fun addFunction(func: EditFunction) {
            containerFunctions.add(func)
        }

        @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
        open fun saveAsFile(
            context: Context,
            file: File,
            processListener: ProgressListener<String>?
        ) = RxTask.Progress.Quick(processListener) { progressListener ->

            val bitmap = decodeOriginalBitmapWithProcess(context, path)
            progressListener?.onProgress("Setup...")
            val parent = file.parent ?: throw FileParentException()
            val nameDir = parent.substring(parent.lastIndexOf("/"), parent.length)

            BitmapSaver.Factory.save(progressListener, context, bitmap, nameDir, file)
        }

        override fun process(bitmap: Bitmap, func: EditFunction): Bitmap = func.process(bitmap)

        open fun reboot() {
            currentBitmap = originalBitmap
        }

        abstract fun processPreview(support: Bitmap? = null): RxTask<Bitmap>

        abstract fun decodeOriginalBitmapWithProcess(context: Context, path: String): Bitmap
    }
}