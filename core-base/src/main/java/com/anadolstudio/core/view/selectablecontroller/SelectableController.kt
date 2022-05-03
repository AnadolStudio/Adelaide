package com.anadolstudio.core.view.selectablecontroller

interface SelectableController<T> {

    fun setCurrentSelectedItem(t: T?)

    fun updateView(t: T, isSelected: Boolean, state: Int)

    fun saveState(t: T): Int

    fun getState(): Int

    abstract class Abstract<T> : SelectableController<T> {
        private var currentSelectedItem: T? = null
        private var state = -1

        abstract override fun updateView(t: T, isSelected: Boolean, state: Int)

        abstract override fun saveState(t: T): Int

        override fun getState(): Int = state

        override fun setCurrentSelectedItem(t: T?) {
            currentSelectedItem?.also { updateView(it, false, state) }

            currentSelectedItem = t
            currentSelectedItem ?: also {
                state = -1
                return
            }

            currentSelectedItem?.also {
                state = saveState(it)
                updateView(it, true, state)
            }
        }

        fun clear() {
            setCurrentSelectedItem(null)
        }

        fun setStartItem(t: T) {
            currentSelectedItem ?: let {
                currentSelectedItem = t
                state = saveState(t)
            }
        }

        fun selectableItemIsExist() = currentSelectedItem != null

    }

}