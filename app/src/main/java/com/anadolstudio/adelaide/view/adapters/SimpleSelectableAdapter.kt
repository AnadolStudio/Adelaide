package com.anadolstudio.adelaide.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.anadolstudio.adelaide.R
import com.anadolstudio.core.adapters.AbstractAdapter
import com.anadolstudio.core.adapters.ActionClick

open class SimpleSelectableAdapter<Data : Any>(
    data: List<Data>,
    detailable: ActionClick<Data>?,
) : AbstractAdapter.Selectable<Data, SelectableViewHolder<Data>>(data.toMutableList(), detailable) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectableViewHolder<Data> =
        SelectableViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_image_list, parent, false),
            detailable,
            selectableController
        )
}