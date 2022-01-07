package com.anadolstudio.adelaide.ui.screens.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ItemGalleryBinding
import com.anadolstudio.adelaide.domain.utils.ImageLoader
import com.anadolstudio.adelaide.ui.adapters.SimpleAdapter
import com.anadolstudio.core.adapters.AbstractViewHolder
import com.anadolstudio.core.adapters.ActionClick
import com.anadolstudio.core.adapters.ILoadMore

class GalleryImageAdapter(
        data: List<String>,
        detailable: ActionClick<String>,
        private val loadMore: ILoadMore?
) : SimpleAdapter<String>(data.toMutableList(), detailable) {

    private var isLoading = false

    fun getData() = dataList.toList() // TODO Переписать подгрузку

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): AbstractViewHolder.Base<String> =
            GalleryViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false),
                    detailable
            )

    override fun onViewAttachedToWindow(holder: AbstractViewHolder.Base<String>) {
        super.onViewAttachedToWindow(holder)
        val position = holder.adapterPosition

        if (position == itemCount - 1 && !isLoading) {
            isLoading = true
            loadMore?.loadMore() // TODO нужна иная логика за пределами onViewAttachedToWindow?
            isLoading = false
        }
    }

    class GalleryViewHolder(
            view: View,
            detailable: ActionClick<String>?
    ) : AbstractViewHolder.Base<String>(view, detailable), View.OnClickListener {

        val binding = ItemGalleryBinding.bind(view)

        override fun onBind(data: String) {
            ImageLoader.loadImage(binding.imageView, data, ImageLoader.ScaleType.CENTER_CROP, true)
            super.onBind(data)
        }
    }
}
