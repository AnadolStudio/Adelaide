package com.anadolstudio.adelaide.base.adapter.paging

import android.view.View
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ItemPagingErrorBinding
import com.xwray.groupie.viewbinding.BindableItem

class PagingErrorItem : BindableItem<ItemPagingErrorBinding>() {

    override fun initializeViewBinding(view: View): ItemPagingErrorBinding = ItemPagingErrorBinding.bind(view)

    override fun getId() = layout.toLong()

    override fun getLayout(): Int = R.layout.item_paging_loading

    override fun bind(viewBinding: ItemPagingErrorBinding, position: Int) = Unit

}
