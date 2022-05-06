package com.anadolstudio.core.adapters

import android.view.View
import androidx.cardview.widget.CardView
import com.anadolstudio.core.adapters.selectablecontroller.SelectableController
import com.anadolstudio.core.interfaces.IDetailable

abstract class AbstractSelectableViewHolder<Data>(
    view: View,
    detailable: IDetailable<Data>,
    val controller: SelectableController<AbstractSelectableViewHolder<Data>>
) : AbstractViewHolder<Data>(view, detailable) {

    open fun onBind(data: Data, isSelected: Boolean) {
        onBind(data)
        onBind(isSelected)
    }

    open fun onBind(isSelected: Boolean) = selectView(isSelected)

    abstract fun getSelectableView(): View

    protected fun selectView(isSelected: Boolean) {
        val view = getSelectableView()
        val color = getSelectableColor(isSelected)

        if (view is CardView)
            view.setCardBackgroundColor(color)
        else
            view.setBackgroundColor(color)
    }

    abstract fun getSelectableColor(isSelected: Boolean): Int

    override fun onClick(view: View) {
        super.onClick(view)
        if (!controller.selectableItemIsExist() || controller.getCurrentPosition() != this.adapterPosition) {
            controller.setCurrentSelectedItem(this)
        }
    }
}