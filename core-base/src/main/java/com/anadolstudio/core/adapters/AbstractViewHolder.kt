package com.anadolstudio.core.adapters

import android.view.View
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

abstract class AbstractViewHolder<Data>(view: View) : RecyclerView.ViewHolder(view) {
    protected var data: Data? = null

    open fun onBind(data: Data) {
        this.data = data
    }

    abstract class Selectable<Data>(view: View) :
        AbstractViewHolder<Data>(view) {
        protected var selectableMode: Boolean = true

        open fun onBind(data: Data, isSelected: Boolean, selectableMode: Boolean = true) {
            this.selectableMode = selectableMode
            onBind(data, isSelected)
        }

        open fun onBind(data: Data, isSelected: Boolean) {
            onBind(data)
            if (selectableMode) onBind(isSelected)
        }

        fun onBind(isSelected: Boolean) = selectView(isSelected)

        abstract fun getSelectableView(): View

        private fun selectView(isSelected: Boolean) {
            val view = getSelectableView()
            val color = getSelectableColor(isSelected)

            if (view is CardView) view.setCardBackgroundColor(color)
            else view.setBackgroundColor(color)
        }

        abstract fun getSelectableColor(isSelected: Boolean): Int
    }
}