package com.anadolstudio.adelaide.base.adapter.paging

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section

open class GroupiePagingAdapter(
        private val onNeedLoadMoreData: () -> Unit,
        private val prefetchDistance: Int = PREFETCH_DISTANCE,
        vararg sections: Section
) : GroupAdapter<GroupieViewHolder>() {

    private companion object {
        const val PREFETCH_DISTANCE = 10
    }

    init {
        sections.forEach { add(it) }
    }

    override fun onBindViewHolder(holder: GroupieViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)

        if (position == itemCount - prefetchDistance || itemCount < prefetchDistance && position == 0) onNeedLoadMoreData()
    }

}
