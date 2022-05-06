package com.anadolstudio.adelaide.view.screens.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ItemGalleryBinding
import com.anadolstudio.adelaide.domain.utils.ImageLoader
import com.anadolstudio.adelaide.view.adapters.SimpleAdapter
import com.anadolstudio.core.adapters.AbstractViewHolder
import com.anadolstudio.core.adapters.BaseDiffUtilCallback
import com.anadolstudio.core.interfaces.IDetailable
import com.anadolstudio.core.interfaces.ILoadMore

class GalleryAdapter(
    data: List<String>,
    detailable: IDetailable<String>,
    val loadMore: ILoadMore?
) : SimpleAdapter<String>(data.toMutableList(), detailable) {

    private var isLoading = false

    fun getData() = dataList.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder.Base<String> =
        GalleryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false),
            detailable
        )

    override fun onViewAttachedToWindow(holder: AbstractViewHolder.Base<String>) {
        super.onViewAttachedToWindow(holder)
        val position = holder.adapterPosition

        if (position == itemCount - 1 && !isLoading) {
            isLoading = true
            loadMore?.loadMore()
            isLoading = false
        }
    }

    class GalleryViewHolder(
        view: View,
        detailable: IDetailable<String>
    ) : AbstractViewHolder.Base<String>(view, detailable), View.OnClickListener {

        val binding = ItemGalleryBinding.bind(view)

        override fun onBind(data: String) {
            ImageLoader.loadImage(binding.imageView, data, ImageLoader.ScaleType.CENTER_CROP)
            super.onBind(data)
        }
    }
}