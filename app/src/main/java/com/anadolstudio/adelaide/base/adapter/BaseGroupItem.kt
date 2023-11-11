package com.anadolstudio.adelaide.base.adapter

import androidx.viewbinding.ViewBinding
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

abstract class BaseGroupItem<VB : ViewBinding>(id: Long) : BindableItem<VB>(id) {

    override fun bind(viewBinding: VB, position: Int, payloads: MutableList<Any>) {
        val newItem = payloads.filterIsInstance(this::class.java).firstOrNull()

        if (newItem != null) {
            bind(viewBinding, newItem)
        } else {
            super.bind(viewBinding, position, payloads)
        }
    }

    override fun bind(viewBinding: VB, position: Int) = bind(viewBinding, this)

    protected abstract fun bind(viewBinding: VB, item: BaseGroupItem<VB>)

    abstract override fun equals(other: Any?): Boolean

    abstract override fun hashCode(): Int

    override fun getChangePayload(newItem: Item<*>): Any = newItem
}
