package com.anadolstudio.adelaide.base.adapter

import androidx.viewbinding.ViewBinding
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

abstract class BaseGroupItem<VB : ViewBinding>(id: Long) : BindableItem<VB>(id) {

    override fun hasSameContentAs(other: Item<*>): Boolean = this.id == other.id

}
