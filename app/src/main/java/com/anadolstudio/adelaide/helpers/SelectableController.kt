package com.anadolstudio.adelaide.helpers

import android.util.Log
import com.anadolstudio.adelaide.interfaces.ISelectableItem

abstract class SelectableController<T> : ISelectableItem<T> {
    private var currentSelectedItem: T? = null
    private var state = -1

    override fun getState(): Int {
        return state
    }

    override fun setCurrentSelectedItem(t: T?) {
        currentSelectedItem?.let { updateView(it, false) }

        currentSelectedItem = t
        currentSelectedItem ?: let {
            state = -1
            return
        }
        currentSelectedItem?.let {
            state = saveState(it)
            updateView(it, true)
        }
    }

    abstract override fun updateView(t: T, isSelected: Boolean)

    abstract override fun saveState(t: T): Int

    fun selectableItemIsExist() = currentSelectedItem != null
}