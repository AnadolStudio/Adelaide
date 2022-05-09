package com.anadolstudio.photoeditorprocessor.functions.filter

import com.anadolstudio.photoeditorprocessor.functions.EditFunction
import com.anadolstudio.photoeditorprocessor.functions.FuncItem

class FilterFunction : EditFunction.Abstract.Base(FuncItem.MainFunctions.FILTER) {
    private lateinit var filter: String

    fun setPath(filter: String) {
        this.filter = filter
    }

}