package com.anadolstudio.photoeditorprocessor.functions

import android.graphics.Bitmap

interface EditFunction {

    val type: FuncItem.MainFunctions

    fun process(bitmap: Bitmap): Bitmap

    abstract class Abstract(override val type: FuncItem.MainFunctions) : EditFunction {

        override fun equals(other: Any?): Boolean {
            other ?: return false
            return if ((other as? EditFunction) == null) false
            else this.type == other.type
        }

        abstract fun reboot()

        override fun hashCode(): Int = type.hashCode()
    }

}