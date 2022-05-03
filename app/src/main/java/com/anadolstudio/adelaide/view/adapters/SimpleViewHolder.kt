package com.anadolstudio.adelaide.view.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.domain.utils.ImageLoader
import com.anadolstudio.core.adapters.AbstractViewHolder
import com.anadolstudio.core.interfaces.IDetailable

open class SimpleViewHolder<Data>(
    view: View,
    val detailable: IDetailable<Data>,
) : AbstractViewHolder<Data>(view) {

    var imageView: ImageView? = itemView.findViewById(R.id.imageView)
    var textView: TextView? = itemView.findViewById(R.id.textView)
    val actionView: View = itemView

    init {
        textView?.also { it.visibility = View.GONE }
        actionView.setOnClickListener(::onClick)
    }

    open fun onClick(view: View) {
        data?.let { detailable.toDetail(it) }
    }

    override fun onBind(data: Data) {
        imageView?.also {
            if (data is String) ImageLoader.loadImage(imageView, data, scaleType())
        }

        super.onBind(data)
    }

    protected open fun scaleType() = ImageLoader.ScaleType.FIT_CENTER

}
