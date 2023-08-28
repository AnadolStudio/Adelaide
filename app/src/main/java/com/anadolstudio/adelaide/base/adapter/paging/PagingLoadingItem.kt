package com.anadolstudio.adelaide.base.adapter.paging

import android.view.View
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ItemPagingLoadingBinding
import com.xwray.groupie.viewbinding.BindableItem

class PagingLoadingItem : BindableItem<ItemPagingLoadingBinding>() {

    override fun initializeViewBinding(view: View): ItemPagingLoadingBinding = ItemPagingLoadingBinding.bind(view)

    override fun getId() = layout.toLong()

    override fun getLayout(): Int = R.layout.item_paging_loading

    override fun bind(viewBinding: ItemPagingLoadingBinding, position: Int) = Unit
}
