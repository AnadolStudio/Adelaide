package com.anadolstudio.adelaide.ui.screens.save

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ItemShareBinding
import com.anadolstudio.core.share_util.SharedAction.SharedItem
import com.anadolstudio.adelaide.ui.adapters.SimpleAdapter
import com.anadolstudio.core.adapters.AbstractViewHolder
import com.anadolstudio.core.adapters.ActionClick

class SharedAdapter(
        data: List<SharedItem>,
        detailable: ActionClick<SharedItem>?
) : SimpleAdapter<SharedItem>(data, detailable) {

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): AbstractViewHolder.Base<SharedItem> = SharedViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_share, parent, false),
            detailable
    )

    private class SharedViewHolder(
            view: View,
            detailable: ActionClick<SharedItem>?
    ) : AbstractViewHolder.Base<SharedItem>(view, detailable) {

        val binding = ItemShareBinding.bind(view)

        override fun onBind(data: SharedItem) {
            binding.shareIcon.setImageDrawable(ContextCompat.getDrawable(itemView.context, data.drawable))
            super.onBind(data)
        }
    }
}
