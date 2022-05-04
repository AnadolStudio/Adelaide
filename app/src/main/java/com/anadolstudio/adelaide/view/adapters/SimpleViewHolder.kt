package com.anadolstudio.adelaide.view.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.domain.utils.ImageLoader
import com.anadolstudio.core.adapters.AbstractViewHolder
import com.anadolstudio.core.interfaces.IDetailable
import com.anadolstudio.core.view.selectablecontroller.SelectableController

open class SimpleViewHolder<Data>protected constructor(
    view: View,
    val detailable: IDetailable<Data>,
    selectableMode: Boolean
) : AbstractViewHolder<Data>(view, selectableMode) {

    constructor(view: View, detailable: IDetailable<Data>):this(view, detailable, false)

    var imageView: ImageView? = itemView.findViewById(R.id.imageView)
    var textView: TextView? = itemView.findViewById(R.id.textView)

    init {
        textView?.also { it.visibility = View.GONE }
        itemView.setOnClickListener(::onClick)
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

    override fun getSelectableView(): View = itemView.findViewById(R.id.main_container) ?: itemView

    override fun getSelectableColor(isSelected: Boolean): Int = ContextCompat.getColor(
        itemView.context, if (isSelected) R.color.colorAccent else R.color.colorAccentInverse
    )

    protected open fun scaleType() = ImageLoader.ScaleType.FIT_CENTER

    open class Selectable<Data>(
        view: View,
        val controller: SelectableController.Abstract<in SimpleViewHolder<Data>>,
        detailable: IDetailable<Data>,
    ) : SimpleViewHolder<Data>(view, detailable, true) {

        override fun onClick(view: View) {
            if (selectableMode
                && (!controller.selectableItemIsExist() || controller.getState() != this.adapterPosition)
            ) {
                data?.let { detailable.toDetail(it) }
                controller.setCurrentSelectedItem(this)
            }else{
                super.onClick(view)
            }
        }
    }
}