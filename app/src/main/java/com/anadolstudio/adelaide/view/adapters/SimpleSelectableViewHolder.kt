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

open class SimpleSelectableViewHolder<Data>(
    view: View,
    protected val controller: SelectableController.Abstract<in SimpleSelectableViewHolder<Data>>,
    val detailable: IDetailable<Data>,
) : AbstractViewHolder.Selectable<Data>(view) {

    var imageView: ImageView? = itemView.findViewById(R.id.imageView)
    var textView: TextView? = itemView.findViewById(R.id.textView)

    init {
        textView?.also { it.visibility = View.GONE }
        itemView.setOnClickListener(::onClick)
    }

    open fun onClick(view: View) {
        if (!selectableMode)
            data?.let { detailable.toDetail(it) }
        else if (!controller.selectableItemIsExist() || controller.getState() != this.adapterPosition) {
            data?.let { detailable.toDetail(it) }
            controller.setCurrentSelectedItem(this)
        }
    }

    override fun onBind(data: Data, isSelected: Boolean) {
        imageView?.also {
            if (data is String) ImageLoader.loadImage(imageView, data, scaleType())
        }

        super.onBind(data, isSelected)
    }

    override fun getSelectableView(): View = itemView.findViewById(R.id.main_container) ?: itemView

    override fun getSelectableColor(isSelected: Boolean): Int = ContextCompat.getColor(
        itemView.context, if (isSelected) R.color.colorAccent else R.color.colorAccentInverse
    )

    protected fun scaleType() = ImageLoader.ScaleType.FIT_CENTER

}