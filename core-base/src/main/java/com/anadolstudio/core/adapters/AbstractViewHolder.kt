package com.anadolstudio.core.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.anadolstudio.core.interfaces.IDetailable

abstract class AbstractViewHolder<Data>(
    view: View,
    val detailable: IDetailable<Data>,
) : RecyclerView.ViewHolder(view) {

    private val clickView: View by lazy { initClickView() }

    init {
        clickView.setOnClickListener(::onClick)
    }

    abstract fun initClickView(): View

    protected var data: Data? = null

    open fun onBind(data: Data) {
        this.data = data
    }

    open fun onClick(view: View) {
        data?.also { detailable.toDetail(it) }
    }

    open class Base<Data>(view: View, detailable: IDetailable<Data>) :
        AbstractViewHolder<Data>(view, detailable) {

        override fun initClickView(): View = itemView
    }
}