package com.anadolstudio.photoeditorprocessor.functions.implementation

import com.anadolstudio.photoeditorprocessor.functions.EditFunction
import com.anadolstudio.photoeditorprocessor.functions.FuncItem

class EffectFunction : EditFunction.Abstract(FuncItem.MainFunctions.EFFECT) {
    private lateinit var pathEffect: String

    fun setPath(pathEffect: String) {
        this.pathEffect = pathEffect
    }

    override fun reboot() {
        pathEffect = ""
    }
}