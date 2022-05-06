package com.anadolstudio.adelaide.view.adapters

import android.view.View
import androidx.core.content.ContextCompat
import com.anadolstudio.adelaide.R
import com.anadolstudio.core.adapters.AbstractSelectableViewHolder
import com.anadolstudio.core.adapters.selectablecontroller.SelectableController
import com.anadolstudio.core.interfaces.IDetailable

open class SelectableViewHolder<Data>(
    view: View,
    detailable: IDetailable<Data>,
    controller: SelectableController<out AbstractSelectableViewHolder<Data>>,
) : AbstractSelectableViewHolder<Data>(
    view,
    detailable,
    controller as SelectableController<AbstractSelectableViewHolder<Data>>
) {

    override fun getSelectableView(): View = itemView.findViewById(R.id.main_container)
        ?: itemView

    override fun getSelectableColor(isSelected: Boolean): Int = ContextCompat.getColor(
        itemView.context, if (isSelected) R.color.colorAccent else R.color.colorAccentInverse
    )
}