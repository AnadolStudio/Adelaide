package com.anadolstudio.adelaide.domain.editphotoprocessor

import android.graphics.Bitmap

interface EditFunction {

    val type: String

    fun process(bitmap: Bitmap): Bitmap

}
