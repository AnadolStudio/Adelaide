package com.anadolstudio.adelaide.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.anadolstudio.adelaide.R
import com.anadolstudio.core.adapters.AbstractAdapter
import com.anadolstudio.core.interfaces.IDetailable

open class SimpleAdapter<Data>(
    data: List<Data>,
    detailable: IDetailable<Data>,
) : AbstractAdapter<Data, SimpleViewHolder<Data>>(data.toMutableList(), detailable) {

    open fun getViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder<Data> =
        SimpleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_image_list, parent, false),
            detailable,
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder<Data> =
        getViewHolder(parent, viewType)

}
