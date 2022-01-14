package com.anadolstudio.adelaide.helpers

import android.util.Log
import com.anadolstudio.adelaide.interfaces.ISelectableAdapterItem

abstract class SelectableAdapterController<T> : ISelectableAdapterItem<T> {
    private var currentSelectedItem: T? = null
    private var state = -1

    override fun getState(): Int {
        return state
    }

    override fun setCurrentSelectedItem(t: T?) {
        currentSelectedItem?.let { updateView(state) }

        currentSelectedItem = t
        currentSelectedItem ?: let {
            state = -1
            return
        }
        currentSelectedItem?.let {
            state = saveState(it)
            updateView(state)
        }
    }

    abstract override fun updateView(state: Int)

    abstract override fun saveState(t: T): Int

    fun setStartItem(t: T) {
        currentSelectedItem ?: let {
            currentSelectedItem = t
            state = saveState(t)
        }
    }

    fun selectableItemIsExist() = currentSelectedItem != null
}