package com.anadolstudio.adelaide.domain.editphotoprocessor

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.RequiresPermission
import com.anadolstudio.core.tasks.ProgressListener
import com.anadolstudio.core.tasks.RxTask
import java.io.File

interface EditProcessorContract {

    val containerFunctions: LinkedHashMap<String, EditFunction>

    val applyFuncList: MutableSet<EditFunction>

    fun init(path: String): RxTask<Bitmap>

    fun saveAsBitmap():RxTask<Bitmap>

    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    fun saveAsFile(
        context: Context, file: File, processListener: ProgressListener<String>
    ): RxTask<String>

    fun processPreview(): RxTask<Bitmap>

    fun process(bitmap: Bitmap, func: EditFunction): Bitmap

    fun processAll(bitmap: Bitmap): Bitmap

    fun getOriginalImage(): Bitmap? // Большая сторона <= 1280

    fun getCurrentImage(): Bitmap?

    fun clear()
}