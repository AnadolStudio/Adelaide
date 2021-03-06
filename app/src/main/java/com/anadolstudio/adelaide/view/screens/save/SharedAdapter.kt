package com.anadolstudio.adelaide.view.screens.save

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ItemShareBinding
import com.anadolstudio.adelaide.domain.editphotoprocessor.shareaction.SharedAction
import com.anadolstudio.adelaide.view.adapters.SimpleAdapter
import com.anadolstudio.core.adapters.AbstractViewHolder
import com.anadolstudio.core.interfaces.IDetailable

class SharedAdapter(
    data: List<SharedAction.SharedItem>,
    detailable: IDetailable<SharedAction.SharedItem>?
) : SimpleAdapter<SharedAction.SharedItem>(data, detailable) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder.Base<SharedAction.SharedItem> = SharedViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_share, parent, false),
        detailable
    )

    private class SharedViewHolder(
        view: View,
        detailable: IDetailable<SharedAction.SharedItem>?
    ) : AbstractViewHolder.Base<SharedAction.SharedItem>(view, detailable) {

        val binding = ItemShareBinding.bind(view)

        override fun onBind(data: SharedAction.SharedItem) {
            binding.shareIcon.setImageDrawable(ContextCompat.getDrawable(itemView.context, data.drawable))
            super.onBind(data)
        }
    }
}