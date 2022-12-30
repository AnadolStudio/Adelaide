package com.anadolstudio.adelaide.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.anadolstudio.adelaide.R
import com.anadolstudio.core.adapters.AbstractAdapter
import com.anadolstudio.core.adapters.AbstractViewHolder
import com.anadolstudio.core.adapters.ActionClick

open class SimpleAdapter<Data : Any>(
        data: List<Data>,
        detailable: ActionClick<Data>?,
) : AbstractAdapter.Base<Data, AbstractViewHolder.Base<Data>>(data.toMutableList(), detailable) {

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): AbstractViewHolder.Base<Data> = AbstractViewHolder.Base(
            LayoutInflater.from(parent.context).inflate(R.layout.item_image_list, parent, false),
            detailable,
    )
}
