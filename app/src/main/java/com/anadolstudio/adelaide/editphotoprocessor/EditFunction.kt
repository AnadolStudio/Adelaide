package com.anadolstudio.adelaide.editphotoprocessor

import android.graphics.Bitmap

interface EditFunction {

    val type: String

    fun process(bitmap: Bitmap): Bitmap

}
