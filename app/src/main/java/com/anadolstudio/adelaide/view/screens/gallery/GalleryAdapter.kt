package com.anadolstudio.adelaide.view.screens.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.domain.utils.ImageLoader
import com.anadolstudio.adelaide.view.adapters.SimpleAdapter
import com.anadolstudio.adelaide.view.adapters.SimpleViewHolder
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

    override fun getViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder<String> =
        GalleryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false),
            detailable
        )

    override fun setData(list: MutableList<String>) {
        //TODO Внедрить DifUtil в абстракцию
        val diffUtilCallback = BaseDiffUtilCallback(dataList, list)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback, false)

        dataList = list
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onViewAttachedToWindow(holder: SimpleViewHolder<String>) {
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
    ) : SimpleViewHolder<String>(view, detailable), View.OnClickListener {

        override fun scaleType() = ImageLoader.ScaleType.CENTER_CROP
    }
}