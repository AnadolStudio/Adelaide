package com.anadolstudio.adelaide.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.anadolstudio.adelaide.R
import com.anadolstudio.core.adapters.AbstractAdapter
import com.anadolstudio.core.interfaces.IDetailable

open class SimpleSelectableAdapter<Data>(
    data: List<Data>,
    detailable: IDetailable<Data>,
) : AbstractAdapter.Selectable<Data, SimpleSelectableViewHolder<Data>>(data.toMutableList(), detailable) {

    open fun getViewHolder(parent: ViewGroup, viewType: Int): SimpleSelectableViewHolder<Data> =
        SimpleSelectableViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_image_list, parent, false),
            selectableController,
            detailable,
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleSelectableViewHolder<Data> =
        getViewHolder(parent, viewType)
}