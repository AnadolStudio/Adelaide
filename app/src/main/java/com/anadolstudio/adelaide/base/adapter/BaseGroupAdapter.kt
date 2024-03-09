package com.anadolstudio.adelaide.base.adapter

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section

@Deprecated("Добавлено в core")
open class BaseGroupAdapter(vararg sections: Section) : GroupAdapter<GroupieViewHolder>() {

    init {
        sections.forEach { add(it) }
    }
}
