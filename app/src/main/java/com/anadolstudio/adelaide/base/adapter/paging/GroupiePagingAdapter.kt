package com.anadolstudio.adelaide.base.adapter.paging

import com.anadolstudio.ui.adapters.groupie.BaseGroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section

open class GroupiePagingAdapter(
        vararg sections: Section,
        private val onNeedLoadMoreData: () -> Unit,
        private val prefetchDistance: Int = PREFETCH_DISTANCE
) : BaseGroupAdapter(sections = sections) {

    private companion object {
        const val PREFETCH_DISTANCE = 10
    }

    override fun onBindViewHolder(holder: GroupieViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)

        if (position == itemCount - prefetchDistance || itemCount < prefetchDistance && position == 0) onNeedLoadMoreData()
    }

}
